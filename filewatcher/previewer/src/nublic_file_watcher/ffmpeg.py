import sys
import os
import os.path
from converter import Converter
import shlex
import subprocess
#import pexpect

#log = logging.getLogger(__name__)

ROOT_FOLDER = "/var/nublic/cache/music"
ARTISTS_FOLDER = os.path.join(ROOT_FOLDER, "artists")
ALBUMS_FOLDER = os.path.join(ROOT_FOLDER, "albums")
ORIGINAL_FILENAME = "orig"
THUMBNAIL_FILENAME = "thumb.png"
THUMBNAIL_SIZE = 96
LOG_FILE = "/var/log/nublic/ffmpeg.log"


class NublicConverter(object):
    def __init__(self, resolution_x=640, resolution_y=360, f_format="mp4",
                 vcodec="libx264", maxrate=350, crf=28, acodec='libvo_aacenc',
                 a_bitrate=128, samplerate=48000):
        self.op = {}
        self.op['resolution_x'] = resolution_x
        self.op['resolution_y'] = resolution_y
        self.op['f_format'] = f_format
        self.op['vcodec'] = vcodec
        self.op['maxrate'] = maxrate
        self.op['crf'] = crf
        self.op['acodec'] = acodec
        self.op['a_bitrate'] = a_bitrate
        self.op['samplerate'] = samplerate
        self.out = self.op

    def probe(self, path):
        ''' Probe an audio/video file
        It must exist. It will be added to the options
        '''
        c = Converter()
        info = c.probe(path)
        self.out['in_file'] = path
        return info

    def recommended_options(self, path, info=None):
        if not info:
            info = self.probe(path)
        if info.video:
            return self.video_recommended_options(path, info)
        elif info.audio:
            return self.audio_recommended_options(path, info)
        return None

    def video_recommended_options(self, path, info=None):
        if not info:
            info = self.probe(path)
        #if 0 <= len(info.streams) <= 2:
        #    log.warn('Incorrect number of streams %s', len(info.streams))

        if info.audio:
            self.out['samplerate'] = min(info.audio.audio_samplerate,
                                         self.op['samplerate'])
        if info.video:
            self.out['resolution_x'] = min(info.video.video_width,
                                           self.op['resolution_x'])
            self.out['resolution_y'] = min(info.video.video_height,
                                           self.op['resolution_y'])
        return self.out

    def audio_recommended_options(self, path, info=None):
        if not info:
            info = self.probe(path)
        if info.audio:
            self.out['samplerate'] = min(info.audio.audio_samplerate,
                                         self.op['samplerate'])

        return self.out

    def output_file(self, path):
        self.out['out_file'] = path

    def command_audio(self):
        ''' Returns the command optimal for audio files'''
        com = ("nice ffmpeg -i '{in_file}' -f {f_format} -acodec libvo_aacenc "
               "-ar {samplerate} -ab {a_bitrate}K -threads 2 -y '{out_file}' ")
        return com.format(**self.out)

    def command_video(self):
        ''' Returns the command optimal for video files'''
        com = ("nice ffmpeg -i '{in_file}' -s {resolution_x}x{resolution_y} "
               "-strict experimental -vcodec {vcodec} -f {f_format} "
               "-coder 0 -bf 0 -refs 1 -flags2 -wpred-dct8x8 -level 30 "
               "-crf {crf} -bufsize 4000k -maxrate {maxrate}k "
               "-preset medium -r 15 -acodec libvo_aacenc "
               "-ar {samplerate} -ab {a_bitrate}K -threads 2 -y '{out_file}'")
        return com.format(**self.out)

    def ffmpeg_audio_convert(self, logfile=None):
#        return pexpect.run(self.command_audio(), withexitstatus=True,
#                           logfile=logfile)
        pass

    def ffmpeg_video_convert(self, logfile=None):
        output = file(LOG_FILE, 'a')
        args = shlex.split(self.command_video())
        return subprocess.call(args, stdout=output, stderr=output)
        #timeout = ""
        #print self.command_video()
        #return pexpect.run(self.command_video(), withexitstatus=True,
        #                   logfile=logfile)


def ffmpeg_convert(in_file, out_file, logfile=None):
    c = NublicConverter()
    options = c.recommended_options(in_file)
    print str(options)
    c.output_file(out_file)
    #timeout = ""
    output, exit = c.ffmpeg_video_convert()
    print output
    print exit
    if exit != 0:
        #log.error("Conversion unsuccessful of file %s: Options were %s",
        #          in_file, str(options))
        pass
    return True

if __name__ == '__main__':
    argv = sys.argv
    #ffmpeg_convert(argv[1], argv[2])

    sys.exit(0)
