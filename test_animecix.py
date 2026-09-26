import urllib.request
import json

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    'x-e-h': '7Y2ozlO+QysR5w9Q6Tupmtvl9jJp7ThFH8SB+Lo7NvZjgjqRSqOgcT2v4ISM9sP10LmnlYI8WQ==.xrlyOBFS5BHjQ2Lk'
}

print('1. ADIM (ANA SAYFA):')
req1 = urllib.request.Request('https://animecix.tv/secure/last-episodes?page=1&perPage=1', headers=headers)
data1 = json.loads(urllib.request.urlopen(req1).read().decode('utf-8'))
print(json.dumps(data1, indent=2, ensure_ascii=False))

print('\n2. ADIM (ARAMA):')
req2 = urllib.request.Request('https://animecix.tv/secure/search/naruto?limit=1', headers=headers)
data2 = json.loads(urllib.request.urlopen(req2).read().decode('utf-8'))
print(json.dumps(data2, indent=2, ensure_ascii=False))

print('\n3. ADIM (DETAY):')
title_id = 9207 # Naruto
req3 = urllib.request.Request(f'https://animecix.tv/secure/titles/{title_id}?titleId={title_id}', headers=headers)
data3 = json.loads(urllib.request.urlopen(req3).read().decode('utf-8'))
print(json.dumps(data3, indent=2, ensure_ascii=False))
