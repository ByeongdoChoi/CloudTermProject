# CloudTermProject
1.환경
-운영체제: 우분투 20.04

-자바: 버전8 이상 설치

-메이븐 설치

2.실행
1.master 브런치의 프로젝트를 다운 받은 후 콘솔에서 myapp 폴더로 이동

2.mvn package 명령어 실행으로 프로젝트 빌드

3.mvn exec:java -Dexec.mainClass="com.example.myapp.App" 프로젝트 실행
