#!/bin/bash

pyflakes=1 # NOT BY DEFAULT
pylint=1 # NOT BY DEFAULT
pep8=1 # NOT BY DEFAULT
TARGET_DIR="target/check"

find_python_modules_path() { # Find all python modules on $1
    find $1 -name __init__.py ! -wholename \*/sunburnt/\* -printf "`pwd`/%P:" | sed s/__init__.py//g
}

find_python_modules() { # Find all python modules on $1
    find $1 -name __init__.py \! -wholename \*/3rd-party-libs/\* -printf "`pwd`/%P " | sed s/__init__.py//g
}

all() {
    pyflakes=0
    pep8=0
    pylint=0
}

while getopts ":a" opt; do
  case $opt in
    a)  # Option for all
      all
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      exit 1
      ;;
  esac
done

mkdir -p $TARGET_DIR

if [[ $# = 0 ]]; then
    all
fi

if [[ $pyflakes = 0 ]]; then
    echo "Running pyflakes to $TARGET_DIR/pyflakes.log"
    pyflakes . > $TARGET_DIR/pyflakes.log
fi

if [[ $pep8 = 0 ]]; then
    echo "Running pep8 to $TARGET_DIR/pep8.log"
    pep8 --filename=*.py apps/ files_and_users/ filewatcher/ notification/ python/ > $TARGET_DIR/pep8.log
fi

if [[ $pylint = 0 ]]; then
    echo "Running pylint to $TARGET_DIR/pylint.log"
    touch $TARGET_DIR/pylint.dot
    PYTHONPATH=`find_python_modules_path .` pylint `find_python_modules . ` --import-graph=`pwd`/$TARGET_DIR/pylint.dot > $TARGET_DIR/pylint.log
    dot -Tpng $TARGET_DIR/pylint.dot -o $TARGET_DIR/pylint.png
    echo "Running pylint to $TARGET_DIR/pylint.html"
    PYTHONPATH=`find_python_modules_path .` pylint `find_python_modules . ` -f html > $TARGET_DIR/pylint.html
fi




