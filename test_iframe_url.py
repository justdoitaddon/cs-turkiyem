import urllib.request
import re

url = 'https://belgeselx.com/video/data/new4.php?id=16336&sira=1'
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0', 'Referer': 'https://belgeselx.com/'})
try:
    html = urllib.request.urlopen(req).read().decode('utf-8', errors='ignore')
    print("Length:", len(html))
    match = re.search(r'file:\s*"([^"]+)",\s*label:\s*"([^"]+)"', html)
    if match:
        print("Found:", match.groups())
    else:
        print("Not found in new4.php")
except Exception as e:
    print("Error:", e)
