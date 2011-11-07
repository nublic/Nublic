all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/tomcat6/conf/Catalina/localhost
	cp manager#server.xml ${DESTDIR}/var/lib/tomcat6/conf/Catalina/localhost
	mkdir -p ${DESTDIR}/var/lib/nublic/webapps
	cp target/*.war ${DESTDIR}/var/lib/nublic/webapps/app.manager.server.war
