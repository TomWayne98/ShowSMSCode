package cz.johrusk.showsmscode

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.vibrator
import org.jetbrains.anko.warn

/**
 * Activity which  code and sender of received SMS.

 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

class ShowActivity : Activity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vibrationPattern = longArrayOf(0, 500, 50, 300)
        //-1 - don't repeat
        val indexInPatternToRepeat = -1
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat)

        warn("ShowActivity")
        val sArray = intent.getStringExtra("code")
        val parts = sArray.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        warn("Code / Sender: " + parts[0] + " / " + parts[1])
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

        watch_view_stub.setOnClickListener { finish() }
        watch_view_stub.setOnLayoutInflatedListener { stub ->
            val tv_code = stub.findViewById(R.id.text) as TextView //TODO: how to use Kotlin with ViewStub?
            tv_code.text = parts[0]
            val tv_sender = stub.findViewById(R.id.sender) as TextView
            tv_sender.text = parts[1]
        }
    }

    override fun onResume() {
        super.onResume()
        warn("OnResume")
    }

    override fun onRestart() {
        super.onRestart()
        warn("OnRestart")
    }
}