all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/nublic/apache2/apps/photos
	unzip target/*.war -d ${DESTDIR}/var/lib/nublic/apache2/apps/photos
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/photos/META-INF
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/photos/WEB-INF
