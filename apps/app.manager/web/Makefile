all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/nublic/apache2/apps/manager
	unzip target/*.war -d ${DESTDIR}/var/lib/nublic/apache2/apps/manager
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/manager/META-INF
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/manager/WEB-INF
