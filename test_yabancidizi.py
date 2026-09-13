import urllib.request
import re

try:
    req = urllib.request.Request('https://yabancidizi.news', headers={'User-Agent': 'Mozilla/5.0'})
    html = urllib.request.urlopen(req).read().decode('utf-8', errors='ignore')
    
    # Try to find common item selectors (e.g. for episodes/series)
    print('Görseller:')
    imgs = re.findall(r'<img[^>]+src=[\'\"]([^\'\"]+)[\'\"]', html)
    for i in imgs[:5]:
        print('-', i)
        
    print('\nLinkler:')
    links = re.findall(r'<a[^>]+href=[\'\"](https://yabancidizi\.news/[^\'\"]+)[\'\"]', html)
    for l in links[:5]:
        print('-', l)
        
except Exception as e:
    print(f'Hata: {e}')
