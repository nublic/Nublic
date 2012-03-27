all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/tomcat7/webapps
	cp target/*.war ${DESTDIR}/var/lib/tomcat7/webapps
