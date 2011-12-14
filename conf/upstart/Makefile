all:
	echo "Make nothing"

install:
	mkdir -p ${DESTDIR}/usr/sbin
	cp check-postgresql ${DESTDIR}/usr/sbin
	cp check-tomcat6 ${DESTDIR}/usr/sbin
	cp check-solr ${DESTDIR}/usr/sbin
	mkdir -p ${DESTDIR}/etc/init
	cp *.conf ${DESTDIR}/etc/init
