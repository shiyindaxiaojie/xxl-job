FROM tomcat:8.5.78-jdk8

ENV TZ=Asia/Shanghai
COPY cat-home/target/cat-home.war /usr/local/tomcat/webapps/cat.war
COPY docker/tomcat-server.xml /usr/local/tomcat/conf/server.xml

CMD ["/bin/sh", "catalina.sh run"]
