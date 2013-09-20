import os
from pgmagick.api import Image
from nublic_server.places import ensure_cache_folder
from file_info import (FileInfo, djvu_mimes, pdf_mimes, ps_mimes, dvi_mimes,
                       office_mimes)
import subprocess

import logging
log = logging.getLogger(__name__)

SIZE = 1024
THUMBNAIL_SIZE = 96
ORIGINAL_FILENAME = "orig"
THUMBNAIL_FILENAME = "thumbnail.png"
VIEW_FILENAME = "view.pdf"


def make_tmp_file(out, prefix="tmp_"):
    return os.path.join(os.path.dirname(out), prefix + os.path.basename(out))


def pdf_to_thumb(pdf, thumb):
    """ Generates a thumb from the first page of a pdf
    from_ and to_ are bytestrings in utf8
    """
    tmp = make_tmp_file(thumb)
    img = Image(pdf + '[0]')
    img.scale("%sx%s>" % (THUMBNAIL_SIZE, THUMBNAIL_SIZE))
    # img.img.interlaceType(pgmagick.InterlaceType.LineInterlace)
    # img.img.quality(5)
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
        log.warning("Conversion failed with ddjvu for file %s", path)
    else:
        os.rename(tmp, out)


def office_view_converter(path, out):
    tmp = make_tmp_file(out)
    with open(tmp, 'w') as output:
        p = subprocess.Popen(['unoconv', '--format', 'pdf',
                              #"--timeout=24",
                              '--stdout', path],
                             stdout=output)
        #shutil.copyfileobj(p.stdout, output)
    exitcode = p.wait()
    if exitcode != 0:
        log.warning("Conversion failed with office unoconv for file %s", path)
    else:
        os.rename(tmp, out)


def dvi_view_converter(path, out):
    tmp = make_tmp_file(out)
    exitcode = subprocess.call(['dvipdf', path, tmp])
    if exitcode != 0:
        log.warning("Conversion failed with office dvipdf for file %s", path)
    else:
        os.rename(tmp, out)


def ps_view_converter(path, out):
    tmp = make_tmp_file(out)
    exitcode = subprocess.call(['ps2pdf', path, tmp])
    if exitcode != 0:
        log.warning("Conversion failed with ps2pdf for file %s", path)
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
        if info is None:
            self.info = FileInfo(original)
        else:
            self.info = info
        self.cache_path = (cache_path if cache_path
                            else ensure_cache_folder(self.original))
        log.debug("Cache path: %s", self.cache_path)
        print "Cache path: %s" % (self.cache_path,)
        self.thumb_path = os.path.join(self.cache_path, THUMBNAIL_FILENAME)
        self.view_path = os.path.join(self.cache_path, VIEW_FILENAME)

    def generate_thumb(self):
        if self.thumb_needed():
            pdf = self.generate_pdf()
            if pdf:
                pdf_to_thumb(pdf, self.thumb_path)
            else:
                return None
        return self.thumb_path

    def generate_pdf(self):
        if self.view_needed():
            if os.path.islink(self.view_path):
                os.unlink(self.view_path)
            for converter in converter_list:
                if self.info.mime_type() in converter['mimes']:
                    try:
                        converter['function'](self.original, self.view_path)
                    except:
                        log.exception(
                            "Exception detected at converter for mime %s",
                            self.info.mime_type())
                    if os.path.exists(self.view_path):
                        return self.view_path
            log.warning("No converter could be found for %s", self.original)
            return None
        else:
            return self.view_path

    def view_needed(self):
        # Check for view cache
        if not os.path.exists(self.view_path):
            return True
        original_time = self.info.last_modified_time()
        view_time = os.path.getmtime(self.view_path)
        return original_time > view_time

    def thumb_needed(self):
        # Check for thumbnail cache
        if not os.path.exists(self.thumb_path):
            return True
        original_time = self.info.last_modified_time()
        thumb_time = os.path.getmtime(self.thumb_path)
        return original_time > thumb_time

    def needs_pdf(self):
        if self.info.view_type() != 'pdf':
            return False
        return self.view_needed() and self.thumb_needed()

    def generate_view(self):
        """ Create the view for nublic """
        pdf = self.generate_pdf()
        thumb = self.generate_thumb()
        return (pdf, thumb)
