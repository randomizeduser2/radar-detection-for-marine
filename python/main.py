import cv2
import numpy as np
import easyocr
import re
from ultralytics import YOLO
import sys 

reader = easyocr.Reader(['en']) 

def isStandBy(image_path, model_name='model.pt'):
    # 1. Modeli Yükle
    model = YOLO(model_name)
    img = cv2.imread(image_path)
    
    if img is None:
        return False

    # 2. Tahmin Yap daireyi prediction yapıyorum
    results = model.predict(source=img, conf=0.5, verbose=False)[0]

    # Eğer daire yoksa direkt çık
    if results.masks is None:
        return None

    # STANDBY OFF, TX OFF, STANDBY-OFF gibi ihtimalleri kapsayacak
    standby_patterns = re.compile(r"(STANDBY\s*OFF|TX\s*OFF)", re.IGNORECASE)

    for i, poly_points in enumerate(results.masks.xy):
        # Segment Edilen Daireyi Poligon Noktalarına Dönüştür
        poly_points = np.array(poly_points, dtype=np.int32)
        
        # Nesnenin etrafındaki en küçük kareyi bul (Bounding Box)
        x, y, w, h = cv2.boundingRect(poly_points)
        
        # Sadece o bölgeyi maskele (Arka plan gürültüsünü temizlemek için)
        mask = np.zeros(img.shape[:2], dtype=np.uint8)
        cv2.fillPoly(mask, [poly_points], 255)
        
        # Maskelenmiş resmi al ve nesnenin olduğu kutuyu kes
        masked_img = cv2.bitwise_and(img, img, mask=mask)
        crop_img = masked_img[y:y+h, x:x+w]

        # EasyOCR ile Metin Okuma
        ocr_results = reader.readtext(crop_img, detail=0)
        detected_text = " ".join(ocr_results).upper()
        
        # Regex Kontrolü
        if standby_patterns.search(detected_text):
            # Java'nın veya Terminalin yakalaması için kritik bilgiyi basıyoruz
            print(f"[*]: STANDBY durum tespit edildi! ({detected_text})")
            return True 

    return False

if __name__ == "__main__":
    if len(sys.argv) > 1:
        file_path = sys.argv[1]
        result = isStandBy(file_path)
        # Java tarafının kolayca okuması için en son satıra ham sonucu basıyoruz
        print(result)
    else:
        print(None)
        #print("Hata: Dosya yolu belirtilmedi.")
        #print("Kullanım: python main.py <dosya_yolu>")