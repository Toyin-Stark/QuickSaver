package ng.canon.quicksaver.StoryCam

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import kotlinx.android.synthetic.main.story_photo_viewer.*
import ng.canon.quicksaver.GlideApp
import ng.canon.quicksaver.R
import java.io.File



class PhotoPlayer : AppCompatActivity() {

    var file:File? = null

    var bitty:Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.story_photo_viewer)

        val ints = intent.extras
        val photoID = ints.getString("imageID")
        GlideApp.with(this@PhotoPlayer).load(photoID).into(display)

    }




}
