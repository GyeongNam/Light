package com.example.light

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils.split
import android.widget.*
import androidx.annotation.RequiresApi
import java.util.*
import kotlin.concurrent.thread

@Suppress("DEPRECATION")
class push : AppCompatActivity() {
    private lateinit var sampleQueue: Queue<Int>

    internal val initialList = charArrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
        'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    internal val medialList = charArrayOf(
        'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
        'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
    )

    internal val finalList: CharArray
        get() = charArrayOf(
            this.nullChar,'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ',
            'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ',
            'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )

    internal val splitTable: Map<Char, String>
        get() = mapOf(
            'ㅘ' to "ㅗㅏ",
            'ㅙ' to "ㅗㅐ",
            'ㅚ' to "ㅗㅣ",
            'ㅝ' to "ㅜㅓ",
            'ㅞ' to "ㅜㅔ",
            'ㅟ' to "ㅜㅣ",
            'ㅢ' to "ㅡㅣ",
            'ㄵ' to "ㄴㅈ",
            'ㄺ' to "ㄹㄱ",
            'ㄻ' to "ㄹㅁ",
            'ㄼ' to "ㄹㅂ",
            'ㄽ' to "ㄹㅅ",
            'ㄾ' to "ㄹㅌ",
            'ㄿ' to "ㄹㅍ",
            'ㅀ' to "ㄹㅎ",
            'ㅄ' to "ㅂㅅ"
        )

    /**
     * `null`문자(`'\0'`)를 나타냅니다.
     */
    val nullChar = '\u0000'

    /**
     * 문자 [char]가 한글인지 확인합니다.
     *
     * @param char 한글인지 확인하려는 문자
     * @return 만일 한글이면 `true`, 그렇지 않으면 `false`를 반환
     */
    fun isHangul(char: Char): Boolean = isHangul(char.toInt())

    /**
     * 코드 값 [code]가 한글인지 확인합니다.
     *
     * @see isHangul
     *
     * @param code 한글인지 확인하려는 코드 값
     * @return 만일 한글이면 `true`, 그렇지 않으면 `false`를 반환
     */
    fun isHangul(code: Int): Boolean = 0xAC00 <= code && code <= 0xD7A3

    /**
     * 문자 [char]를 초성, 중성, 종성으로 분리된 문자열을 반환합니다.
     *
     * @param char 초성, 중성, 종성으로 분리하고 하는 문자
     * @param atomic `true`이면 중성과 종성을 더 잘게 분리합니다. 예)`ㄼ`은 `ㄹㅂ`으로, `ㅚ`는 `ㅗㅣ`로
     * @return 초성, 중성, 종성으로 분리된 문자열을 반환합니다.
     */
    @JvmOverloads
    fun splitAsString(char: Char, atomic: Boolean = false): String {
        val (i, m, f) = split(char) ?: return char.toString()
        val initial = i
        val medial = if (atomic) splitTable[m] ?: m else m
        val final = if (atomic) splitTable[f] ?: f else f
        return "$initial$medial$final".filter { it != nullChar }
    }

    /**
     * 문자 [char]를 초성, 중성, 종성으로 분리합니다.
     *
     * @see splitAsString
     *
     * @param char 초성, 중성, 종성으로 분리하고 하는 문자
     * @return 초성, 중성, 종성으로 분리된 결과
     */
    fun split(char: Char): Triple<Char, Char, Char>? {
        if (!isHangul(char)) {
            return null
        }

        return (char.toInt() - 0xAC00).let {
            var v = it
            val f = v % 28
            v /= 28
            val m = v % 21
            v /= 21
            val i = v % 19
            Triple(initialList[i], medialList[m], finalList[f])
        }
    }
    var timebut = 0
    var han = 0
    /**
     * 문자열 [string]의 자소를 전부 분해합니다.
     *
     * @see split
     *
     * @param string 자소분해를 하고자 하는 문자열
     * @param atomic `true`이면 중성과 종성을 더 잘게 분리합니다. 예)`ㄼ`은 `ㄹㅂ`으로, `ㅚ`는 `ㅗㅣ`로
     * @return 자소분해된 문자열
     */
    @JvmOverloads
    fun strokes(string: String, atomic: Boolean = false): String = string
        .map { splitAsString(it, atomic) }
        .joinToString("")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push)
        val but_push1 : Button = findViewById(R.id.button3)
        val editText : EditText = findViewById(R.id.editText)
        val radio2 : RadioButton = findViewById(R.id.radioButton2)

        but_push1.setOnClickListener {
            but_push1.setEnabled(false)
            but_push1.setText("")
            var string = ""
            var restring = ""
            han = 0
            var push_text = editText.text.toString()
            string = push_text
//            strokes(push_text)
            for (i in string.indices) {
                if(isHangul(string[i])){
                    han = 1
                }
            }

            if(han==1){
                Toast.makeText(this@push, "영어만 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
            else{

                for (i in string.indices) {
                    if (string[i].toLowerCase().equals('a')) {
                        restring = restring + "1200"
                    } else if (string[i].toLowerCase().equals(' ')) {
                        restring = restring + "1001"
                    } else if (string[i].toLowerCase().equals('b')) {
                        restring = restring + "2111"
                    } else if (string[i].toLowerCase().equals('c')) {
                        restring = restring + "2121"
                    } else if (string[i].toLowerCase().equals('d')) {
                        restring = restring + "2110"
                    } else if (string[i].toLowerCase().equals('e')) {
                        restring = restring + "1000"
                    } else if (string[i].toLowerCase().equals('f')) {
                        restring = restring + "1121"
                    } else if (string[i].toLowerCase().equals('g')) {
                        restring = restring + "2210"
                    } else if (string[i].toLowerCase().equals('h')) {
                        restring = restring + "1111"
                    } else if (string[i].toLowerCase().equals('i')) {
                        restring = restring + "1100"
                    } else if (string[i].toLowerCase().equals('j')) {
                        restring = restring + "1222"
                    } else if (string[i].toLowerCase().equals('k')) {
                        restring = restring + "2120"
                    } else if (string[i].toLowerCase().equals('l')) {
                        restring = restring + "1211"
                    } else if (string[i].toLowerCase().equals('m')) {
                        restring = restring + "2200"
                    } else if (string[i].toLowerCase().equals('n')) {
                        restring = restring + "2100"
                    } else if (string[i].toLowerCase().equals('o')) {
                        restring = restring + "2220"
                    } else if (string[i].toLowerCase().equals('p')) {
                        restring = restring + "1221"
                    } else if (string[i].toLowerCase().equals('q')) {
                        restring = restring + "2212"
                    } else if (string[i].toLowerCase().equals('r')) {
                        restring = restring + "1210"
                    } else if (string[i].toLowerCase().equals('s')) {
                        restring = restring + "1110"
                    } else if (string[i].toLowerCase().equals('t')) {
                        restring = restring + "2000"
                    } else if (string[i].toLowerCase().equals('u')) {
                        restring = restring + "1120"
                    } else if (string[i].toLowerCase().equals('v')) {
                        restring = restring + "1112"
                    } else if (string[i].toLowerCase().equals('w')) {
                        restring = restring + "1220"
                    } else if (string[i].toLowerCase().equals('x')) {
                        restring = restring + "2112"
                    } else if (string[i].toLowerCase().equals('y')) {
                        restring = restring + "2122"
                    } else if (string[i].toLowerCase().equals('z')) {
                        restring = restring + "2211"
                    }
//                else if (string[i].equals('ㅠ')) {
//                    restring = restring + "1210"
//                } else if (string[i].equals('ㅡ')) {
//                    restring = restring + "2110"
//                } else if (string[i].equals('ㅣ')) {
//                    restring = restring + "1120"
//                } else if (string[i].equals('ㅔ')) {
//                    restring = restring + "2122"
//                } else if (string[i].equals('ㅐ')) {
//                    restring = restring + "2212"
//                } else if (string[i].equals('ㅘ')) {
//                    restring = restring + "12001000"
//                } else if (string[i].equals('ㅙ')) {
//                    restring = restring + "12002212"
//                } else if (string[i].equals('ㅚ')) {
//                    restring = restring + "12001120"
//                } else if (string[i].equals('ㅢ')) {
//                    restring = restring + "21101120"
//                } else if (string[i].equals('ㅟ')) {
//                    restring = restring + "11111120"
//                } else if (string[i].equals('ㅞ')) {
//                    restring = restring + "11112122"
//                } else if (string[i].equals('ㅝ')) {
//                    restring = restring + "11112000"
//                } else if (string[i].equals('ㅒ')) {
//                    restring = restring + "22122212"
//                } else if (string[i].equals('ㅖ')) {
//                    restring = restring + "21222122"
//                } else if (string[i].equals('ㄳ')) {
//                    restring = restring + "12112210"
//                } else if (string[i].equals('ㄵ')) {
//                    restring = restring + "11211221"
//                } else if (string[i].equals('ㄶ')) {
//                    restring = restring + "11211222"
//                } else if (string[i].equals('ㄺ')) {
//                    restring = restring + "11121211"
//                } else if (string[i].equals('ㄻ')) {
//                    restring = restring + "11122200"
//                } else if (string[i].equals('ㄼ')) {
//                    restring = restring + "11121220"
//                } else if (string[i].equals('ㄽ')) {
//                    restring = restring + "11122210"
//                } else if (string[i].equals('ㄾ')) {
//                    restring = restring + "11122211"
//                } else if (string[i].equals('ㄿ')) {
//                    restring = restring + "11122220"
//                } else if (string[i].equals('ㅀ')) {
//                    restring = restring + "11121222"
//                } else if (string[i].equals('ㅄ')) {
//                    restring = restring + "12202210"
//                }
                } // 한글
            }

            val torch = Torch(this)
            //sampleQueue = LinkedList()

            if(radio2.isChecked == true){
                but_push1.setEnabled(true)
                but_push1.setText("Send")
                val nextIntent = Intent(this, Billboard::class.java)
                nextIntent.putExtra("key",restring )
                startActivity(nextIntent)
            }
            else {
                var handler = Handler{
                    if (timebut == 0){
                        but_push1.setEnabled(true)
                        but_push1.setText("Send")
                    }
                    true
                }
                thread {
                    timebut = 1
                    for (i in restring.indices) {
                        if (restring[i].equals('1')) {
                            torch.flashOn()
                            Thread.sleep(1000)
                        }
                        if (restring[i].equals('2')) {
                            torch.flashOn()
                            Thread.sleep(2000)
                        }
                        if (restring[i].equals('0')) {
                            torch.flashOff()
                            Thread.sleep(1000)
                        }
                        torch.flashOff()
                        Thread.sleep(500)
                    }
                    timebut = 0
                    handler.obtainMessage().sendToTarget()
                }
            }
        }
    }
    class Torch(context: Context) {   // CameraManager 객체를 얻어야 하므로 Context를 생성자로 받았음
        private var cameraId: String? = null
        @SuppressLint("NewApi")
        private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE)
                as CameraManager   // getSystemService()의 리턴값이 object형이므로 as로 형변환했음

        init {                         // 클래스가 초기화 될 때 실행됨
            cameraId = getCameraId()
        }

        @SuppressLint("NewApi")
        fun flashOn() {
            if (cameraId != null) cameraManager.setTorchMode(cameraId!!, true)
        }

        @SuppressLint("NewApi")
        fun flashOff() {
            if (cameraId != null) cameraManager.setTorchMode(cameraId!!, false)
        }

        @SuppressLint("NewApi")
        private fun getCameraId():String? { // 카메라 ID는 각각의 내장 카메라에 부여된 고유의 ID이다
            // 카메라가 없다면 null을 반환해야 하므로 리턴형을 String?로 지정
            val cameraIds = cameraManager.cameraIdList   // 기기가 가진 모든 카메라 목록
            for (id in cameraIds) {
                val info = cameraManager.getCameraCharacteristics(id)
                val flashAvailable = info.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) // 플래시 가능 여부
                val lensFacing = info.get(CameraCharacteristics.LENS_FACING)   // 카메라 랜즈의 방향
                if (flashAvailable != null && flashAvailable && (lensFacing != null) && (lensFacing > 0)
                    && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    // 플래시가 가능하고 카메라 방향이 뒷방향
                    return id
                }
            }
            return null
        }
    }
}