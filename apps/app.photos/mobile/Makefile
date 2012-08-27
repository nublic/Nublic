all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/nublic/apache2/apps/photos-mobile
	unzip target/*.war -d ${DESTDIR}/var/lib/nublic/apache2/apps/photos-mobile
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/photos-mobile/META-INF
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/photos-mobile/WEB-INF
