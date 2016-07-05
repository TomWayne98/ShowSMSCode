package cz.johrusk.showsmscode.service

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.NotificationCompat
import cz.johrusk.showsmscode.R
import cz.johrusk.showsmscode.activity.MainActivity
import timber.log.Timber

/**
 * Service which sends notification which contain the code and number of sender.
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

class NotificationService : IntentService("NotificationService") {


    override fun onHandleIntent(intent: Intent) {
//        var dataArray = arrayOfNulls<String>(4)
        val bundle = intent.getBundleExtra("bundle")
        val  dataArray = bundle.getStringArray("key")
        val notifType = dataArray[3]

        var smsContent: String? = null
        var smsSender: String? = null

        var appIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageName))
        appIntent.addCategory(Intent.CATEGORY_DEFAULT)
        appIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        var nID = 0
        when (notifType) {
            "notifCode" -> {
                appIntent = Intent(this, MainActivity::class.java)
                smsContent = dataArray[0]
                smsSender = dataArray[2]
                nID = 1
            }
            else -> Timber.d("Switch statement didn't catch the case:" + notifType)
        }
        val startAppIntent = PendingIntent.getActivity(this, 0, appIntent, 0)


        val notifBuilder = NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_sms).setContentTitle(smsContent).setContentText(smsSender).setAutoCancel(true) as NotificationCompat.Builder

        if (notifType == "notifCode") {
            notifBuilder.setContentIntent(startAppIntent)
        }

        val notif = notifBuilder.build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(nID, notif)
    }
}