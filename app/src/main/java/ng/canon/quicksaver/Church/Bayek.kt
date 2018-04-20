package ng.canon.quicksaver.Church

import android.app.*
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.content.*
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.os.Build
import io.reactivex.Observable
import ng.canon.quicksaver.Intel.Core
import ng.canon.quicksaver.R
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class Bayek : Service() {
    var observable: Observable<String>? = null
    var linkBox:ArrayList<String>? = null
    val clipBox = ArrayList<String>()
    companion object {
        val ACTION_PING = Bayek::class.java.name + ".PING"
        val ACTION_PONG = Bayek::class.java.name + ".PONG"
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, IntentFilter(ACTION_PING));
        notifySystem()

        val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.addPrimaryClipChangedListener {
            val currentText = clipboard.primaryClip.getItemAt(0).text.toString()
            if (currentText.contains("https://www.instagram.com/p/") || currentText.contains("https://www.instagram.com/v/")){

                val id = currentText.replace("https://www.instagram.com/p/","")
                if (!clipBox.contains(id)){



                   val dialogIntent =  Intent(this, Core::class.java);
                    dialogIntent.putExtra("videoID",id)
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent)



                }


            }else{


            }

        }

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy()
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_PING) {
                val manager = LocalBroadcastManager.getInstance(applicationContext)
                manager.sendBroadcast(Intent(ACTION_PONG))
            }
        }
    }




    fun notifySystem(){

        val title = getString(R.string.note_title)
        val texts = getString(R.string.note_text)




        var builder:Notification.Builder? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val ChannelId = "ng.canon.quicksaver";
        val ChannelName = "Quciksaver";
        val channel = NotificationChannel(ChannelId, ChannelName, NotificationManager.IMPORTANCE_LOW);
        val mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            mNotificationManager.createNotificationChannel(channel)
            builder = Notification.Builder(this, ChannelId)
        } else {
             builder = Notification.Builder(this)

        }

                builder!!.setContentTitle(title)
                .setContentText(texts)
                .setSmallIcon(R.drawable.ic_notif)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)

        val notification = builder.build()

        startForeground(110, notification)
    }





    //Downloader



    fun SaveDit(link:String):String{

        var pink = ""
        val saveclient = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build()
        val saverequest = Request.Builder()
                .url(link)
                .build()
        val response = saveclient.newCall(saverequest).execute()


        val json = JSONObject(response.body()!!.string())


        return json.toString()
    }


}
