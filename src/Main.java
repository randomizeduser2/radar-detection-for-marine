import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Parametre: Test modu aktif mi?
        boolean test = true;

        if (test) {
            System.out.println("[MAIN]: Test modu başlatılıyor...");
            runTestMode();
        } else {
            System.out.println("[MAIN]: Tekli işlem modu...");
            StandByDetection.processRadarImage("radar-sample.jpg");
        }
    }

    private static void runTestMode() {
        File testFolder = new File("test"); // Root'taki klasör

        if (!testFolder.exists() || !testFolder.isDirectory()) {
            System.err.println("[HATA]: 'test' klasörü bulunamadı!");
            return;
        }

        File[] images = testFolder.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));

        if (images != null && images.length > 0) {
            for (File img : images) {
                System.out.println("\n--- TEST BAŞLADI: " + img.getName() + " ---");
                StandByDetection.processRadarImage(img.getAbsolutePath());
            }
            System.out.println("\n[BİTTİ]: Tüm test resimleri işlendi.");
        } else {
            System.out.println("[BİLGİ]: Test klasöründe resim bulunamadı.");
        }
    }
}