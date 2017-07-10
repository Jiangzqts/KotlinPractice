package app.linksus.kotlindemo.kotlin

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import app.linksus.kotlindemo.R
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.imageResource
import java.lang.reflect.Field
import java.util.concurrent.CopyOnWriteArrayList
import java.util.regex.Pattern
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by linksus on 2017/7/4.
 */

//model
data class DMJ(var _id: String, var url: String, var who: String)

/**
 * Preference代理
 */

class Preference<T>(var context: Context, var name: String, val default: T) : ReadWriteProperty<Any?, T> {
    val prefs by lazy { context.getSharedPreferences("default", Context.MODE_PRIVATE) }
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(name, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    private fun <U> findPreference(name: String, default: U): U = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type can not be saved")
        }
        res as U
    }

    private fun <U> putPreference(name: String, value: U) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can not be saved")
        }.apply()
    }
}


/**
 * 扩展图片加载
 */
fun ImageView.setImageBG(url: String) {
    if (url == null || url.isEmpty() || TextUtils.isEmpty(url)) {
        imageResource = R.mipmap.ic_launcher
    } else {
        Glide.with(context)
                .load(url)
                .into(this)
    }
}


/**
 * 扩展函数toast
 */
fun Context.showToast(str: String) {
    Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
}


/**
 * 单利管理所有activity生命周期
 */
object AppManager {
    private var activityStack: CopyOnWriteArrayList<Activity>? = null
    private var instance: AppManager? = null
    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {
        if (activityStack == null) {
            activityStack = CopyOnWriteArrayList<Activity>()
        }
        activityStack!!.add(activity)
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity?) {
        var activity = activity
        if (activity != null) {
            activityStack!!.remove(activity)
            activity.finish()
            activity = null
        }
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>?) {
        if (cls == null) return
        for (activity in activityStack!!) {
            if (activity.javaClass == cls) {
                finishActivity(activity)
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        var i = 0
        val size = activityStack!!.size
        while (i < size) {
            if (null != activityStack!![i]) {
                activityStack!![i].finish()
            }
            i++
        }
        activityStack!!.clear()
    }

    /**
     * 结束所有Activity
     */
    fun finishSpcActivity() {
        var i = 0
        val size = activityStack!!.size
        while (i < size) {
            if (null != activityStack!![i]) {
                activityStack!![i].finish()
                println("结束所有Activity = " + activityStack!![i])
            }
            i++
        }
        activityStack!!.clear()
    }

    /**
     * 退出应用程序
     */
    fun AppExit(context: Context) {
        try {
            finishAllActivity()
            val activityMgr = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityMgr.restartPackage(context.packageName)
            System.exit(0)
        } catch (e: Exception) {
        }

    }


    /**
     * 获取手机屏幕尺寸

     * @param act
     * *
     * @return
     */
    fun getDisplaySize(act: Activity): DisplayMetrics {
        val dm = DisplayMetrics()
        act.windowManager.defaultDisplay.getMetrics(dm)
        return dm

    }

    /**
     * 判断是否是图片
     */

    fun isImg(str: String): Boolean {
        if (str.contains(".")) {
            if (str.substring(str.lastIndexOf(".")) == ".jpg")
                return true
            if (str.substring(str.lastIndexOf(".")) == ".png")
                return true
            if (str.substring(str.lastIndexOf(".")) == ".jpeg")
                return true
        }
        return false
    }

    /**
     * 获取手机屏幕宽度

     * @param context
     * *
     * @return
     */
    fun getWindowWidth(context: Context): Int {
        val wm = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = wm.defaultDisplay.width
        val height = wm.defaultDisplay.height
        return width
    }

    /**
     * 获取手机状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        var c: Class<*>? = null
        var obj: Any? = null
        var field: Field? = null
        var x = 0
        var statusBarHeight = 0
        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c!!.newInstance()
            field = c.getField("status_bar_height")
            x = Integer.parseInt(field!!.get(obj).toString())
            statusBarHeight = context.resources.getDimensionPixelSize(x)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        return statusBarHeight
    }

    private val TAG = "NetUtil"

    /**
     * 网络连接是否可用
     */
    fun isNetworkAvailable(ctx: Context): Boolean {
        val connectivity = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity == null) {
            return false
        } else {
            val info = connectivity.allNetworkInfo
            if (info != null) {
                for (i in info.indices) {
                    if (info[i].state == NetworkInfo.State.CONNECTED || info[i].state == NetworkInfo.State.CONNECTING) {
                        return true
                    }
                }
            }
        }
        return false
    }


    /**
     * 检查网络类型

     * @return 0 表示网络错误  1表示手机网络 2表示wifi
     */
    fun checkNetworkType(ctx: Context): Int {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager ?: return 0
        val mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state
        val wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state

        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
            println("手机网络")
            return 1
        }
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            println("WIFI")
            return 2
        }
        return 0
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * wifi网络
     */
    fun isWifiNetwork(ctx: Context): Boolean {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        if (networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI) {
            return true
        }
        return false
    }

    /**
     * 检测手机号是否正确
     */
    fun checkPhone(mobiles: String): Boolean {
        val p = Pattern
                .compile("^((14[5,7])|(13[0-9])|(17[0-9])|(15[^4,\\D])|(18[0,1-9]))\\d{8}$")
        val m = p.matcher(mobiles)
        return m.matches()
    }

    /**
     * 检测密码格式
     */
    fun checkPassword(password: String): Boolean {
        val p = Pattern.compile("^[0-9a-zA-Z_]{6,20}")
        val m = p.matcher(password)
        return m.matches()
    }

    /**
     * 检查密码是由数字或字母组成
     */
    fun checkRegisterPassword(pw: String): Boolean {
        var isLetter = false
        var isDigit = false
        for (i in 0..pw.length - 1) {
            if (Character.isDigit(pw[i])) {
                isDigit = true
            }
            if (Character.isLetter(pw[i])) {
                isLetter = true
            }
            if (!isDigit && !isLetter) {
                return false
            }
        }
        return true
    }

    /**
     * 判断邮箱格式
     */
    fun checkEmail(email: String): Boolean {
        val p = Pattern
                .compile("^\\s*([A-Za-z0-9_-]+(\\.\\w+)*@(\\w+\\.)+\\w{2,5})\\s*$")
        val m = p.matcher(email)
        return m.matches()
    }

    /**
     * 跳转去网络设置页面
     */
    fun startNetworkSettingActivity(ctx: Context) {
        var intent = Intent()
        val sdkVersion = Build.VERSION.SDK_INT
        if (sdkVersion >= 14) {
            intent = Intent(Settings.ACTION_SETTINGS)
        } else {
            // android4.0系统找不到此Activity
            intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(intent)
    }

    /**
     * 获得当前进程的名字

     * @param context context
     * *
     * @return 进程号
     */
    fun getCurProcessName(context: Context): String? {
        val pid = android.os.Process.myPid()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (appProcess in activityManager.runningAppProcesses) {
            if (appProcess.pid == pid) {
                return appProcess.processName
            }
        }
        return null
    }

    /**
     * gson 解析
     */
    fun setObjectToJSON(obj: Any): String {
        val gson = Gson()
        return gson.toJson(obj)
    }

    fun <T> getObejctFromJSON(jsonStr: String, cls: Class<T>): T {
        val gson = Gson()
        val t = gson.fromJson(jsonStr, cls)
        return t
    }

    fun getListObjectFromJSON(jsonStr: String, typeToken: TypeToken<*>): List<*> {
        val gson = Gson()
        val list = gson.fromJson<List<*>>(jsonStr, typeToken.type)
        return list
    }

    fun <T> getListMapFromJSON(jsonStr: String, cls: Class<T>): List<Map<String, Any>> {
        return Gson().fromJson<List<Map<String, Any>>>(jsonStr,
                object : TypeToken<List<Map<String, Any>>>() {}.type)
    }

}








