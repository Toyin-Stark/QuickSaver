package ng.canon.quicksaver.Intel

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.downloader.*
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.shashank.sony.fancygifdialoglib.FancyGifDialog
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.core.*
import kotlinx.android.synthetic.main.jars.*
import ng.canon.quicksaver.R
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class Core : AppCompatActivity() {

    var linkBox: ArrayList<String>? = null
    val clipBox = ArrayList<String>()
    var observable: Observable<String>? = null
    var mu: Array<String>? = null
    var version = ""
    var Primaryresponse: Response? = null
    var showing = false
    var finalURL = ""
    var track = ""
    var topic = ""
    var pass = ""
    var app_version = "1470648201"
    var tlc = ""
    var finalUrl = ""
    var file_url = ""
    var counter = 0
    var videoID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core)

        val config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build()
        PRDownloader.initialize(applicationContext, config)

        videoID = intent.getStringExtra("videoID")

        looku()
    }


    fun looku(){


        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            cardy.visibility = View.VISIBLE
            progressx.isIndeterminate = true
            linkCore(videoID)


        }else{
            runOnUiThread {

                lasma()

            }
        }

    }

    fun linkCore(videoID: String) {
        linkBox = ArrayList<String>()
        observable = Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                try {

                    val start = "https://www.instagram.com/p/$videoID"
                    val end = "?__a=1"
                    val instaUrl = start + end
                    val respond = SaveDit(instaUrl)
                    val json = JSONObject(respond)
                    val type = json.getJSONObject("graphql").getJSONObject("shortcode_media").getString("__typename")

                    if (type.contains("GraphSidecar")) {

                        val mediaBox = json.getJSONObject("graphql").getJSONObject("shortcode_media").getJSONObject("edge_sidecar_to_children").getJSONArray("edges")

                        for (i in 0..mediaBox.length() - 1) {

                            val jsonobj = mediaBox.getJSONObject(i)
                            val genre = jsonobj.getJSONObject("node").getString("__typename")
                            if (genre.contains("GraphVideo")) {

                                val videoURL = jsonobj.getJSONObject("node").getString("video_url")
                                val viewURL = jsonobj.getJSONObject("node").getString("display_url")

                                linkBox!!.add(videoURL)
                            } else {

                                val photoURL = jsonobj.getJSONObject("node").getString("display_url")
                                linkBox!!.add(photoURL)

                            }
                        }

                    }


                    if (type.contains("GraphImage")) {

                        val imageLink = json.getJSONObject("graphql").getJSONObject("shortcode_media").getString("display_url")
                        linkBox!!.add(imageLink)

                    }


                    if (type.contains("GraphVideo")) {


                        val videoLink = json.getJSONObject("graphql").getJSONObject("shortcode_media").getString("video_url")
                        val photoLink = json.getJSONObject("graphql").getJSONObject("shortcode_media").getString("display_url")

                        linkBox!!.add(videoLink)


                    }



                    subscriber.onNext("")

                } catch (e: Exception) {

                    subscriber.onError(e)
                }


                subscriber.onComplete()
            }
        })

        observable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    override fun onSubscribe(d: Disposable) {


                    }

                    override fun onComplete() {

                        counter = 0
                        mrSave()



                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(applicationContext, "" + e.message, Toast.LENGTH_LONG).show()

                    }

                    override fun onNext(response: String) {


                    }
                })

    }


    fun SaveDit(link: String): String {

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


    fun mrSave() {

        progressx.isIndeterminate = false
        progressx.progress = 0
        var extension = ""
        val urld = linkBox!![counter]

        if(urld.contains(".jpg")){
            extension = "jpg"
        }

        if(urld.contains(".png")){
            extension = "png"
        }


        if(urld.contains(".gif")){
            extension = "gif"
        }


        if (urld.contains(".mp4")){

            extension = "mp4"
        }
        val sizes = linkBox!!.size
        count.text = "$counter /  $sizes"
        var desc = getString(R.string.bannerTitle)
        val timeStamp = System.currentTimeMillis()
        val name = "insta_$timeStamp.$extension"
        val dex = File(Environment.getExternalStorageDirectory().absolutePath, "quicksave")
        if (!dex.exists())
            dex.mkdirs()

        val filed = File(dex, name)


        val downloadId = PRDownloader.download(urld, dex.absolutePath, name)
                .build()
                .setOnProgressListener(object : OnProgressListener {
                    override fun onProgress(progress: Progress?) {
                        val progressPercent = progress!!.currentBytes * 100 / progress.totalBytes
                        val percents = progressPercent.toInt()
                        val papi = percents.toString()
                        progressive.text = "${papi}%"
                        progressx.progress = percents


                    }


                }).start(object : OnDownloadListener {
            override fun onError(error: Error?) {


            }

            override fun onDownloadComplete() {
                counter += 1
                count.text = "$counter /  $sizes"

                MediaScannerConnection.scanFile(applicationContext, arrayOf(filed.absolutePath), null) { path, uri ->

                    if (counter != linkBox!!.size){

                        runOnUiThread {
                            mrSave()
                        }
                    }else{
                        finish()


                    }

                }


            }


        })


    }










    fun lasma(){
        val request = permissionsBuilder(Manifest.permission.WRITE_EXTERNAL_STORAGE).build()
        request.send()
        request.listeners {

            onAccepted { permissions ->

                looku()

            }

            onDenied { permissions ->

                permissionDialog()
            }

            onPermanentlyDenied { permissions ->
                permissionDialog()

            }

            onShouldShowRationale { permissions, nonce ->
                permissionDialog()

            }
        }
        // load permission methods here
    }





    fun permissionDialog(){


        runOnUiThread {
            FancyGifDialog.Builder(this@Core)
                    .setTitle(getString(R.string.permissionTitle))
                    .setMessage(getString(R.string.permissionMessage))
                    .setNegativeBtnText(getString(R.string.permissionNegative))
                    .setPositiveBtnBackground("#FF4081")
                    .setPositiveBtnText(getString(R.string.permissionPositive))
                    .setNegativeBtnBackground("#FFA9A7A8")
                    .setGifResource(R.drawable.permit)   //Pass your Gif here
                    .isCancellable(false)
                    .OnPositiveClicked(object : FancyGifDialogListener {
                        override fun OnClick() {

                            lasma()


                        }


                    })

                    .OnNegativeClicked(object : FancyGifDialogListener {
                        override fun OnClick() {

                            Toast.makeText(this@Core,""+getString(R.string.permissionMessage),Toast.LENGTH_LONG).show()
                            finish()

                        }


                    })
                    .build()
        }


    }

}
