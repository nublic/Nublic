#!/bin/bash

assert_equals() { # $1 result, $2 expected value, $3 error message, $4 extra error lines
    if [ $1 -ne $2 ]; then
        echo ""
        echo "ERROR: $3"
        echo "       $4"
    else
        echo -n "."
    fi
}

assert_not_equals() { # $1 result, $2 expected value, $3 error message, $4 extra error lines
    if [ $1 -eq $2 ]; then
        echo ""
        echo "ERROR: $3"
        echo "       $4"
    else
        echo -n "."
    fi
}

print_if_error() {
    assert_not_equals "$1" 1 "$2" "$3"
}

print_if_no_error() {
    assert_not_equals "$1" 0 "$2" "$3"
}

assert_found() {  # $1 error, $2 pattern to match, $3 file
    grep "$2" "$3" > /dev/null
    print_if_error $? "$1" "$2 not found"
}
assert_not_found() {  # $1 error, $2 pattern to match, $3 file
    grep "$2" "$3" > /dev/null
    print_if_no_error $? "$1" "$2 found"
}

INI_DIR=/tmp/test/filewatcher-internal
DATA_DIR=${INI_DIR}/data
CONFIG_FILE=${INI_DIR}/config_debug.cfg
LOG_FILE=${INI_DIR}/daemon.log
SEND_FILE=${INI_DIR}/send.log
# Clean
rm -rf ${DATA_DIR}
# Prepare tests
echo "--------------------------" > ${LOG_FILE}
echo "Starting test" >> ${LOG_FILE}
mkdir -p  ${DATA_DIR}
cat > ${CONFIG_FILE} << DEBUG
[filewatcher-internal]
directory = /tmp/test/filewatcher-internal/data
listen_port = 12000

DEBUG
echo "Starting daemon NOW" >> ${LOG_FILE}
nublic-file-watcher-internal-daemon --config ${CONFIG_FILE} &>> ${LOG_FILE} &
pid=$!
sleep 3
echo "connecting a 'test app' to the port" >> ${LOG_FILE}
echo "test" | nc localhost 12000 > ${SEND_FILE} &
sleep 1
# Do stuff
echo "test create file 1" >> ${LOG_FILE}
echo "hola" >> ${DATA_DIR}/test_create
echo "hola" >> "${DATA_DIR}/test space"
touch ${DATA_DIR}/test_empty
# Fast test
max=1000
echo "test create file: create $max files" >> ${LOG_FILE}
for i in `seq $max` ; do
    echo "hola $i" >> ${DATA_DIR}/test_num_$i
done
# Test unicode
echo "test create file: create unicode filenames" >> ${LOG_FILE}
echo "hola" >> ${DATA_DIR}/test_ñ
echo "hola" >> ${DATA_DIR}/test_ú
echo "hola" >> ${DATA_DIR}/test_ç
# Directories test
mkdir ${DATA_DIR}/dir
mkdir "${DATA_DIR}/dir space"
# Unicode directories test
mkdir ${DATA_DIR}/dir_ñ
mkdir ${DATA_DIR}/ú
mkdir ${DATA_DIR}/ü
mkdir ${DATA_DIR}/Música
# Copying test
for i in `seq $max` ; do
    echo "hola $i" >> ${DATA_DIR}/test_for_copy_$i
done
for i in `seq $max` ; do
    cp ${DATA_DIR}/test_for_copy_$i ${DATA_DIR}/dir_ñ/
done
# Moving test
for i in `seq $max` ; do
    echo "hola $i" >> ${DATA_DIR}/test_for_move_$i
done
for i in `seq $max` ; do
    mv ${DATA_DIR}/test_for_move_$i ${DATA_DIR}/Música
done
# Editing test
for i in `seq 50` ; do
    echo "$i" >> ${DATA_DIR}/dir_ñ/test_mod
done
# Massive create fast
for i in `seq 50` ; do
    touch ${DATA_DIR}/ü/$i
done
# Create and delete fast test
touch ${DATA_DIR}/rëmoving
rm ${DATA_DIR}/rëmoving
mkdir ${DATA_DIR}/rëmove
touch ${DATA_DIR}/rëmove/äcëdècé  # Undetected, watch not in place yet
rm -r ${DATA_DIR}/rëmove
# Move folders
mv ${DATA_DIR}/{ú,ó}
# Change folders
mkdir ${DATA_DIR}/chängè
for i in `seq 100` ; do
    touch ${DATA_DIR}/chängè/$i
done
mv ${DATA_DIR}/{chängè,lâst}
touch ${DATA_DIR}/lâst/lôcô
# Check directories with . on them
touch ${DATA_DIR}/.hidden_file
mkdir ${DATA_DIR}/.hidden
touch ${DATA_DIR}/.hidden/file
touch ${DATA_DIR}/editado.txt~
touch ${DATA_DIR}/text.txt

sleep 4
# Tests for all the previous lines
assert_found "Create file failed" '{"isdir": false, "src_pathname": "", "pathname": "/tmp/test/filewatcher-internal/data/test_create", "ty": "modify"}' ${SEND_FILE}
assert_found "Create file with space failed" '{"isdir": false, "src_pathname": "", "pathname": "/tmp/test/filewatcher-internal/data/test space", "ty": "modify"}' ${SEND_FILE}
assert_not_found "exception detected" "exception" ${LOG_FILE}
assert_not_found "Error detected" "Error found in ${LOG_FILE}" ${LOG_FILE}
assert_not_found "error detected" "error found in ${LOG_FILE}" ${LOG_FILE}
assert_not_found "ERROR detected" "ERROR found in ${LOG_FILE}" ${LOG_FILE}
assert_not_found "xcep detected" "xcep found in ${LOG_FILE}" ${LOG_FILE}
assert_not_found "Traceback detected" "Traceback found in ${LOG_FILE}" ${LOG_FILE}
assert_not_found ".hidden_file found" ".hidden_file" ${SEND_FILE}
assert_not_found ".hidden folder found" ".hidden" ${SEND_FILE}
assert_not_found "file inside .hidden folder found" ".hidden/file" ${SEND_FILE}
assert_not_found "editado.txt~ found" "editado.txt~" ${SEND_FILE}
assert_found "File text.txt undetected" "text.txt" ${SEND_FILE}
assert_found "Move file to utf8 directory failed" '{"isdir": false, "src_pathname": "/tmp/test/filewatcher-internal/data/test_for_move_1000", "pathname": "/tmp/test/filewatcher-internal/data/M\\u00fasica/test_for_move_1000", "ty": "move"}' ${SEND_FILE}
# Test for massive scripts
assert_equals `grep 'test_num_' ${SEND_FILE} | wc -l` $(( $max * 2 )) "Creating $max files failed." "Wrong number of entries found"
assert_equals `grep 'test_for_move_' ${SEND_FILE} | wc -l` $(( $max * 3 )) "Moving $max files failed." "Wrong number of entries found"
# TODO: Test for scanned directories
# TODO: Test for missing files on newly created directories

# Clean
kill $pid
echo ""
