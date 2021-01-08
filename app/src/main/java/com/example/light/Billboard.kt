package com.example.light

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_billboard.*
import kotlin.concurrent.thread

class Billboard : AppCompatActivity() {

    lateinit var params: WindowManager.LayoutParams
    var brightness: Float = 0.0f

    override fun onResume() {
        super.onResume();

        // 기존 밝기 저장
        brightness = params.screenBrightness;
        // 최대 밝기로 설정
        params.screenBrightness = 1f;
        // 밝기 설정 적용
        getWindow().setAttributes(params);
    }

    override fun onPause() {
        super.onPause();

        // 기존 밝기로 변경
        params.screenBrightness = brightness;
        getWindow().setAttributes(params);
    }

    // 뒤로가기 못하게 하기
    override fun onBackPressed() {
        super.onBackPressed();
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) setFullscreen()
    }

    private fun setFullscreen() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billboard)
        var linear1 : LinearLayout = findViewById(R.id.linear1)

        var restring = intent.getStringExtra("key")
        var Colors = 0
        params = getWindow().getAttributes();

        var handler = Handler{
            if (Colors == 1){
                linear1.setBackgroundColor(Color.WHITE)
            }
            else{
                linear1.setBackgroundColor(Color.BLACK)
            }
            true
        }
        thread {
            for (i in restring!!.indices) {
                if (restring[i].equals('1')) {
                    Colors = 1
                    handler.obtainMessage().sendToTarget()
                    Thread.sleep(1000)
                }
                if (restring[i].equals('2')) {
                    Colors = 1
                    handler.obtainMessage().sendToTarget()
                    Thread.sleep(2000)
                }
                if (restring[i].equals('0')) {
                    Colors = 0
                    handler.obtainMessage().sendToTarget()
                    Thread.sleep(1000)
                }
                Colors = 0
                handler.obtainMessage().sendToTarget()
                Thread.sleep(500)
            }
            finish()
        }
//        finish()
    }

}
