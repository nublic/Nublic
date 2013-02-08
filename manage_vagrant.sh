reload_nublic() {
    sudo apt-get update -o Dir::Etc::sourcelist="sources.list.d/nublic.list" -o Dir::Etc::sourceparts="-" -o APT::Get::List-Cleanup="0" && sudo apt-get -y --force-yes dist-upgrade
}

if [[ $* -eq 'reload_nublic' ]]; then
    reload_nublic
fi
