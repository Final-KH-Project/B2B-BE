# OpenJDK 21을 기본 이미지로 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일만 복사
# 예시:
# - 원본 파일: build/libs/[프로젝트명]-0.0.1-SNAPSHOT.jar
# - 복사 후: /app/app.jar
COPY build/libs/b2b-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
# app.jar로 실행 (복사된 파일명 사용)
ENTRYPOINT ["java", "-jar", "app.jar"]