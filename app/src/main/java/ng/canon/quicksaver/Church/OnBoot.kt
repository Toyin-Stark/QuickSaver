package ng.canon.quicksaver.Church

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat


class OnBoot : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val serviceIntent = Intent(context, Bayek::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
