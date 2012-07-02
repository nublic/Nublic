all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/nublic/apache2/apps/downloads
	unzip target/*.war -d ${DESTDIR}/var/lib/nublic/apache2/apps/downloads
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/downloads/META-INF
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/downloads/WEB-INF
