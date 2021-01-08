#include <jni.h>
#include <string.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/core/core.hpp>
#include <iostream>
#include "opencv2/videoio.hpp"

using namespace cv;
using namespace std;

String result_stirng;
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_light_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

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
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_light_pull_stringFromJNI(JNIEnv *env, jobject thiz) {

    return env->NewStringUTF(result_stirng.c_str());
}