import magic
import os
import os.path

SharedMagic = magic.open(magic.MAGIC_MIME_TYPE)
SharedMagic.load()


class FileInfo:
    def __init__(self, path):
        self.path = path
        self.extension = os.path.splitext(self.path)[1].lower()
        self._mime = None

    def path(self):
        return self.path

    def extension(self):
        return self.extension

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
            mime = SharedMagic.file(self.path)
            if mime == "application/zip":  # for Office XML docs
                if self.extension == ".docx":
                    mime = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                elif self.extension == ".xlsx":
                    mime = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                elif self.extension == ".pptx":
                    mime = "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            elif mime == "application/vnd.ms-office":  # for Office non-XML docs
                if self.extension == ".doc":
                    mime = "application/msword"
                elif self.extension == ".xls":
                    mime = "application/msexcel"
                elif self.extension == ".ppt":
                    mime = "application/mspowerpoint"
            elif mime == "application/x-staroffice" or \
                    mime == "application/soffice" or \
                    mime == "application/x-soffice":
                if self.extension == ".sdw":
                    mime = "application/vnd.stardivision.writer"
                elif self.extension == ".sdc":
                    mime = "application/vnd.stardivision.calc"
                elif self.extension == ".sdd":
                    mime = "application/vnd.stardivision.impress"
                elif self.extension == ".sda":
                    mime = "application/vnd.stardivision.draw"
            elif mime == "application/octet-stream":
                if self.extension == ".mp3":
                    mime = "audio/mpeg"
            # Save the MIME
            self._mime = mime
        # Return the cached result
        return self._mime
