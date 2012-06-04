all:
	echo "Make nothing"

install:
	mkdir -p ${DESTDIR}/etc/apt/apt.conf.d
	cp 50unattended-upgrades ${DESTDIR}/etc/apt/apt.conf.d
	cp 10periodic ${DESTDIR}/etc/apt/apt.conf.d
