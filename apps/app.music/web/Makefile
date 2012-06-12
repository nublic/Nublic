all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/nublic/apache2/apps/music
	unzip target/*.war -d ${DESTDIR}/var/lib/nublic/apache2/apps/music
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/music/META-INF
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/music/WEB-INF
