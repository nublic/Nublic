import os
from pgmagick.api import Image
from nublic_server.places import get_cache_folder
from file_info import (FileInfo, image_mimes)

import logging
log = logging.getLogger(__name__)

VIEW_SIZE = 1024
THUMBNAIL_SIZE = 96
ORIGINAL_FILENAME = "orig"
THUMBNAIL_FILENAME = "thumbnail.png"


def make_tmp_file(out, prefix="test_"):
    return os.path.join(os.path.dirname(out), prefix + os.path.basename(out))


def image_to_thumb(image_path, thumb):
    """Create thumbs for the image"""
    tmp = make_tmp_file(thumb)
    img = Image(image_path)
    img.scale("%sx%s>" % (THUMBNAIL_SIZE, THUMBNAIL_SIZE))
    img.write(tmp)
    os.rename(tmp, thumb)


def image_view_converter(path, out):
    """Create view for the image"""
    tmp = make_tmp_file(out)
    img = Image(path)
    img.scale("%sx%s>" % (VIEW_SIZE, VIEW_SIZE))
    img.write(tmp)
    os.rename(tmp, out)

converter_list = [
    {'mimes': image_mimes, 'function': image_view_converter}
]


class ViewConverter(object):
    def __init__(self, original, info=None, cache_path=None):
        self.original = original
        if not info:
            self.info = FileInfo(original)
        self.jpg = None
        self.cache_path_ = cache_path

    def cache_path(self):
        if not self.cache_path_:
            self.cache_path_ = get_cache_folder(self.original)
        return self.cache_path_

    def thumb_path(self, path=THUMBNAIL_FILENAME):
        return os.path.join(self.cache_path(), path)

    def view_path(self, path="view.jpg"):
        return os.path.join(self.cache_path(), path)

    def generate_thumb(self, path=None):
        if not path:
            path = self.thumb_path(THUMBNAIL_FILENAME)
        self.jpg = self.generate_view()
        image_to_thumb(self.jpg, path)
        return path

    def generate_view(self, path=None):
        if not path:
            path = self.view_path()
        if not self.jpg:
            for converter in converter_list:
                if self.info.mime_type() in converter['mimes']:
                    try:
                        converter['function'](self.original, path)
                    except:
                        log.exception(
                            "Exception detected at converter for mime %s",
                            self.info.mime_type())
                    if os.path.exists(path):
                        self.jpg = path
                        return self.jpg
            return None
        else:
            return self.jpg

    def needs_jpg(self):
        return self.info.view_type() == 'jpg'

    def view(self):
        """ Create the view for nublic """
        image = self.generate_view()
        thumb = self.generate_thumb()
        return [image, thumb]
