# Light - 스마트폰 라이트로 모스부호 보내기

**Light**는 스마트폰 라이트를 활용하여 모스부호 신호를 송신하고, OpenCV를 이용해 모스부호를 수신 및 해석할 수 있는 개인 프로젝트입니다.

---

## 🗂 목차
- [프로젝트 개요](#프로젝트-개요)
- [주요 기능](#주요-기능)
- [디자인 및 사용 방법](#디자인-및-사용-방법)
- [사용 기술 및 특징](#사용-기술-및-특징)
- [프로젝트 목적](#프로젝트-목적)

---

## 📖 프로젝트 개요
**Light** 프로젝트는 스마트폰의 라이트와 카메라를 사용하여 모스부호를 송수신하는 애플리케이션입니다.
- **송신**: 사용자가 입력한 텍스트를 모스부호로 변환하여 스마트폰 라이트로 송신.
- **수신**: 스마트폰 카메라로 빛의 패턴을 감지하고 OpenCV로 분석하여 텍스트로 해석.

이 프로젝트는 Android 앱 개발과 OpenCV 활용을 통해 이미지 처리 및 신호 분석을 학습하기 위해 제작되었습니다.

---

## ⚙️ 개발 환경
![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)  
![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)  
![C++](https://img.shields.io/badge/Language-C++-00599C?style=for-the-badge&logo=cplusplus&logoColor=white)  
![OpenCV](https://img.shields.io/badge/Library-OpenCV-5C3EE8?style=for-the-badge&logo=opencv&logoColor=white)

---

## 주요 기능

### 1. 신호 송신
- 사용자가 입력한 텍스트를 모스부호로 변환.
- 스마트폰 플래시를 사용하여 점(1)과 선(2)으로 신호를 보냅니다.

### 2. 신호 수신 및 해석
- 스마트폰 카메라로 라이트 신호를 감지.
- OpenCV를 이용해 라이트의 깜빡임을 분석하고, 모스부호를 해석하여 텍스트로 변환합니다.

---

## 디자인 및 사용 방법

### 아이콘
![ic_launcher_light](https://user-images.githubusercontent.com/63902992/143733539-1766f2cb-320a-46a3-92e7-cfb07d13e9e4.png)

### 메인 화면
앱 실행 후 메인 화면에서 송신(SEND) 또는 수신(RECEIVE) 기능을 선택할 수 있습니다.  
![image](https://user-images.githubusercontent.com/63902992/143733732-e6ff336b-7ae7-413f-8801-795352b0ac8d.png)

### 송신 (SEND)
1. 모스부호 형식을 선택한 후, 텍스트를 입력합니다.
2. `Send` 버튼을 클릭하여 라이트 신호를 전송합니다.  
   ![image](https://user-images.githubusercontent.com/63902992/143733756-229b0026-48a3-4450-ab50-be4fdbef9c09.png)

### 수신 (RECEIVE)
1. `Analysis Start` 버튼을 클릭하여 라이트 신호 수신을 시작합니다.
2. 중앙의 노란 사각형 영역에 들어오는 빛을 분석합니다. (빛이 감지되면 오른쪽 상단에 상태 표시)
3. 신호가 끝나면 `Analysis End` 버튼을 클릭하여 해석을 종료하고, 결과를 확인합니다.  
   ![image](https://user-images.githubusercontent.com/63902992/143733826-8c70f59e-e1a2-45c2-a905-5e19c03ba4ff.png)  
   ![image](https://user-images.githubusercontent.com/63902992/143733881-59992a1d-a069-422e-89ec-8dd70e7ba6c6.png)

---

## 사용 기술 및 특징

1. **Kotlin**
    - Android UI 설계 및 앱 로직 구현.

2. **OpenCV**
    - 카메라 이미지 처리 및 빛 감지.
    - 모스부호 수신 분석에 사용.

3. **C++ (JNI)**
    - OpenCV와의 연동 및 고성능 이미지 처리.

---

## 프로젝트 목적
Light 프로젝트는 스마트폰의 라이트와 카메라를 활용하여 모스부호 송신 및 수신 기능을 구현하며, OpenCV와 Kotlin의 통합 활용 방법을 익히고자 제작한 개인 프로젝트입니다.
