all:
	echo "Make nothing"

install:
	mkdir -p ${DESTDIR}/etc/solr
	cp solr-tomcat.xml ${DESTDIR}/etc/solr
	cp tomcat.policy ${DESTDIR}/etc/solr
	mkdir -p ${DESTDIR}/etc/solr/conf
	cp solrconfig.xml ${DESTDIR}/etc/solr/conf
	cp schema.xml ${DESTDIR}/etc/solr/conf
	mkdir -p ${DESTDIR}/etc/tomcat7/Catalina/localhost
	cp solr-tomcat.xml ${DESTDIR}/etc/tomcat7/Catalina/localhost/solr.xml
