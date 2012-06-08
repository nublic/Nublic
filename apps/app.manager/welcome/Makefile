all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/nublic/apache2/apps/welcome
	unzip target/*.war -d ${DESTDIR}/var/lib/nublic/apache2/apps/welcome
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/welcome/META-INF
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/welcome/WEB-INF
