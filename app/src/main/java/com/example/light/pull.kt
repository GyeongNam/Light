package com.example.light

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.SurfaceView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.core.Point
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStream
import kotlin.concurrent.thread


@Suppress("DEPRECATION")
class pull : AppCompatActivity() , CvCameraViewListener2 {
    external fun ConvertRGBtoGray(nativeObjAddr: Long, nativeObjAddr1: Long)
    external fun stringFromJNI(): String
    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
            System.loadLibrary("opencv_java3");
        }
    }

    private val multiplePermissionsCode = 100
    //필요한 퍼미션 리스트
    //원하는 퍼미션을 이곳에 추가하면 된다.
    private val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA
    )
    private fun checkPermissions() {
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var rejectedPermissionList = ArrayList<String>()
        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for(permission in requiredPermissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(
                this,
                rejectedPermissionList.toArray(array),
                multiplePermissionsCode
            )
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            multiplePermissionsCode -> {
                if (grantResults.isNotEmpty()) {
                    for ((i, permission) in permissions.withIndex()) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i("TAG", "The user has denied to $permission")
                            Log.i("TAG", "I can't work for you anymore then. ByeBye!")
                            finish()
                        }
                    }
                }
            }
        }
    }



    private val TAG = "opencv"
    private var mOpenCvCameraView: CameraBridgeViewBase? = null
    private var matInput: Mat? = null
    private var matResult: Mat? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pull)

        checkPermissions()
        val but_pull : Button = findViewById(R.id.button4)
        val but_pull2 : Button = findViewById(R.id.button5)
        val scrollView : View = findViewById(R.id.ScrollView)
        scrollView.scrollTo(0, 0);

        val msg : TextView = findViewById(R.id.msgtext)
        msg.setText("Click on the \"Analysis Start\" button.")
        but_pull2.setVisibility(GONE);
        var text1 : TextView = findViewById(R.id.textView2)
        var text2 : TextView = findViewById(R.id.textView3)
        var text3 : TextView = findViewById(R.id.textView4)
        var text4 : TextView = findViewById(R.id.textView5)

        mOpenCvCameraView = findViewById(R.id.fd_activity_surface_view)

        var widths = getWindowManager().getDefaultDisplay().width;
        text1.setWidth(widths)
        text2.setWidth(widths)
        text3.setWidth(widths)
        text4.setWidth(widths)

        val mOpenCvCameraView2 = mOpenCvCameraView
        if (mOpenCvCameraView2 != null) {
            mOpenCvCameraView2.setVisibility(SurfaceView.VISIBLE)
        }
        if (mOpenCvCameraView2 != null) {
            mOpenCvCameraView2.setCvCameraViewListener(this)
        }
        if (mOpenCvCameraView2 != null) {
            mOpenCvCameraView2.setCameraIndex(0)
        }
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        var click = true
        var Mstring = ""
        var restring = ""

        but_pull2.setOnClickListener(){
            msg.setText("Scroll left and right to find the right analysis")
            but_pull.setVisibility(VISIBLE)
            but_pull2.setVisibility(GONE);
            click = false
            restring = Mstring
            var longtmp1 = ""
            var longtmp2 = ""
            var longtmp3 = ""
            var longtmp4 = ""
            var longtmp5 = ""
            var longtmp33 = ""
            var longtmp44 = ""
            var longtmp55 = ""
            var zcout = 0
            var cout = 0

            Mstring = Mstring.replace("10101", "11111")
            Mstring = Mstring.replace("101", "111")
            Mstring = Mstring.replace("110011", "111111")
            Mstring = Mstring.replace("010", "000")

            for(i in Mstring.indices){
                if(Mstring[i] != '0'){
                    Mstring = Mstring.substring(i)
                    break
                }
            }

            for(i in Mstring.indices) {
                if(Mstring[i] == '1') {
                    zcout = 0
                    cout++
                }
                else if (Mstring[i] == '0'){
                    if (cout > 17){
                        longtmp1 += "2"
                    }
                    else if(cout > 7){
                        longtmp1 += "1"
                    }
                    cout = 0
                    zcout++
                    if (zcout > 14){
                        longtmp1 += "0"
                        zcout = 0
                    }
                }
            }
            if(longtmp1.length > 0){
                longtmp3 = longtmp1.substring(1)
                Log.e("2번째 값" ,longtmp3 )
            }
            if(longtmp1.length > 1){
                longtmp4 = longtmp1.substring(2)
                Log.e("3번째 값" ,longtmp4 )
            }
            if(longtmp1.length > 2){
                longtmp5 = longtmp1.substring(3)
                Log.e("4번째 값" ,longtmp5 )
            }

            for(i in longtmp3.indices){
                if(longtmp3[i] != '0'){
                    longtmp3 = longtmp3.substring(i)
                    break
                }
            }
            for(i in longtmp4.indices){
                if(longtmp4[i] != '0'){
                    longtmp4 = longtmp4.substring(i)
                    break
                }
            }
            for(i in longtmp5.indices){
                if(longtmp5[i] != '0'){
                    longtmp5 = longtmp5.substring(i)
                    break
                }
            }

            for(i in longtmp1.indices){
                if(i % 4 ==0) {
                    longtmp2 += ","
                }
                longtmp2 += longtmp1[i]
            }

            for(i in longtmp3.indices){
                if(i % 4 ==0) {
                    longtmp33 += ","
                }
                longtmp33 += longtmp3[i]
            }

            for(i in longtmp4.indices){
                if(i % 4 ==0) {
                    longtmp44 += ","
                }
                longtmp44 += longtmp4[i]
            }
            for(i in longtmp5.indices){
                if(i % 4 ==0) {
                    longtmp55 += ","
                }
                longtmp55 += longtmp5[i]
            }
            var atext1 = Alphabet(longtmp2).split(" ")
            var atext2 = Alphabet(longtmp33).split(" ")
            var atext3 = Alphabet(longtmp44).split(" ")
            var atext4 = Alphabet(longtmp55).split(" ")
            var retext1 = ""
            var retext2 = ""
            var retext3 = ""
            var retext4 = ""
            val assetManager: AssetManager = resources.assets
            val inputStream: InputStream = assetManager.open("dictionary.txt")

            inputStream.bufferedReader().readLines().forEach {

                for (item in atext1) {
                    if (it.equals(item)) {
                        retext1 += item + " "
                    }
                }
                for (item in atext2) {
                    if (it.equals(item)) {
                        retext2 += item + " "
                    }
                }
                for (item in atext3) {
                    if (it.equals(item)) {
                        retext3 += item + " "
                    }
                }
                for (item in atext4) {
                    if (it.equals(item)) {
                        retext4 += item + " "
                    }
                }

            }

            retext1 = checktext(retext1)
            retext2 = checktext(retext2)
            retext3 = checktext(retext3)
            retext4 = checktext(retext4)

            text1.setText(
                "[Analysis 1]" + "\n" +
                        Alphabet(longtmp2) + "\n\n" +
                        "[English word]" + "\n"+
                        retext1

            )
            text2.setText(
                "[Analysis 2]" + "\n" +
                        Alphabet(longtmp33) + "\n\n" +
                        "[English word]" + "\n"+
                        retext2
            )
            text3.setText(
                "[Analysis 3]" + "\n" +
                        Alphabet(longtmp44) + "\n\n" +
                        "[English word]" + "\n"+
                        retext3
            )

            text4.setText(
                "[Analysis 4]" + "\n" +
                        Alphabet(longtmp55) + "\n\n" +
                        "[English word]" + "\n"+
                        retext4
            )


            //restring + "\n\n" + longtmp1 + "\n\n" +
        }
        but_pull.setOnClickListener(){
            msg.setText("If there is no light for 5 seconds, \n click the \"Analysis End\" button.")
            but_pull2.setVisibility(VISIBLE);
            but_pull.setVisibility(GONE)
            click = true
            Mstring = ""
            thread {
                while (click) {
                    if(!click){
                        this.onStop()
                    }
                    if (stringFromJNI() == "1") {
                        Mstring += "1"
                        Thread.sleep(100)
                    }
                    if (stringFromJNI() == "0") {
                        Mstring += "0"
                        Thread.sleep(100)
                    }
                }
            }
        }
    }

    fun checktext(retext : String) : String{
        var retexts = ""
        if(retext.equals("")) {
            retexts = "There are no English words."
        }
        else{
            retexts += retext
        }
        return retexts;
    }

    fun Alphabet(astring: String): String{
        var a = astring.replace("1200", "a")
        var b = ""
        a = a.replace("2111", "b")
        a = a.replace("2121", "c")
        a = a.replace("2110", "d")
        a = a.replace("1000", "e")
        a = a.replace("1121", "f")
        a = a.replace("2210", "g")
        a = a.replace("1111", "h")
        a = a.replace("1100", "i")
        a = a.replace("1222", "j")
        a = a.replace("2120", "k")
        a = a.replace("1211", "l")
        a = a.replace("2200", "m")
        a = a.replace("2100", "n")
        a = a.replace("2220", "o")
        a = a.replace("1221", "p")
        a = a.replace("2212", "q")
        a = a.replace("1210", "r")
        a = a.replace("1110", "s")
        a = a.replace("2000", "t")
        a = a.replace("1120", "u")
        a = a.replace("1112", "v")
        a = a.replace("1220", "w")
        a = a.replace("2112", "x")
        a = a.replace("2122", "y")
        a = a.replace("2211", "z")
        a = a.replace("1001", " ")
        a = a.replace(",", "")
        Log.e("단어 값", a)
        for(i in a.indices){
            if(a[i] == '0'){
                continue
            }else if (a[i] == '1'){
                continue
            }else if (a[i] == '2'){
                continue
            }
            b += a[i]
        }
        return b
    }

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    mOpenCvCameraView!!.enableView()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCameraViewStarted(width: Int, height: Int) {

    }

    override fun onCameraViewStopped() {

    }

    override fun onCameraFrame(inputFrame: CvCameraViewFrame?): Mat? {
        val matInput = inputFrame!!.rgba()
        if (matResult != null) matResult!!.release()
        matResult = Mat(matInput.rows(), matInput.cols(), matInput.type())

        ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult!!.nativeObjAddr)

//        Log.d("ConvertRGBtoGray",matResult.toString())
        return matResult
    }

    override fun onPause() {
        super.onPause()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(
                TAG,
                "onResume :: Internal OpenCV library not found."
            )
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback)
        } else {
            Log.d(
                TAG,
                "onResum :: OpenCV library found inside package. Using it!"
            )
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

}



