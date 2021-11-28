## Light
스마트폰 라이트로 모스부호 보내기<br>


## 개발 환경

- Android 
- Kotlin
- OpenCV

## 디자인 및 사용 방법

ICON:

![ic_launcher_light](https://user-images.githubusercontent.com/63902992/143733539-1766f2cb-320a-46a3-92e7-cfb07d13e9e4.png)


MAIN:


![image](https://user-images.githubusercontent.com/63902992/143733732-e6ff336b-7ae7-413f-8801-795352b0ac8d.png)

SEND:

빛을 모스부호 형식을으로 보낼 방법을 선택한후 텍스트를 입력하고 'send'버튼 클릭.

![image](https://user-images.githubusercontent.com/63902992/143733756-229b0026-48a3-4450-ab50-be4fdbef9c09.png)

RECEIVE:

Analysis Start 버튼 클릭시 가운데 노랑색 사각형안에 빛이 들어오면 1로 판단, <br>
우측상단에 빛으로 인식되는지 안되는지 노란색 영역 따로 표시

![image](https://user-images.githubusercontent.com/63902992/143733826-8c70f59e-e1a2-45c2-a905-5e19c03ba4ff.png)

신호가 끝나면 Analysis End 버튼을 클릭.

해석으로 예상 단어 및 문장을 보여준다.

![image](https://user-images.githubusercontent.com/63902992/143733881-59992a1d-a069-422e-89ec-8dd70e7ba6c6.png)

## 개발 내용

신호 발송

```java
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
        if (restring[i].equals('1')) {    // 점
            torch.flashOn()
            Thread.sleep(1000)
        }
        if (restring[i].equals('2')) {    // 선
            torch.flashOn()
            Thread.sleep(2000)
        }
        if (restring[i].equals('0')) {    // 없음
            torch.flashOff()
            Thread.sleep(1000)
        }
        torch.flashOff()
        Thread.sleep(500)
    }
    timebut = 0
    handler.obtainMessage().sendToTarget()
}

```
openCV 설정
```cpp
extern "C"
JNIEXPORT void JNICALL
Java_com_example_light_pull_ConvertRGBtoGray(JNIEnv *env, jobject thiz, jlong matAddrInput, jlong matAddrResult) {
    result_stirng = "0";
    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;
    Mat Intmp = *( Mat *) new Mat();
    Mat Retmp = *( Mat *) new Mat();
    Mat Roi = *( Mat *) new Mat();

    cvtColor(matInput, Intmp,COLOR_RGB2GRAY);
    cvtColor(matInput, matResult,CV_CAP_MODE_RGB);

    Roi = Intmp(Rect(matResult.size().width/2-50, matResult.size().height/2-50, 100, 100));

    threshold(Roi , Retmp , 250.0 , 255 , CV_THRESH_BINARY );
    GaussianBlur(Retmp, Retmp, Size(5, 5), 0);
    inRange(Retmp, Scalar(255.0, 255.0, 200.0), Scalar(255.0, 255.0, 255.0), Retmp);


    vector<vector<Point>> contours;
    vector<Vec4i> hierarchy;

    findContours(Retmp, contours, hierarchy, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE,Point(0,0));

    rectangle(matResult, Rect(matResult.size().width/2-50, matResult.size().height/2-50, 100, 100), Scalar(255, 255, 200),20, 8, 0);
    for (int i = 0; i< contours.size(); i++)
    {
        if(contourArea(contours[i]) > 10) {
            int x = (moments(contours[i]).m10 / moments(contours[i]).m00);
            int y = (moments(contours[i]).m01 / moments(contours[i]).m00);

            circle(matResult, Point(x, y), 1, Scalar(255, 0, 255), 10, 8, 0);
            drawContours(matResult, contours, i, Scalar(0, 255, 255), 3, 8, hierarchy, 0, Point());
            result_stirng = "1";
            continue;
        }
    }
}
```

신호 해석

```java
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
```
