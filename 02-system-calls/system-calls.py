import os

# NOT: Bu kod, notlarda anlatılan "System Call" (Sistem Çağrısı) mantığını gösterir.
# Yüksek seviyeli "open()" fonksiyonu yerine, çekirdek seviyesine daha yakın olan
# os.open(), os.write() gibi fonksiyonları kullanıyoruz.

def dusuk_seviye_dosya_islemleri():
    dosya_adi = "sistem_cagrisi_test.txt"
    veri = b"Bu veri dogrudan Kernel uzerinden yazildi!\nSistem Cagrilari: open -> write -> close"

    print("--- 1. Dosya Oluşturuluyor (Sistem Çağrısı: open) ---")
    # os.open, bir dosya nesnesi değil, bir "Dosya Tanımlayıcısı" (File Descriptor - tamsayı) döner.
    # O_CREAT: Yoksa oluştur, O_RDWR: Oku ve Yaz, O_TRUNC: Varsa içeriği sil
    fd = os.open(dosya_adi, os.O_CREAT | os.O_RDWR | os.O_TRUNC)
    print(f"Çekirdekten dönen Dosya Tanımlayıcısı (File Descriptor ID): {fd}")

    print("\n--- 2. Dosyaya Yazılıyor (Sistem Çağrısı: write) ---")
    # os.write doğrudan byte verisi yazar. print() gibi formatlama yapmaz.
    yazilan_byte = os.write(fd, veri)
    print(f"{yazilan_byte} byte veri diske yazıldı.")

    print("\n--- 3. Dosya Başa Sarılıyor (Sistem Çağrısı: lseek) ---")
    # Dosya imlecini başa (0. byte) al
    os.lseek(fd, 0, 0)

    print("\n--- 4. Dosyadan Okunuyor (Sistem Çağrısı: read) ---")
    # Dosyadan 100 byte oku
    okunan_veri = os.read(fd, 100)
    print(f"Okunan Ham Veri: {okunan_veri}")
    print(f"Decode Edilmiş: \n{okunan_veri.decode()}")

    print("\n--- 5. Dosya Kapatılıyor (Sistem Çağrısı: close) ---")
    # Dosya tanımlayıcısını serbest bırak
    os.close(fd)
    print("Dosya başarıyla kapatıldı.")

    # Temizlik (İsteğe bağlı)
    # os.remove(dosya_adi)

if __name__ == "__main__":
    try:
        dusuk_seviye_dosya_islemleri()
    except OSError as e:
        print(f"Sistem çağrısı hatası: {e}")