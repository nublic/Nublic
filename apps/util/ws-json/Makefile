all:
	${MAVEN} package

install:
	mkdir -p ${DESTDIR}/var/lib/nublic
	cp target/*jar-with-dependencies.jar ${DESTDIR}/var/lib/nublic/nublic-ws-json-scala.jar
