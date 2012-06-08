all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/nublic/apache2/apps/browser
	unzip target/*.war -d ${DESTDIR}/var/lib/nublic/apache2/apps/browser
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/browser/META-INF
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/browser/WEB-INF
