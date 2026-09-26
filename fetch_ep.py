import urllib.request
import re

url = 'https://belgeselx.com/belgesel/gizemli-tarih'
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    html = urllib.request.urlopen(req).read().decode('utf-8', errors='ignore')
    with open('gizemli_tarih_ep.html', 'w', encoding='utf-8') as f:
        f.write(html)
    print("Episode Kaydedildi.")
except Exception as e:
    print("Error:", e)
