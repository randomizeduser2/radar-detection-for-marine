# Radar Standby Tespiti

Bu proje, radar görüntülerinden "Standby" durumunun açık veya kapalı olduğunu tespit eden hibrit bir Java-Python uygulamasıdır. Görüntü işleme ve OCR işlemleri için Python (YOLOv8 + EasyOCR) kullanılırken, genel akış kontrolü ve dosya yönetimi Java üzerinden sağlanmaktadır.

## Özellikler

* YOLOv8 kullanarak görüntüdeki ilgili alanları tespit etme ve maskeleme.
* EasyOCR ile maskelenmiş alanlardaki metinleri okuma.
* "STANDBY OFF" veya "TX OFF" gibi metinleri algılayarak durum raporlama.
* Tespit sonuçlarına göre görüntüleri otomatik olarak ilgili klasörlere (true/false) arşivleme.

## Gereksinimler

Bu projeyi çalıştırmak için sisteminizde aşağıdaki yazılımların yüklü olması gerekir:

* Java Development Kit (JDK) 8 veya üzeri
* Python 3.11 (Kod içerisinde Python yolu `python3.11` olarak yapılandırılmıştır)
* Gerekli Python kütüphaneleri

## Kurulum

1. Projeyi bilgisayarınıza indirin.
2. Gerekli Python kütüphanelerini yükleyin. `python` klasörü içerisindeki `requirements.txt` dosyasını kullanabilirsiniz:

    ```bash
    pip install -r python/requirements.txt
    ```

3. `python` klasörü içerisinde `model.pt` (YOLO modeli) dosyasının bulunduğundan emin olun.

## Kullanım

Proje iki modda çalışabilir: Test modu veya tekli dosya modu. Varsayılan olarak test modunda çalışacak şekilde ayarlanmıştır.

1. İşlenmesini istediğiniz radar görüntülerini proje ana dizinindeki `test` klasörüne kopyalayın.
2. `src/Main.java` dosyasını derleyin ve çalıştırın.

Program `test` klasöründeki `.jpg` ve `.png` uzantılı dosyaları sırasıyla işleyecektir. Her bir görüntü için Python betiği çağrılır ve sonuç terminale yazdırılır.

### Sonuçların Sınıflandırılması

İşlenen görüntüler analiz sonucuna göre `radar_dataset/training_data/` dizini altında aşağıdaki alt klasörlere taşınır:

* **true**: Standby durumu tespit edilenler.
* **false**: Standby durumu tespit edilemeyenler (Kapalı).
* **failed_detections**: Nesne tespiti yapılamayan veya hata alınan görüntüler.

## Proje Yapısı

* **src/**: Java kaynak kodlarını içerir (`Main.java`, `StandByDetection.java`).
* **python/**: Görüntü işleme mantığını barındıran `main.py`, YOLO modeli (`model.pt`) ve bağımlılık listesini içerir.
* **radar_dataset/**: Eğitim verilerinin ve işlenmiş görüntülerin saklandığı dizindir.
* **test/**: Test amaçlı görüntülerin konulduğu giriş klasörüdür.
