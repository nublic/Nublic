unset DEBCONF_REDIR
unset DEBCONF_FRONTEND
unset DEBIAN_HAS_FRONTEND
unset DEBIAN_FRONTEND

# We know that debian-installer will copy the nublic repo in /root/nublic-repo
echo "deb file:/root/nublic-repo oneiric nublic" > /etc/apt/sources.list.d/nublic-temp.list
apt-get update
apt-get --force-yes --yes install nublic
rm /etc/apt/sources.list.d/nublic-temp.list
# Here we have to add information about real nublic repo
