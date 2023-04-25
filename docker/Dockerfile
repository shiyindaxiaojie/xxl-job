FROM tomcat:8.5.78-jdk8

ENV TZ=Asia/Shanghai
ADD xxl-job-admin/target/xxl-job-admin*.war /usr/local/tomcat/webapps/xxl-job-admin.war
COPY docker/tomcat-server.xml /usr/local/tomcat/conf/server.xml

CMD ["/bin/sh", "catalina.sh run"]
