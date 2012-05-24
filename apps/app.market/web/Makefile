all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/tomcat7/conf/Catalina/localhost
	cp market.xml ${DESTDIR}/var/lib/tomcat7/conf/Catalina/localhost
	mkdir -p ${DESTDIR}/var/lib/nublic/webapps
	cp target/*.war ${DESTDIR}/var/lib/nublic/webapps/app.market.war
