import re
with open('gizemli_tarih.html','r',encoding='utf-8') as f:
    html = f.read()

ep = re.search(r'(<a[^>]*class="px-ep-card[^>]*>.*?</a>)', html, re.DOTALL)
print('Episode HTML:', ep.group(1)[:500] if ep else 'none')
