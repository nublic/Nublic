all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/tomcat7/conf/Catalina/localhost
	cp downloads#server.xml ${DESTDIR}/var/lib/tomcat7/conf/Catalina/localhost
	mkdir -p ${DESTDIR}/var/lib/nublic/webapps
	cp target/*.war ${DESTDIR}/var/lib/nublic/webapps/app.downloads.server.war
	mkdir -p ${DESTDIR}/etc/init
	cp aria2.conf ${DESTDIR}/etc/init
