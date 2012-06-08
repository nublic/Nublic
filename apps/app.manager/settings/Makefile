all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/nublic/apache2/apps/settings
	unzip target/*.war -d ${DESTDIR}/var/lib/nublic/apache2/apps/settings
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/settings/META-INF
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/settings/WEB-INF
