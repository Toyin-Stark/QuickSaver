package ng.canon.quicksaver.BoxBraids


import android.content.*
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import com.github.angads25.toggle.LabeledSwitch
import com.github.angads25.toggle.interfaces.OnToggledListener
import kotlinx.android.synthetic.main.insta.view.*
import ng.canon.quicksaver.Church.Bayek
import ng.canon.quicksaver.GlideApp

import ng.canon.quicksaver.R


/**
 * A simple [Fragment] subclass.
 */
class Insta : Fragment() {
    private var isSvcRunning = false


    var switch: LabeledSwitch? = null
    var logo:ImageView? = null
    var toolz:Toolbar? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.insta, container, false)

        switch = v.switches
        logo = v.logo_view
        toolz = v.toolz

        toolz!!.title = getString(R.string.app_name)
        toolz!!.setTitleTextColor(ContextCompat.getColor(activity!!.applicationContext,android.R.color.white))
        switch!!.setOnToggledListener(object: OnToggledListener {
            override fun onSwitched(labeledSwitch: LabeledSwitch?, isOn: Boolean) {

                val box = PreferenceManager.getDefaultSharedPreferences(activity)

                if (isOn){

                    GlideApp.with(activity!!.applicationContext).load(R.drawable.ic_ball).into(logo!!)
                    box.edit().putBoolean("locked", true).apply()
                    Genesis()

                }else{


                    GlideApp.with(activity!!.applicationContext).load(R.drawable.ic_ball_f).into(logo!!)
                    box.edit().putBoolean("locked", false).apply()
                    Revelation()


                }
            }


        })



        v.facer.setOnClickListener {

            callInstagram(activity!!.applicationContext,"com.instagram.android")

        }


        v.tuber.setOnClickListener {

            watchYoutubeVideo("aTkzQLwVawE")

        }

        return v
    }



    fun Genesis(){

        val intu = Intent(activity!!.applicationContext, Bayek::class.java)
        ContextCompat.startForegroundService(activity!!.applicationContext,intu)
    }



    fun Revelation(){

        val intu = Intent(activity!!.applicationContext,Bayek::class.java)
        activity!!.stopService(intu)
    }



    override fun onResume() {
        val manager = LocalBroadcastManager.getInstance(activity!!.applicationContext)
        manager.registerReceiver(mReceiver, IntentFilter(Bayek.ACTION_PONG))
        // the service will respond to this broadcast only if it's running
        manager.sendBroadcast(Intent(Bayek.ACTION_PING))
        super.onResume()
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(activity!!.applicationContext).unregisterReceiver(mReceiver);
        super.onStop()
    }


    protected var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // here you receive the response from the service
            if (intent.action == Bayek.ACTION_PONG) {
                isSvcRunning = true
                watchTower()
                GlideApp.with(activity!!.applicationContext).load(R.drawable.ic_ball).into(logo!!)

            }
        }
    }



    fun watchTower(){

        switch!!.isOn = isSvcRunning

    }



    private fun callInstagram(context: Context, packageN: String) {
        val apppackage = packageN
        try {
            val i = context.packageManager.getLaunchIntentForPackage(apppackage)
            context.startActivity(i)
            activity!!.finish()
        } catch (e: Exception) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageN)))
        }

    }


    fun watchYoutubeVideo(id: String) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id))
        val webIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id))
        try {
            startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            startActivity(webIntent)
        }

    }


}// Required empty public constructor
