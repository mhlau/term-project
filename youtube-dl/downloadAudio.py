from __future__ import unicode_literals
import youtube_dl
import sys

ydl_opts = {'postprocessors':[{'key': 'FFmpegExtractAudio'}]}
video = sys.argv[1]
with youtube_dl.YoutubeDL(ydl_opts) as ydl:
	    ydl.download([video])
