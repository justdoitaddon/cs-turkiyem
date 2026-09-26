import re

with open('gizemli_tarih_ep.html', 'r', encoding='utf-8') as f:
    html = f.read()

scripts = [x for x in re.findall(r'<script[^>]*>(.*?)</script>', html, re.DOTALL) if 'vF1' in x or 'new4' in x or 'php' in x or 'id' in x or '16336' in x]

with open('scripts_out.txt', 'w', encoding='utf-8') as f:
    for s in scripts:
        f.write(s + '\n\n')
