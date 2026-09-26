import requests
import re

url = 'https://belgeselx.com/belgeseldizi/atlantis'
headers = {'User-Agent': 'Mozilla/5.0'}
r = requests.get(url, headers=headers)
with open('belgeselx_sample.html', 'w', encoding='utf-8') as f:
    f.write(r.text)
print("Saved to belgeselx_sample.html")
