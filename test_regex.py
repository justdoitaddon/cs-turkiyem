import re
with open('gizemli_tarih.html','r',encoding='utf-8') as f:
    html = f.read()

title=re.search(r'class="px-hero-title"[^>]*>(.*?)</', html)
print('Title:', title.group(1) if title else 'none')

poster=re.search(r'class="px-dizi-poster".*?<img[^>]*src="(.*?)"', html, re.DOTALL)
if not poster:
    poster=re.search(r'class="px-dizi-card-poster".*?<img[^>]*src="(.*?)"', html, re.DOTALL)
print('Poster:', poster.group(1) if poster else 'none')

desc=re.search(r'class="px-hero-desc"[^>]*>(.*?)</', html)
print('Desc:', desc.group(1) if desc else 'none')

eps=re.findall(r'class="px-ep-card', html)
print('Eps:', len(eps))
