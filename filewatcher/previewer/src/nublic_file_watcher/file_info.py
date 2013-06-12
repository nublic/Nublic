import magic
import os
import os.path

SharedMagic = magic.open(magic.MAGIC_MIME_TYPE)
SharedMagic.load()

audio_mimes = [
    # Obtained looking at:
    # - List of files supported by ffmpeg: `ffmpeg -formats`
    # - Information about file extensions: http://filext.com/
    #
    # AAC
    "audio/aac", "audio/x-aac",
    # AC3
    "audio/ac3",
    # AIFF
    "audio/aiff", "audio/x-aiff", "sound/aiff",
    "audio/x-pn-aiff",
    # ASF
    "audio/asf",
    # MIDI
    "audio/mid", "audio/x-midi",
    # AU
    "audio/basic", "audio/x-basic", "audio/au",
    "audio/x-au", "audio/x-pn-au", "audio/x-ulaw",
    # PCM
    "application/x-pcm",
    # MP4
    "audio/mp4",
    # MP3
    "audio/mpeg", "audio/x-mpeg", "audio/mp3",
    "audio/x-mp3", "audio/mpeg3", "audio/x-mpeg3",
    "audio/mpg", "audio/x-mpg", "audio/x-mpegaudio",
    # WAV
    "audio/wav", "audio/x-wav", "audio/wave",
    "audio/x-pn-wav",
    # OGG
    "audio/ogg", "application/ogg", "audio/x-ogg",
    "application/x-ogg",
    # FLAC
    "audio/flac",
    # WMA
    "audio/x-ms-wma",
    # Various
    "audio/rmf", "audio/x-rmf", "audio/vnd.qcelp",
    "audio/x-gsm", "audio/snd"
]

djvu_mimes = ["image/vnd.djvu"]
pdf_mimes = [
    "application/pdf", "application/x-pdf",
    "application/acrobat", "applications/vnd.pdf",
    "text/pdf", "text/x-pdf"
]
ps_mimes = [
    "application/postscript", "application/ps",
    "application/x-postscript", "application/x-ps"
]
dvi_mimes = ["application/dvi", "application/x-dvi"]

general_office_mimes = [
    # Microsoft Office
    "application/vnd.ms-office",
    # General for old StarOffice
    "application/x-staroffice", "application/soffice", "application/x-soffice"
]

word_mimes = [
    # Word .doc
    "application/msword", "application/doc",
    "application/vnd.msword", "application/vnd.ms-word",
    "application/winword", "application/word",
    "application/x-msw6", "application/x-msword",
    # Word .docx
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    # Writer .odt
    "application/vnd.oasis.opendocument.text",
    "application/x-vnd.oasis.opendocument.text",
    # Writer .sxw
    "application/vnd.sun.xml.writer",
    # Writer .sdw
    "application/x-swriter", "application/vnd.stardivision.writer",
    # Rich Text Format .rtf
    "application/rtf", "application/x-rtf", "text/rtf", "text/richtext"
]

spreadsheet_mimes = [
    # Excel .xls
    "application/vnd.ms-excel", "application/msexcel",
    "application/x-msexcel", "application/x-ms-excel",
    "application/vnd.ms-excel", "application/x-excel",
    "application/x-dos_ms_excel", "application/xls",
    # Excel .xlsx
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    # Calc .ods
    "application/vnd.oasis.opendocument.spreadsheet",
    "application/x-vnd.oasis.opendocument.spreadsheet",
    # Calc .sxc
    "application/vnd.sun.xml.calc",
    # Calc .sdc
    "application/vnd.stardivision.calc"
]

presentation_mimes = [
    # PowerPoint .ppt
    "application/vnd.ms-powerpoint", "application/mspowerpoint",
    "application/ms-powerpoint", "application/mspowerpnt",
    "application/vnd-mspowerpoint", "application/powerpoint",
    "application/x-powerpoint",
    # PowerPoint .pptx
    ("application/vnd.openxmlformats-officedocument."
    "presentationml.presentation"),
    # Impress .odp
    "application/vnd.oasis.opendocument.presentation",
    "application/x-vnd.oasis.opendocument.presentation",
    # Impress .sxi
    "application/vnd.sun.xml.impress",
    # Impress .sdd
    "application/vnd.stardivision.impress"
]

drawing_mimes = [
    # Draw .odg
    "application/vnd.oasis.opendocument.graphics",
    "application/x-vnd.oasis.opendocument.graphics",
    # Draw .sxd
    "application/vnd.sun.xml.draw",
    # Draw .sda
    "application/x-sdraw", "application/x-sda",
    "application/vnd.stardivision.draw"
]

video_mimes = [
    # Obtained looking at:
    # - List of files supported by ffmpeg: `ffmpeg -formats`
    # - Information about file extensions: http://filext.com/
    #
    # AVI
    "video/avi", "video/msvideo", "video/x-msvideo",
    "image/avi", "video/xmpg2", "application/x-troff-msvideo",
    # MPEG
    "video/mpeg", "video/mpg", "video/x-mpg",
    "video/mpeg2", "application/x-pn-mpg", "video/x-mpeg",
    "video/x-mpeg2a",
    # ASF, WMV
    "video/x-ms-asf-plugin", "application/x-mplayer2", "video/x-ms-asf",
    "application/vnd.ms-asf", "video/x-ms-asf-plugin", "video/x-ms-wm",
    "video/x-ms-wmx", "video/x-ms-wmv",
    # FLV
    "video/x-flv",
    # MOV
    "video/quicktime", "video/x-quicktime",
    # DV
    "video/x-dv",
    # MP4
    "video/mp4v-es"
    # MKV
    # no known mime type
]

image_mimes = [
    "image/bmp", "image/gif", "image/png",
    "image/jpg", "image/jpeg", "image/pjpeg",
    "image/svg", "image/x-icon", "image/x-pict",
    "image/x-pcx", "image/pict", "image/x-portable-bitmap",
    "image/tiff", "image/x-tiff", "image/x-xbitmap",
    "image/x-xbm", "image/xbm", "application/wmf",
    "application/x-wmf", "image/wmf", "image/x-wmf",
    "image/x-ms-bmp"
]

supported_languages = [
    "java", "python", "h", "c", "fortran", "script", "pascal", "perl",
    "scheme", "lisp", "haskell", "guile", "diff", "clojure", "scala",
    "javascript", "js", "lua", "r", "ruby", "smalltak", "tex", "stex",
    "csrc", "c++src", "groovy", "httpd-php", "php", "plsql", "strc",
    "smalltalk", "yaml",
    "script.perl", "script.python", "script.scheme", "script.guile",
    "script.sh", "script.tcl", "script.tcsh", "script.zsh"
]
programming_mimes = ["text/x-" + l for l in supported_languages]

text_mimes = [
    "text/plain", "text/troff",
    "text/html", "text/css", "text/xml", "text/sgml",
    "text/javascript", "application/json"
]

audio_view_mimes = (audio_mimes)
office_mimes = (general_office_mimes + word_mimes + spreadsheet_mimes +
                presentation_mimes + drawing_mimes)
pdf_view_mimes = (djvu_mimes + pdf_mimes + dvi_mimes + ps_mimes + office_mimes)
video_view_mimes = (video_mimes)
text_view_mimes = (text_mimes + programming_mimes)
image_view_mimes = (image_mimes)

view_mimes = [
    {'view': 'mp3', 'mimes': audio_view_mimes},
    {'view': 'pdf', 'mimes': pdf_view_mimes},
    {'view': 'txt', 'mimes': text_view_mimes},
    {'view': 'mp4', 'mimes': video_view_mimes},
    # @TODO Before was flv instead of mp4!
    {'view': 'png', 'mimes': image_view_mimes}
]


def view_type(mime):
    ''' Get the view associated to the mime
    given or from the current file'''
    for view in view_mimes:
        if mime in view['mimes']:
            return view['view']
    return None


def guess_mime_from_ext(mime, extension):
    ''' For generic mime types improves the guess by using the extension
    '''
    if mime == "application/zip":  # for Office XML docs
        if extension == ".docx":
            mime = ("application/vnd.openxmlformats-officedocument."
                    "wordprocessingml.document")
        elif extension == ".xlsx":
            mime = ("application/vnd.openxmlformats-officedocument."
                    "spreadsheetml.sheet")
        elif extension == ".pptx":
            mime = ("application/vnd.openxmlformats-officedocument."
                    "presentationml.presentation")
    elif mime == "application/vnd.ms-office":  # for Office non-XML docs
        if extension == ".doc":
            mime = "application/msword"
        elif extension == ".xls":
            mime = "application/msexcel"
        elif extension == ".ppt":
            mime = "application/mspowerpoint"
    elif (mime == "application/x-staroffice" or
            mime == "application/soffice" or
            mime == "application/x-soffice"):
        if extension == ".sdw":
            mime = "application/vnd.stardivision.writer"
        elif extension == ".sdc":
            mime = "application/vnd.stardivision.calc"
        elif extension == ".sdd":
            mime = "application/vnd.stardivision.impress"
        elif extension == ".sda":
            mime = "application/vnd.stardivision.draw"
    elif mime == "application/octet-stream":
        if extension == ".mp3":
            mime = "audio/mpeg"
    return mime


class FileInfo(object):
    def __init__(self, path):
        self.path = path
        self.extension = os.path.splitext(self.path)[1].lower()
        self._mime = None

    def size(self):
        return os.path.getsize(self.path)

    def is_directory(self):
        return os.path.isdir(self.path)

    def last_access_time(self):
        return os.path.getatime(self.path)

    def last_modified_time(self):
        return os.path.getmtime(self.path)

    def mime_type(self):
        if self._mime is None:
            mime_raw = SharedMagic.file(self.path)
            mime = guess_mime_from_ext(mime_raw, self.extension)
            # Save the MIME
            self._mime = mime
        # Return the cached result
        return self._mime

    def view_type(self):
        ''' Get the view associated to the mime
        given or from the current file'''
        mime = self.mime_type()
        return view_type(mime)
