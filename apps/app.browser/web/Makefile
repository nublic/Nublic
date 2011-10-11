all:
	mvn package

install:
	mkdir -p ${DESTDIR}/var/lib/tomcat6/webapps
	cp target/*.war ${DESTDIR}/var/lib/tomcat6/webapps
