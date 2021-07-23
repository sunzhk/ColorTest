package com.sunzk.base.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import com.sunzk.base.utils.StringUtils.isBlank
import java.io.Serializable
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 * Created by zhangxs on 2018/1/19.
 */
object DeviceUtils {
    private const val TAG = "DeviceUtils"
    fun getProperty(key: String?, defaultValue: String): String {
        var value = defaultValue
        try {
            val c = Class.forName("android.os.SystemProperties")
            val get = c.getMethod("get", String::class.java, String::class.java)
            value = get.invoke(c, key, "unknown") as String
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return value
        }
    }

    val deviceType: String
        get() = Build.MODEL
    val vendor: String
        get() = Build.MANUFACTURER
    val stbID: String
        get() = Build.SERIAL//		return Build.ID;

    /**
     * 不建议使用
     *
     * @return
     */
    @get:Deprecated("")
    val deviceId: String
        get() {
//		return Build.ID;
            val deviceId = getProperty("ro.serialno", "")
            return try {
                deviceId.substring(0, 32)
            } catch (e: Exception) {
                deviceId
            }
        }
    val deviceVersion: String
        get() = Build.VERSION.RELEASE
    val stbIpAddress: IpAndMac
        get() = getStbIpAddress(false)

    fun getStbIpAddress(useWifi: Boolean): IpAndMac {
        var iPandMac = getLocalMac("eth0", null)
        if (useWifi && isBlank(iPandMac.mac)) {
            //	使用wifi配置的话 当本地mac获取失败则取wifi的mac地址
            iPandMac = getLocalMac("wlan0", iPandMac)
        }
        iPandMac = getLocalIp("eth0", iPandMac)
        if (isBlank(iPandMac.ip)) {
            //	本地ip如果获取失败 则获取wifi的ip地址
            iPandMac = getLocalIp("wlan0", iPandMac)
        }
        return iPandMac
    }

    private fun getLocalMac(name: String, ipAndMac: IpAndMac?): IpAndMac {
        var ipAndMac = ipAndMac
        Log.d(TAG, "getLocalIp name:$name")
        if (ipAndMac == null) {
            ipAndMac = IpAndMac()
        }
        try {
            Log.d(TAG, "all interface:" + NetworkInterface.getNetworkInterfaces().toString())
            val nilist = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (ni in nilist) {
                Log.d(TAG, "network inferface:$ni")
                if (ni.name == name) {
                    ipAndMac.mac = byte2hex(ni.hardwareAddress)
                    Log.d(TAG, " mac:" + ipAndMac.mac)
                    break
                }
            }
        } catch (ex: SocketException) {
            Log.e("localip", ex.toString())
        }
        return ipAndMac
    }

    private fun getLocalIp(name: String, ipAndMac: IpAndMac): IpAndMac {
        var ipAndMac: IpAndMac? = ipAndMac
        Log.d(TAG, "getLocalIpAddress name:$name")
        if (ipAndMac == null) {
            ipAndMac = IpAndMac()
        }
        try {
            Log.d(TAG, "all interface:" + NetworkInterface.getNetworkInterfaces().toString())
            val nilist = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (ni in nilist) {
                Log.d(TAG, "network inferface:$ni")
                if (ni.name == name) {
                    val ialist = Collections.list(ni.inetAddresses)
                    for (address in ialist) {
                        if (!address.isLoopbackAddress && address is Inet4Address) {
                            ipAndMac.ip = address.getHostAddress()
                            Log.d(TAG, " ip:" + ipAndMac.ip)
                            break
                        }
                    }
                }
            }
        } catch (ex: SocketException) {
            Log.e("localip", ex.toString())
        }
        return ipAndMac
    }

    private fun getLocalIpAddress(name: String): IpAndMac {
        Log.d(TAG, "getLocalIpAddress name:$name")
        val iPandMac = IpAndMac()
        try {
            var ipv4: String
            Log.d(TAG, "all interface:" + NetworkInterface.getNetworkInterfaces().toString())
            val nilist = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (ni in nilist) {
                Log.d(TAG, "network inferface:$ni")
                if (ni.name == name) {
                    val ialist = Collections.list(ni.inetAddresses)
                    iPandMac.mac = byte2hex(ni.hardwareAddress)
                    for (address in ialist) {
                        if (!address.isLoopbackAddress && address is Inet4Address) {
                            iPandMac.ip = address.getHostAddress()
                            Log.d(TAG, "mac:" + iPandMac.mac + " ip:" + iPandMac.ip)
                            return iPandMac
                        }
                    }
                }
            }
        } catch (ex: SocketException) {
            Log.e("localip", ex.toString())
        }
        return iPandMac
    }

    val isNetworkIsOk: Boolean
        get() {
            val iPandMac = stbIpAddress ?: return false
            return if ("0:0:0:0" == iPandMac.ip) {
                false
            } else true
        }

    fun byte2hex(b: ByteArray): String {
        var hs = StringBuffer(b.size)
        var stmp = ""
        val len = b.size
        for (n in 0 until len) {
            stmp = Integer.toHexString(b[n].toInt() and 0xFF)
            hs = if (stmp.length == 1) hs.append("0").append(stmp) else {
                hs.append(stmp)
            }
            hs.append(":")
        }
        return hs.substring(0, hs.length - 1).toString()
    }

    /**
     * 获得IP地址，分为两种情况，一是wifi下，二是移动网络下，得到的ip地址是不一样的
     */
    fun getIPAddress(context: Context): IpAndMac {
        val iPandMac = IpAndMac()
        val info =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (info != null && info.isConnected) {
            if (info.type == ConnectivityManager.TYPE_MOBILE || info.type == ConnectivityManager.TYPE_ETHERNET) { //当前使用2G/3G/4G网络 ，有线网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    val en = NetworkInterface.getNetworkInterfaces()
                    while (en.hasMoreElements()) {
                        val intf = en.nextElement()
                        val enumIpAddr = intf.inetAddresses
                        while (enumIpAddr.hasMoreElements()) {
                            val inetAddress = enumIpAddr.nextElement()
                            if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                                iPandMac.mac = byte2hex(intf.hardwareAddress)
                                iPandMac.ip = inetAddress.getHostAddress()
                                return iPandMac
                            }
                        }
                    }
                } catch (e: SocketException) {
                    e.printStackTrace()
                }
            } else if (info.type == ConnectivityManager.TYPE_WIFI) { //当前使用无线网络
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                //调用方法将int转换为地址字符串
                val ipAddress = intIP2StringIP(wifiInfo.ipAddress) //得到IPV4地址
                iPandMac.mac = wifiInfo.macAddress
                iPandMac.ip = ipAddress
                return iPandMac
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return iPandMac
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    fun intIP2StringIP(ip: Int): String {
        return (ip and 0xFF).toString() + "." + (ip shr 8 and 0xFF) + "." + (ip shr 16 and 0xFF) + "." + (ip shr 24 and 0xFF)
    }

    class IpAndMac : Serializable {
        var ip: String? = null
        var mac: String? = null
    }
}