package cz.johrusk.showsmscode.service

import android.app.IntentService
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import org.jetbrains.anko.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*
import java.util.regex.Pattern


/**
 * Class which handle work around recognizing received SMS.
 * It launches all notification services
 *
 * @author Josef Hruska (pepa.hruska@gmail.com
 */
class MsgHandlerService : IntentService("MsgHandlerService"), AnkoLogger {

    override fun onHandleIntent(intent: Intent) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx)
        val sendNotification: Boolean = sharedPref.getBoolean("pref_notification", true)
        val msgArr = intent.getBundleExtra("msg").getStringArray("msg")

        try {
            val smsOnList: Array<String?>? = recognizeSms(msgArr[0], msgArr[1])
            warn(Arrays.toString(smsOnList))

            if (smsOnList != null) {

                var code: String? = ""
                debug("Unique text was recognized")

                val p = Pattern.compile(smsOnList[2])
                val matcher = p.matcher(msgArr[1])
                while (matcher.find()) { // Find each match in turn; String can't do this.
                    code = matcher.group(1) // Access a submatch group; String can't do this.
                    debug("code is: " + code)
                }
                if (code != "" && code != null) {
                    val type = "notifCode"
                    val bundle = Bundle()

                    bundle.putStringArray("key", arrayOf<String>(code, msgArr[0], smsOnList[1]!!, type))

                    if (sendNotification) startService(intentFor<NotificationService>("bundle" to bundle))  // It will start service for sending notification if its allowed in settings
                    startService(intentFor<WearService>("bundle" to bundle)) // It send a content which can be displayed on wear device.
                    startService(intentFor<ClipService>("code" to code)) // Starts IntentService which sets sms code to clipboard.

                    if ((Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(ctx) || Build.VERSION.SDK_INT < 23)) {
                        startService(intentFor<OverlayService>("bundle" to bundle)) // Starts service for showing a code on the screen
                        warn("Overlay intent started")
                    } else warn("Permission for overlay is not granted")
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    /**
     * This method checks whether both sms and version file is located in internal storage

     * @return true == both sms and version file is located in internal storage, yet. false == either sms or version isn't located in internal storage.
     */
    fun checkStorage(): Boolean {
        val SMS_FILE = "/sms.txt"
        val VERSION_FILE = "/version.txt"

        val INTERNAL_PATH_SMS = ctx.filesDir.path + SMS_FILE
        val INTERNAL_PATH_VERSION = ctx.filesDir.path + VERSION_FILE

        val version_file = File(INTERNAL_PATH_VERSION)
        val sms_file = File(INTERNAL_PATH_SMS)
        warn("Path of smsJSON: " + sms_file.absolutePath)
        warn("Path of versionJSON: " + version_file.absolutePath)

        if (sms_file.exists() && version_file.exists()) {
            return true
        } else {
            return false
        }
    }

    /**
     * This method loads sms file from internal storage
     */
    @Throws(JSONException::class)
    private fun loadJSONFromInternal(): String {
        var ret = ""
        val file = "sms.txt"
        try {
            val inputStream = ctx.openFileInput(file)

            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder = StringBuilder()
                bufferedReader.forEachLine {stringBuilder.append(it)}


                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
        }

        warn("readFrommFile return: " + ret)

        return ret
    }

    /**
     * This method loads sms file from internal storage.

     * @return sms file in form of String
     */
    fun loadJSONFromAsset(): String? {
        val json:String
        try {
            val iStream = ctx.assets.open("sms.json")
            val size = iStream.available()
            val buffer = ByteArray(size)
            iStream.read(buffer)
            iStream.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }

    /**
     * This method checks whether is unique text in sms
     * @param msg_content text of sms
     * *
     * @param results     Array with information in sms
     * *
     * @return true == "Unique text is contained in sms" false == "Unique text is not contained in sms"
     */
    fun containUnique(msg_content: String, results: Array<String?>): Boolean {

        val pUnique = Pattern.compile(results[0])
        val mUnique = pUnique.matcher(msg_content)
        warn(msg_content)

        if (mUnique.find()) {
            warn("Unique text is contained in sms")
            return true
        } else {
            warn("Unique text is not contained in sms")
            return false
        }
    }

    /**
     * This method removes the "Precode" (I have no idea what's the exact word for that) in number. f.e.: +420123456789 will be changed to 123456789
     * @param msg_sender_number original number
     * *
     * @return changed number
     */
    fun removePreCode(msg_sender_number: String): Long {
        val pattern = "\\+(\\d..)"
        val patt = Pattern.compile(pattern)
        val mat = patt.matcher(msg_sender_number)
        if (mat.find()) {
            val number: Long = java.lang.Long.valueOf(mat.replaceFirst(""))!!
            warn("edited number: " + number)
            return number
        }
        return java.lang.Long.valueOf(msg_sender_number)!!
    }

    /**
     * This method return false if the getDisplayOriginatingAddress is name of contact (f.e. "Notify";"Google Authenticator")
     * otherwise returns true which means it is a standard number (756453112)

     * @param msg_sender_number
     * *
     * @return
     */
    fun numberOrName(msg_sender_number: String): Boolean {

        val p = Pattern.compile("[^0123456789+]")
        warn(p.toString())
        val m = p.matcher(msg_sender_number)

        if (m.find()) {
            debug("Sender address is a name")
            return false
        } else {
            debug("Sender address is a number")
            return true
        }
    }

    /**
     * This method loads data from JSON DB and compares them with each sms.
     */
    @Throws(JSONException::class)
    fun recognizeSms(msg_sender_number: String, msg_content: String): Array<String?>? {
        val m_jArry: JSONArray
        val results = arrayOfNulls<String>(3)
        var name: String? = null
        var number: Long = 0
        var name_value: String? = null
        var number_value: Long = 0
        var alt_numbers: JSONArray

        val isNumber = numberOrName(msg_sender_number)
        if (!(isNumber)) {
            name = msg_sender_number
        } else {
            number = removePreCode(msg_sender_number)
        }

        if (checkStorage()) {
            m_jArry = JSONObject(loadJSONFromInternal()).getJSONArray("sms")
            debug("Internal source will be used. Length of JSONArray: " + m_jArry.length())
        } else {
            m_jArry = JSONObject(loadJSONFromAsset()).getJSONArray("sms")
            debug("Assets source will be used. Length of JSONArray: " + m_jArry!!.length())
        }

        for (i in 0..m_jArry.length() - 1) {
            val jo_inside = m_jArry!!.getJSONObject(i)
            warn("id = " + jo_inside.getString("id"))
            val id_value = jo_inside.getInt("id")
            if (id_value < 1000) {
                number_value = jo_inside.getLong("number")
            } else {
                name_value = jo_inside.getString("number")
            }
            val altnumbers_value = arrayOfNulls<String>(50)

            results[0] = jo_inside.getString("unique")
            results[1] = jo_inside.getString("sender")
            results[2] = jo_inside.getString("reg_ex")
            if (id_value < 1000 && !number.equals(0)) {
                if (number == number_value && containUnique(msg_content, results)) {
                    warn("number was recognized")
                    return results
                }
                if (jo_inside.has("alt_numbers")) {
                    alt_numbers = jo_inside.getJSONArray("alt_numbers")
                    for (x in 0..alt_numbers!!.length() - 1) {
                        altnumbers_value[x] = alt_numbers.getString(x)
                        if (number == java.lang.Long.valueOf(altnumbers_value[x]) && containUnique(msg_content, results)) {
                            return results
                        }
                    }
                }
            } else if (name != null) {
                if (name == name_value && containUnique(msg_content, results)) {
                    warn("number was recognized")
                    return results
                }
                if (jo_inside.has("alt_numbers")) {
                    alt_numbers = jo_inside.getJSONArray("alt_numbers")
                    for (x in 0..alt_numbers!!.length() - 1) {
                        altnumbers_value[x] = alt_numbers.getString(x)
                        if (name == altnumbers_value[x] && containUnique(msg_content, results)) {
                            return results
                        }
                    }
                }
            }
        }
        warn("number is not in DB")
        return null
    }

}
