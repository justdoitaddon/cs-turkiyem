import urllib.request
import re
import ssl
ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

cx = "016376594590146270301:iwmy65ijgrm"
url = f"https://cse.google.com/cse.js?cx={cx}"
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    resp = urllib.request.urlopen(req, context=ctx).read().decode('utf-8')
    cseLibVersion = re.search(r'cselibVersion": "(.*?)"', resp).group(1)
    cseToken = re.search(r'cse_token": "(.*?)"', resp).group(1)
    print("Version:", cseLibVersion)
    print("Token:", cseToken)
    
    query = "uzay"
    search_url = f"https://cse.google.com/cse/element/v1?rsz=filtered_cse&num=10&hl=tr&source=gcsc&cselibv={cseLibVersion}&cx={cx}&q={query}&safe=off&cse_tok={cseToken}&sort=&exp=cc%2Capo&oq={query}&callback=google.search.cse.api9969&rurl=https%3A%2F%2Fbelgeselx.com%2F"
    
    req2 = urllib.request.Request(search_url, headers={'User-Agent': 'Mozilla/5.0', 'Referer': 'https://belgeselx.com/'})
    resp2 = urllib.request.urlopen(req2, context=ctx).read().decode('utf-8')
    print("Search resp:", resp2[:200])
except Exception as e:
    print("Error:", e)
