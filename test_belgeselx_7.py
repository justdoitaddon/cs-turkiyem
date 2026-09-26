import re

with open('belgeselx_main.html', 'r', encoding='utf-8') as f:
    html = f.read()

urls = re.findall(r'href="([^"]+)"', html)
for url in urls:
    if url.startswith('https://belgeselx.com/') and len(url) > 23 and 'belgeselkanali' not in url and 'konu' not in url:
        print(url)
