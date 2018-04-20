package ng.canon.quicksaver.BoxBraids


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.Observable.create
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.storified.view.*
import ng.canon.quicksaver.Commons.Saveit
import ng.canon.quicksaver.Commons.UserAdapter
import ng.canon.quicksaver.Commons.UserModel

import ng.canon.quicksaver.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 * A simple [Fragment] subclass.
 */
class Storified : Fragment(),SwipeRefreshLayout.OnRefreshListener {
    var observable: Observable<String>? = null
    var linkBox = ArrayList<String>()
    var userlist:ArrayList<UserModel>? = null
    var userx: UserAdapter? = null

    var swipes:SwipeRefreshLayout? = null
    var recyclerview:RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.storified, container, false)

        swipes = v.swipes
        recyclerview = v.recyclerview


        v.findwidget.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {


                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {

                scanUsers(query!!.replace("@","").replace(" ",""))
                return false
            }


        })

        stories()

        return v
    }





    override fun onRefresh() {

        stories()
    }

    fun scanUsers(userID:String)
    {

        swipes!!.isRefreshing = true
        swipes!!.visibility = View.VISIBLE

        userlist = ArrayList<UserModel>()
        recyclerview!!.adapter = null
        recyclerview!!.isNestedScrollingEnabled = true
        recyclerview!!.layoutManager =  LinearLayoutManager(activity!!.applicationContext, LinearLayoutManager.VERTICAL, false)

        observable = create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                try {

                    val instaUrl = "https://www.instagram.com/web/search/topsearch/?query=$userID&count=100"
                    val respond = Saveit(instaUrl)
                    val json = JSONObject(respond)
                    val users = json.getJSONArray("users")


                    for (i in 0..users.length() -1){

                        val jsonobj = users.getJSONObject(i)
                        val name = jsonobj.getJSONObject("user").getString("username")
                        val avatar = jsonobj.getJSONObject("user").getString("profile_pic_url")
                        val followers = jsonobj.getJSONObject("user").getString("byline")
                        var story = ""
                        if (jsonobj.getJSONObject("user").isNull("latest_reel_media")){
                            story = "loop"

                        }else{

                            story = "nurse"

                        }

                        userlist!!.add(UserModel(name,followers,story,avatar))
                    }



                    subscriber.onNext("")

                }catch (e:Exception){

                    subscriber.onError(e)
                }


                subscriber.onComplete()
            }
        })

        observable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onSubscribe(d: Disposable) {


                        swipes?.isRefreshing = true

                    }

                    override fun onComplete() {

                        userx = UserAdapter(activity!!.applicationContext, userlist!!)
                        recyclerview?.adapter = userx
                        userx!!.notifyDataSetChanged()
                        recyclerview?.visibility = View.VISIBLE
                        swipes?.isRefreshing = false

                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(activity!!.applicationContext,""+e.message, Toast.LENGTH_LONG).show()
                        swipes!!.isRefreshing = false
                    }

                    override fun onNext(response: String) {


                    }
                })

    }


    //method to preload instagram stories


    fun stories()
    {
        userlist = ArrayList<UserModel>()
        recyclerview!!.adapter = null
        recyclerview!!.isNestedScrollingEnabled = false
        recyclerview!!.layoutManager =  LinearLayoutManager(activity!!.applicationContext, LinearLayoutManager.VERTICAL, false)

        observable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                val response = readFile("stories.json")

                subscriber.onNext(response)
                subscriber.onComplete()

            }
        })

        observable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onSubscribe(d: Disposable) {


                    }

                    override fun onComplete() {


                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(activity!!.applicationContext,""+e.message, Toast.LENGTH_LONG).show()

                    }

                    override fun onNext(response: String) {
                        val json = JSONObject(response)
                        val jsonarr = json.getJSONArray("data")

                        for (i in 0..jsonarr.length() - 1) {

                            val jsonobj = jsonarr.getJSONObject(i)

                            val name = jsonobj.getString("name")
                            val links = jsonobj.getString("code")
                            val images = jsonobj.getString("images")

                            userlist!!.add(UserModel(name,"3.0m followers",links,images))

                        }

                        userx = UserAdapter(activity!!.applicationContext, userlist!!)
                        recyclerview!!.adapter = userx
                        userx!!.notifyDataSetChanged()


                    }
                })

    }







    //read json data from file



    @Throws(IOException::class)
    fun readFile(fileName: String): String {
        var reader: BufferedReader? = null
        reader = BufferedReader(InputStreamReader(activity!!.assets.open(fileName), "UTF-8"))

        var content = ""
        while (true) {
            var line: String? = reader.readLine() ?: break
            content += line

        }

        return content
    }

}
