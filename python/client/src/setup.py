#!/usr/bin/env python

from distutils.core import setup

setup(name='nublic',
      version='0.01',
      description='The Nublic Python Library',
      author='Alejandro Serrano',
      author_email='alex@nublic.com',
      url='http://nublic.com',
      packages=['nublic', 'nublic.filewatcher', 'nublic.files_and_users'],
      license='ALL_RIGHTS_RESERVED',
      long_description="This library provides access to all common Nublic functionality from Python",
      platforms=['all']
     )
