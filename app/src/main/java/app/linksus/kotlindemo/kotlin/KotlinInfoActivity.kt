package app.linksus.kotlindemo.kotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import app.linksus.kotlindemo.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_kotlin_info.*

class KotlinInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_info)
        initData()

    }
    fun initData(){
        var url=intent.getStringExtra("url")
        Glide.with(this).load(url).into(info_img)
    }
}
