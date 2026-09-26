import urllib.request
import re

url = 'https://belgeselx.com/belgeseldizi/gizemli-tarih'
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    html = urllib.request.urlopen(req).read().decode('utf-8', errors='ignore')
    # Save the HTML to a file so we can read it
    with open('gizemli_tarih.html', 'w', encoding='utf-8') as f:
        f.write(html)
    print("Kaydedildi.")
except Exception as e:
    print("Error:", e)
