import re
with open('gizemli_tarih_ep.html','r',encoding='utf-8') as f:
    html = f.read()

# iframe search
iframe = re.search(r'<iframe[^>]*src="([^"]+)"', html)
if iframe:
    print('Iframe:', iframe.group(1))
else:
    print('No iframe found.')

# Check for .php?id= in the text
php_id = re.search(r'new4\.php\?id=(\d+)', html)
if php_id:
    print('PHP ID:', php_id.group(1))
else:
    # Any other php?id= or data-episode= or butonKaydet
    match = re.search(r'data-id="(\d+)"|data-episode="(\d+)"|id="(\d+)"', html)
    print('Alternative Match:', match.groups() if match else 'None')
