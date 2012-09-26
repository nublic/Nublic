all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/nublic/apache2/apps/init
	unzip target/*.war -d ${DESTDIR}/var/lib/nublic/apache2/apps/init
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/init/META-INF
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/init/WEB-INF
