import re

with open('belgeselx_main.html', 'r', encoding='utf-8') as f:
    text = f.read()

links = re.findall(r'href="([^"]+)"', text)
for l in links[:50]:
    if 'belgesel' in l or 'bolum' in l or 'konu' in l:
        print(l)
