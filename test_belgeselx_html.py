import urllib.request
import re

url = 'https://belgeselx.com/konu/turk-tarihi-belgeselleri'
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    html = urllib.request.urlopen(req).read().decode('utf-8', errors='ignore')
    items = re.findall(r'<div class="gen-movie-contain">', html)
    print("gen-movie-contain sayısı:", len(items))
    info = re.findall(r'<div class="gen-movie-info">', html)
    print("gen-movie-info sayısı:", len(info))
except Exception as e:
    print("Error:", e)
