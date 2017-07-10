package app.linksus.kotlindemo.kotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import app.linksus.kotlindemo.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_kotlin.*
import okhttp3.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onItemLongClick
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import java.io.IOException
import java.util.*


class KotlinActivity : AppCompatActivity() {


    var adapter: KotlinAdapter? = null
    var datas: ArrayList<DMJ>? = null
    var strs: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
        initData()
        initView()
        initClick()

    }

    fun initData() {
        datas = ArrayList()
        val mOkHttpClient = OkHttpClient()
        val request = Request.Builder()
                .url("http://gank.io/api/data/福利/30/1")
                .build()
        val call = mOkHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onResponse(call: Call?, response: Response?) {
                strs = response?.body()?.string()
                gsonFormat()

            }
        })

    }

    fun gsonFormat() {
        var jsonObject = JSONObject(strs)
        var array = jsonObject.getJSONArray("results")
        var objType = object : TypeToken<List<DMJ>>() {}
        var dats = Gson().fromJson<List<DMJ>>(array.toString(), objType.type)
        datas!!.addAll(dats as List<DMJ>)
        runOnUiThread {
            adapter?.notifyDataSetChanged()
        }

    }

    fun initView() {
        adapter = KotlinAdapter(this, datas!!)
        listView.adapter = adapter
    }

    fun initClick() {
        //点击换颜色
        btn.onClick {
            for (i in 0..datas!!.size - 1) {
                if (i % 2 == 0) {
                    datas!![i].url = "http://pic6.huitu.com/res/20130116/84481_20130116142820494200_1.jpg"
                }
            }
            adapter!!.notifyDataSetChanged()

        }
        //listview点击
        listView.setOnItemClickListener { parent, view, position, id ->
            startActivity<KotlinInfoActivity>("url" to datas!![position].url)
        }
        listView.onItemLongClick { p0, p1, p2, p3 ->
            Toast.makeText(this@KotlinActivity, "长按了", Toast.LENGTH_SHORT).show()
        }
    }



}
