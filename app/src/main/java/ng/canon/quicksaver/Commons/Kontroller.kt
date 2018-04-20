package ng.canon.quicksaver.Commons

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import java.io.IOException
import java.util.*
import android.util.Base64
import android.view.View
import java.io.InputStream
import java.util.regex.Pattern
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Patterns
import android.webkit.WebView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit






//SHOW SNACKBAR
fun snackUp(context: Context,message:String,view: View)
{
    val snacks = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snacks.view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
    snacks.show()
}




fun Checkmate(activity: Activity,context: Context){

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


    }else{


        // load permission request method here

    }

}








 fun Saveit(link:String):String{

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



fun extracTors(text: String): Array<String> {
    val links = ArrayList<String>()
    val m = Patterns.WEB_URL.matcher(text)
    while (m.find()) {
        val urls = m.group()
        links.add(urls)
    }

    return links.toTypedArray()
}













