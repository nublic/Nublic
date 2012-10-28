
import os
import sys
import re

try:
    from setuptools import setup
except ImportError:
    from distutils.core import setup

if sys.version_info < (2, 5):
    raise Exception("rpcbd requires Python 2.5 or higher.")

v = file(os.path.join(os.path.dirname(__file__), 'rpcbd', '__init__.py'))
VERSION = re.compile(r".*__version__ = '(.*?)'", re.S).match(v.read()).group(1)
v.close()

setup(name = "rpcbd",
      version = VERSION,
      description = "Bidirectional (symmetric) Json-Rpc library",
      author = "Rasjid Wilcox",
      author_email = "rasjidw@openminddev.net",
      url = "http://www.openminddev.net/projects/projects/show/python-rpcbd",
      packages = ['rpcbd'],
      license = "MIT License"
      )
