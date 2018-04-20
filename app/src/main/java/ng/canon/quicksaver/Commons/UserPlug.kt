package ng.canon.quicksaver.Commons

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.profile_lego.view.*
import ng.canon.quicksaver.GlideApp
import ng.canon.quicksaver.R
import ng.canon.quicksaver.Stories.StoryView

data class UserModel(val title:String, val followers:String,val story:String ,val imagelink:String)
class UserAdapter(var c: Context, var lists: ArrayList<UserModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v = LayoutInflater.from(c).inflate(R.layout.user_row, parent, false)
        return Item(v)
    }


    override fun getItemCount(): Int {

        return lists.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Item).bindData(lists[position])

    }

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(_list: UserModel) {

            GlideApp.with(itemView.context)
                    .asBitmap()
                    .dontAnimate()
                    .load(_list.imagelink)
                    .into(object : SimpleTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                            itemView.profile_image.setImageBitmap(resource)

                        }


                    })


            itemView.username.text = _list.title
            itemView.followers.text = _list.followers

            if (!_list.story.contains("nurse")){

                itemView.checked.setImageResource(R.drawable.ic_cancel)
            }else{

                itemView.checked.setImageResource(R.drawable.ic_checked)

            }

            itemView.setOnClickListener {

                val ontent = Intent(itemView.context, StoryView::class.java)
                ontent.putExtra("storyID",_list.title)
                ontent.putExtra("imageID",_list.imagelink)
                ontent.putExtra("followID",_list.followers)
                ontent.putExtra("nameID",_list.title)

                itemView.context.startActivity(ontent)

            }
        }
    }
}