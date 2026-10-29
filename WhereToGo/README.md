# 전국어디 놀러갈까? — Android 테스트 앱 (WebView 버전)

프로토타입 HTML(app/src/main/assets/index.html)을 그대로 앱으로 감싼 프로젝트예요.

1. Android Studio(Koala 이상)에서 이 폴더(WhereToGo)를 연다 → Gradle Sync
2. 폰을 USB 디버깅으로 연결하거나 에뮬레이터를 켜고 ▶ Run
3. APK만 필요하면 Build > Build Bundle(s)/APK(s) > Build APK(s)

주의: 구글 폰트(도현체)는 인터넷이 있어야 보여요. 광고·행사·맛집 데이터는 아직 샘플이에요.

## GitHub로 설치 (APK 자동 빌드)
1. GitHub에서 새 저장소 만들기 → 이 폴더 전체를 업로드(또는 git push, 브랜치는 main)
2. 저장소의 Actions 탭 → "Build APK"가 자동 실행 (안 보이면 Run workflow)
3. 완료되면 실행 결과 화면 아래 Artifacts의 WhereToGo-apk 다운로드 → zip 풀고 WhereToGo.apk를 폰에서 설치
   - 태그 `v0.1`을 만들어 push하면 Releases에 APK가 올라가서 폰 브라우저로 바로 받을 수 있어요.
4. 설치 시 "출처를 알 수 없는 앱 허용" 필요
