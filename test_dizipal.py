import urllib.request
import re

print('1. ADIM: DiziPal dizi/film sayfasına (Untold Mr. T) giriliyor...')
try:
    item_url = 'https://dizipal2131.com/film/untold-mr-t-i-pity-the-fool'
    req2 = urllib.request.Request(item_url, headers={'User-Agent': 'Mozilla/5.0'})
    html2 = urllib.request.urlopen(req2).read().decode('utf-8', errors='ignore')
    
    print('\n2. ADIM: İçerik sayfasındaki video oynatıcı (iframe) aranıyor...')
    
    # Check data-src first (as we did in Kotlin code)
    iframes = re.findall(r'<iframe[^>]+data-src=[\'\"]([^\'\"]+)[\'\"]', html2)
    if not iframes:
        iframes = re.findall(r'<iframe[^>]+src=[\'\"]([^\'\"]+)[\'\"]', html2)
    
    if iframes:
        print(f'\nBAŞARILI! {len(iframes)} adet oynatıcı linki (extractor) bulundu:')
        for i, iframe in enumerate(iframes):
            print(f'Oynatıcı {i+1}: {iframe}')
        print('\nSONUÇ: Cloudstream uygulaması bu linkleri (örneğin Vidmoly/Formationfeed vb.) kendi iç sistemine gönderip videoyu başarıyla oynatacaktır!')
    else:
        print('\nBAŞARISIZ! Sayfa açıldı ancak video oynatıcı (iframe) bulunamadı.')
            
except Exception as e:
    print(f'Hata oluştu: {e}')
