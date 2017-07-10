package app.linksus.kotlindemo.kotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import app.linksus.kotlindemo.R
import com.bumptech.glide.Glide

/**
 * Created by linksus on 2017/7/6.
 */
class KotlinAdapter(context: Context, datas: ArrayList<DMJ>) : BaseAdapter() {
    var mcontext: Context? = null
    var mdats: ArrayList<DMJ>? = null

    init {
        mcontext = context
        mdats = datas
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: ViewHolder
        var convertView = convertView
        if (convertView == null) {
            holder = ViewHolder();
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.item, null);
            holder.img = convertView.findViewById(R.id.img) as ImageView
            convertView.setTag(holder)
        } else {
            holder = convertView.tag as ViewHolder
        }
        Glide.with(mcontext).load(mdats!![position].url).into(holder.img)
        holder.text?.text=mdats!![position].who
        return convertView!!

    }

    override fun getItem(position: Int): Any {
        return mdats!!.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mdats!!.size
    }

    class ViewHolder {
        var img: ImageView? = null
        var text: TextView? = null

    }
}