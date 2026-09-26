import requests
import re

url = 'https://belgeselx.com/belgesel/depo-savaslari'
headers = {'User-Agent': 'Mozilla/5.0'}
r = requests.get(url, headers=headers)
matches = re.findall(r"diziGetir\('(\d+)','(\d+)','(\d+)','(\d+)','([^']+)','[^']*','[^']*','(\d+)','(\d+)','[^']*','([^']+)'", r.text)

if matches:
    print('Found episode match:', matches[0])
    id = matches[0][0]
    ic1, ic2, ic3 = matches[0][1], matches[0][2], matches[0][3]
    
    srcMap = {'0': 'new5', '2': 'new1', '5': 'new4', '3': 'new2', '4': 'new3'}
    ics = [ic1, ic2, ic3]
    valid_ics = [ic for ic in ics if ic != '0']
    
    for idx, ic in enumerate(valid_ics):
        f = srcMap.get(ic, 'default')
        sira = idx + 1
        iframeUrl = f"https://belgeselx.com/video/data/{f}.php?id={id}&sira={sira}"
        print(f'Fetching: {iframeUrl}')
        
        ir = requests.get(iframeUrl, headers={'Referer': url, 'User-Agent': 'Mozilla/5.0'})
        files = re.findall(r'file:\s*"([^"]+)"', ir.text)
        labels = re.findall(r'label:\s*"([^"]+)"', ir.text)
        print('Files:', files, 'Labels:', labels)
        if not files:
            print("Could not find file: label:, checking for typical video tags:")
            print(re.findall(r'<source\s+src="([^"]+)"', ir.text))
            print(re.findall(r'<iframe[^>]+src="([^"]+)"', ir.text))
            print(ir.text[:500])
else:
    print('No diziGetir match found in video URL!')
    # Check what is there instead
    print(r.text[:500])
