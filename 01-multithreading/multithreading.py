import threading
import time
import random

# Senaryo:
# 1. Thread (Video_Render): İşlemciyi meşgul eden bir iş (CPU Bound).
# 2. Thread (Dosya_Indirme): Diskten veya ağdan veri bekleyen bir iş (I/O Bound).

def cpu_yogun_is():
    """Sürekli hesaplama yapan ve CPU'yu meşgul eden fonksiyon."""
    isim = threading.current_thread().name
    print(f"[{isim}] Başladı. (CPU Kullanılıyor...)")
    
    # Basit bir hesaplama döngüsü (5 adım)
    for i in range(1, 6):
        print(f"[{isim}] Hesaplama yapılıyor: %{i * 20}")
        # CPU'nun bu işlemde zaman harcadığını simüle etmek için kısa bir bekleme
        time.sleep(0.5)
        
    print(f"[{isim}] Bitti.")

def io_bekleyen_is():
    """Veri bekleyen (Input/Output) fonksiyon."""
    isim = threading.current_thread().name
    print(f"[{isim}] Başladı. (Veri bekleniyor...)")
    
    # I/O beklemesi simülasyonu
    print(f"[{isim}] --> KESİNTİ (Interrupt): Ağdan veri bekleniyor... (BLOCKED)")
    
    # İşletim sistemi bu noktada bu işi "uyutur" ve CPU'yu diğer işe verir.
    # 2.5 saniye boyunca veri gelmesini bekliyoruz.
    time.sleep(2.5)
    
    print(f"[{isim}] --> KESİNTİ: Veri geldi! İşleme devam ediliyor. (READY -> RUNNING)")
    print(f"[{isim}] Bitti.")

if __name__ == "__main__":
    print("--- İşletim Sistemi Simülasyonu (Python) Başlatılıyor ---\n")

    # İş parçacıklarını (Thread) oluşturuyoruz
    t1 = threading.Thread(target=cpu_yogun_is, name="Process-1 (Video Render)")
    t2 = threading.Thread(target=io_bekleyen_is, name="Process-2 (Dosya İndirme)")

    # İşlemleri başlat (Ready Queue'ya al)
    t1.start()
    t2.start()

    # Ana programın, thread'ler bitene kadar beklemesini sağla
    t1.join()
    t2.join()

    print("\n--- Tüm işlemler tamamlandı ---")