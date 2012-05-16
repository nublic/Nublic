all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/tomcat7/conf/Catalina/localhost
	cp market#server.xml ${DESTDIR}/var/lib/tomcat7/conf/Catalina/localhost
	mkdir -p ${DESTDIR}/var/lib/nublic/webapps
	cp target/*.war ${DESTDIR}/var/lib/nublic/webapps/app.market.server.war
	cp packages.json ${DESTDIR}/var/lib/nublic