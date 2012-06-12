all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/nublic/apache2/apps/market
	unzip target/*.war -d ${DESTDIR}/var/lib/nublic/apache2/apps/market
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/market/META-INF
	rm -rf ${DESTDIR}/var/lib/nublic/apache2/apps/market/WEB-INF
