import urllib.request

url = 'https://belgeselx.com/arama?q=uzay'
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    html = urllib.request.urlopen(req).read().decode('utf-8', errors='ignore')
    print("arama sayfasi HTML Length:", len(html))
    print(html[35000:36000])
except Exception as e:
    print("Error:", e)
