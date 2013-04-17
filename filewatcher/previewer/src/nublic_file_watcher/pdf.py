import os
from pgmagick.api import Image
from nublic_server.places import get_cache_folder
from file_info import (FileInfo, djvu_mimes, pdf_mimes, ps_mimes, dvi_mimes,
                       office_mimes)
import subprocess
import shutil

import logging
log = logging.getLogger(__name__)

SIZE = 1024
THUMBNAIL_SIZE = 96
ORIGINAL_FILENAME = "orig"
THUMBNAIL_FILENAME = "thumbnail.png"


def make_tmp_file(out, prefix="test_"):
    return os.path.join(os.path.dirname(out), prefix + os.path.basename(out))


def pdf_to_thumb(pdf, thumb):
    """ Generates a thumb from the first page of a pdf
    from_ and to_ are bytestrings in utf8
    """
    tmp = make_tmp_file(thumb)
    img = Image(pdf + '[0]')
    img.scale("%sx%s>" % (THUMBNAIL_SIZE, THUMBNAIL_SIZE))
    #img.img.interlaceType(pgmagick.InterlaceType.LineInterlace)
    #img.img.quality(5)
    img.write(tmp)
    os.rename(tmp, thumb)


def pdf_view_converter(path, out):
    """ Generate the right view of pdf for a pdf"""
    #  @TODO make symlinks relative to the mount point
    if not os.path.isabs(path):
        path = os.path.abspath(path)
    if os.path.exists(out):
        if not os.path.islink(out):
            os.remove(out)
    os.symlink(path, out)


def djvu_view_converter(path, out):
    tmp = make_tmp_file(out)
    exitcode = subprocess.call(["ddjvu", "-format=pdf", path, tmp])
    if exitcode != 0:
        log.warning("Conversion failed with ddjvu for file $s", path)
    else:
        os.rename(tmp, out)


def office_view_converter(path, out):
    tmp = make_tmp_file(out)
    p = subprocess.Popen(['unoconv', '--format', 'pdf', '--stdout', path],
                         stdout=subprocess.PIPE)
    with open(tmp, 'w') as output:
        shutil.copyfileobj(p.stdout, output)
    exitcode = p.wait()
    if exitcode != 0:
        log.warning("Conversion failed with office unoconv for file %s", path)
    else:
        os.rename(tmp, out)


def dvi_view_converter(path, out):
    tmp = make_tmp_file(out)
    exitcode = subprocess.call(['dvipdf', path, tmp])
    if exitcode != 0:
        log.warning("Conversion failed with office dvipdf for file $s", path)
    else:
        os.rename(tmp, out)


def ps_view_converter(path, out):
    tmp = make_tmp_file(out)
    exitcode = subprocess.call(['ps2pdf', path, tmp])
    if exitcode != 0:
        log.warning("Conversion failed with ps2pdf for file $s", path)
    else:
        os.rename(tmp, out)


converter_list = [
    {'mimes': ps_mimes, 'function': ps_view_converter},
    {'mimes': pdf_mimes, 'function': pdf_view_converter},
    {'mimes': office_mimes, 'function': office_view_converter},
    {'mimes': djvu_mimes, 'function': djvu_view_converter},
    {'mimes': dvi_mimes, 'function': dvi_view_converter},
]


class PdfConverter(object):
    def __init__(self, original, info=None, cache_path=None):
        self.original = original
        if not info:
            self.info = FileInfo(original)
        self.pdf = None
        self.cache_path_ = cache_path

    def cache_path(self):
        if not self.cache_path_:
            self.cache_path_ = get_cache_folder(self.original)
        return self.cache_path_

    def thumb_path(self, path=THUMBNAIL_FILENAME):
        return os.path.join(self.cache_path(), path)

    def view_path(self, path="view.pdf"):
        return os.path.join(self.cache_path(), path)

    def generate_thumb(self, path=None):
        if not path:
            path = self.thumb_path(THUMBNAIL_FILENAME)
        self.pdf = self.generate_pdf()
        pdf_to_thumb(self.pdf, path)
        return path

    def generate_pdf(self, path=None):
        if not path:
            path = self.view_path()
        if not self.pdf:
            for converter in converter_list:
                if self.info.mime_type() in converter['mimes']:
                    try:
                        converter['function'](self.original, path)
                    except:
                        log.exception("Exception detected at converter for mime %s",
                                      self.info.mime_type())
                    if os.path.exists(path):
                        self.pdf = path
                        return self.pdf
            return None
        else:
            return self.pdf

    def needs_pdf(self):
        return self.info.view_type() == 'pdf'

    def view(self):
        """ Create the view for nublic """
        pdf = self.generate_pdf()
        thumb = self.generate_thumb()
        return [pdf, thumb]
