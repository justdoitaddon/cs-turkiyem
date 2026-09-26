import re
import urllib.request

html = open('cizgimax_detail.html', 'r', encoding='utf-8').read()

eps = re.findall(r'<a[^>]*href="([^"]+bolum-izle/)[^"]*"[^>]*class="ep-num-btn"[^>]*>.*?<span class="ep-num-label">([^<]+)</span>', html, re.DOTALL)
print("Eps:", len(eps), eps[:5])

title = re.search(r'class="anime-title-link"[^>]*>(.*?)</a>', html)
print("Title:", title.group(1) if title else 'none')

# Check tags
tags = re.findall(r'href="/ara/\?genre=[^"]+">([^<]+)</a>', html)
if not tags:
    tags = re.findall(r'class="cz-c-genre"[^>]*>(.*?)<', html)
print("Tags:", tags)
