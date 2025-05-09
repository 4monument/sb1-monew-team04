# 1단계: 빌드용 이미지
FROM gradle:8.6-jdk17-alpine AS builder

WORKDIR /app

RUN apk add --no-cache dos2unix

# Gradle Wrapper 복사 (명시적으로)
COPY gradlew ./gradlew
COPY gradle/wrapper/gradle-wrapper.jar ./gradle/wrapper/gradle-wrapper.jar
COPY gradle/wrapper/gradle-wrapper.properties ./gradle/wrapper/gradle-wrapper.properties

RUN dos2unix ./gradlew && chmod +x ./gradlew

# build 설정 복사
COPY build.gradle settings.gradle ./

# 의존성 캐싱
RUN ./gradlew dependencies --no-daemon || return 0

# 전체 소스 복사
COPY src ./src

# 실제 빌드
RUN ./gradlew clean build -x test --no-daemon

# 2단계: 실행용 이미지 (경량)
FROM amazoncorretto:17-alpine

WORKDIR /app

# 환경 변수 설정
ENV PROJECT_NAME=monew \
    PROJECT_VERSION=0.0.1-M1 \
    JVM_OPTS="-Xmx192m -Xms92m -XX:MaxMetaspaceSize=192m -XX:+UseSerialGC"

# /tmp/backup 디렉토리 생성 및 권한 설정
RUN mkdir -p /tmp/backup && chmod 777 /tmp/backup

# 빌드된 JAR만 복사
COPY --from=builder /app/build/libs/monew-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 80

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]