FROM openjdk:8
COPY . /usr/app

WORKDIR /usr/app
RUN ./gradlew shadowJar --no-daemon
#RUN ./gradlew generateDocumentation --no-daemon
ENV GOOGLE_APPLICATION_CREDENTIALS ../../speech-to-text-demo-329208-d15523e308e8.json

WORKDIR /usr/app/build/libs
ENTRYPOINT ["java", "-jar", "speech-to-text-demo-1.0.0-all.jar", "../../config/cloud-run.json"]
