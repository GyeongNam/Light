package com.example.light

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //여기서부턴 퍼미션 관련 메소드
    val PERMISSIONS_REQUEST_CODE = 1000
    var PERMISSIONS =
        arrayOf("android.permission.CAMERA")


    private fun hasPermissions(permissions: Array<String>): Boolean {
        var result: Int

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (perms in permissions) {
            result = ContextCompat.checkSelfPermission(this, perms)
            if (result == PackageManager.PERMISSION_DENIED) {
                //허가 안된 퍼미션 발견
                return false
            }
        }

        //모든 퍼미션이 허가되었음
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> if (grantResults.size > 0) {
                val cameraPermissionAccepted = (grantResults[0]
                        == PackageManager.PERMISSION_GRANTED)
                if (!cameraPermissionAccepted) showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.")
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private fun showDialogForPermission(msg: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("알림")
        builder.setMessage(msg)
        builder.setCancelable(false)
        builder.setPositiveButton("예",
            DialogInterface.OnClickListener { dialog, id ->
                requestPermissions(
                    PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
                val nextIntent = Intent(this, pull::class.java)
                startActivity(nextIntent)
            })
        builder.setNegativeButton("아니오",
            DialogInterface.OnClickListener { arg0, arg1 -> finish() })
        builder.create().show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val but_push : Button = findViewById(R.id.button)
        val but_pull : Button = findViewById(R.id.button2)

        but_push.setOnClickListener{
            val nextIntent = Intent(this, push::class.java)
            startActivity(nextIntent)
        }

        but_pull.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //퍼미션 상태 확인
                if (!hasPermissions(PERMISSIONS)) {

                    //퍼미션 허가 안되어있다면 사용자에게 요청
                    requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                }
                else{
                    val nextIntent = Intent(this, pull::class.java)
                    startActivity(nextIntent)
                }
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
           // System.loadLibrary("opencv_java4");
        }
    }
}
