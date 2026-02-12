#!/bin/bash

# Hata durumunda durdur
set -e

echo "[INFO] Sistem güncelleniyor..."
sudo apt-get update

echo "[INFO] Gerekli paketler yükleniyor (Python3, pip, venv, Java)..."
sudo apt-get install -y python3 python3-pip python3-venv openjdk-17-jdk libgl1 libglib2.0-0

# Python sanal ortamı oluşturma (önerilen yöntem)
echo "[INFO] Python sanal ortamı hazırlanıyor..."
if [ ! -d "python/venv" ]; then
    python3 -m venv python/venv
fi

# Sanal ortamı aktif et ve paketleri yükle
echo "[INFO] Python kütüphaneleri yükleniyor..."
source python/venv/bin/activate
pip install --upgrade pip
pip install -r python/requirements.txt

# Java derleme işlemi 
echo "[INFO] Java dosyaları derleniyor..."
mkdir -p out
javac -d out src/*.java

# Çalıştırma scripti oluştur
echo "[INFO] Çalıştırma scripti (run.sh) oluşturuluyor..."
cat << 'EOF' > run.sh
#!/bin/bash
export PYTHON_BIN="$(pwd)/python/venv/bin/python"
java -cp out Main
EOF

chmod +x run.sh

echo "[BILGI] Kurulum tamamlandı!"
echo "[BILGI] Programı başlatmak için './run.sh' komutunu kullanabilirsiniz."
