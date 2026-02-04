# Radar Detection

Bu proje, görüntü İşleme ve OCR yöntemlerini kullanarak radar ekranı görüntülerinden durum tespiti yapar (örneğin "STANDBY OFF" veya "TX OFF" durumları). YOLO segmentasyon modeli ve EasyOCR kütüphanesini kullanır.

## Kurulum

1. Gerekli Python kütüphanelerini yükleyin:
   ```bash
   pip install -r requirements.txt
   ```

2. `model.pt` dosyasının proje dizininde bulunduğundan emin olun.

## Kullanım

Projeyi komut satırından aşağıdaki gibi çalıştırabilirsiniz:

```bash
python main.py <goruntu_dosyasi_yolu>
```

Örnek:
```bash
python main.py test_image.jpg
```

## Çıktı

Program çalıştırıldığında, belirtilen durumlar tespit edilirse ekrana bilgi yazdırır ve sonuç olarak `True` veya `False` döner.

## Kendi Modelinizi Eğitme

Eğer kendi modelinizi eğitmek isterseniz:

1. `train-data-roboflow.zip` dosyasını klasöre çıkartın.
2. `data.yaml` dosyasını kullanarak YOLO nano segmentasyon modeli ile 150 epoch eğitim yapın.

Örnek eğitim kodu:

```python
from ultralytics import YOLO

# YOLO nano segmentasyon modelini yükle
model = YOLO('yolov26n-seg.pt')

# Modeli eğit
model.train(data='data.yaml', epochs=150)
```
