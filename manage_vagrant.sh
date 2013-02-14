reload_nublic() {
    sudo apt-get update -o Dir::Etc::sourcelist="sources.list.d/nublic.list" -o Dir::Etc::sourceparts="-" -o APT::Get::List-Cleanup="0" && sudo apt-get -y --force-yes dist-upgrade
}

if [[ $1 = 'reload_nublic' ]]; then
    reload_nublic
fi

if [[ $1 = 'test' ]]; then
    echo "---------------------------------"
    echo "Testing filewatcher-internal"
    /opt/code/filewatcher/internal/src/test_file_watcher_internal.sh
    echo "Finished testing filewatcher-internal"; echo " "
fi
