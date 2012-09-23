all:
	echo "Make nothing"

install:
	mkdir -p ${DESTDIR}/usr/sbin
	cp check-postgresql ${DESTDIR}/usr/sbin
	cp check-solr ${DESTDIR}/usr/sbin
	mkdir -p ${DESTDIR}/etc/init
	cp *.conf ${DESTDIR}/etc/init
	mkdir -p ${DESTDIR}/usr/share/postgresql-common
	cp init.d-functions ${DESTDIR}/usr/share/postgresql-common
