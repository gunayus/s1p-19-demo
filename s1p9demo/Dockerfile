FROM openjdk:8-jre-alpine
COPY target/s1p9demo.jar /opt/s1p9demo.jar
ENTRYPOINT ["java",  "-XX:+UseParallelGC", "-Xms2048m", "-Xmx4096m", "-XX:MetaspaceSize=2048m", "-XX:+UnlockExperimentalVMOptions",  "-Djava.net.preferIPv4Stack=true", "-jar","/opt/s1p9demo.jar"]
EXPOSE 2021
