import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class StandByDetection {

    private static final String PYTHON_PATH_ENV     = "PYTHON_BIN";
    private static final String PYTHON_FOLDER       = "python";
    private static final String SCRIPT_NAME         = "main.py";
    private static final String TRAINING_DATA_DIR   = "radar_dataset/training_data/";

    public static void processRadarImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            String absoluteImagePath = imageFile.getAbsolutePath();

            String pythonExecutable = resolvePythonExecutable();
            ProcessBuilder pb = new ProcessBuilder(pythonExecutable, SCRIPT_NAME, absoluteImagePath);
            pb.directory(new File(PYTHON_FOLDER));
            pb.redirectErrorStream(true);

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String pythonResult = "";

            while ((line = reader.readLine()) != null) {
                System.out.println("   [Python]: " + line);
                String trimmed = line.trim();
                if (trimmed.equals("True") || trimmed.equals("False") || trimmed.equals("None")) {
                    pythonResult = trimmed;
                }
            }

            process.waitFor();
            handleResult(pythonResult, imagePath);

        } catch (Exception e) {
            System.err.println("[SERVİS HATASI]: " + e.getMessage());
        }
    }

    private static String resolvePythonExecutable() {
        String envValue = System.getenv(PYTHON_PATH_ENV);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }

        String[] candidates = {
                "/usr/bin/python3.11",
                "/usr/bin/python3",
                "/usr/bin/python",
                "python3",
                "python"
        };

        for (String candidate : candidates) {
            if (candidate.startsWith("/")) {
                if (new File(candidate).exists()) {
                    return candidate;
                }
            } else {
                return candidate;
            }
        }

        return "python3";
    }

    private static void handleResult(String result, String imagePath) {
        System.out.println("-----------------------------------------");
        switch (result) {
            case "True":
                System.out.println("[FINAL]: STANDBY TRUE (Aktif)");
                archiveImage(imagePath, "true");
                break;
            case "False":
                System.out.println("[FINAL]: STANDBY FALSE (Kapalı)");
                archiveImage(imagePath, "false");
                break;
            case "None":
            default:
                System.out.println("[FINAL]: FAILED/UNKNOWN");
                archiveImage(imagePath, "failed");
                break;
        }
        System.out.println("-----------------------------------------");
    }

    /**
     * Resmi okuyorum, JPEG %80 kalite ile sıkıştırır ve UTC ismiyle kaydeder.
     */
    private static void archiveImage(String sourcePath, String folderName) {
        try {
            // 1. Klasör Kontrolü
            File targetDir = new File(TRAINING_DATA_DIR + folderName);
            if (!targetDir.exists()) targetDir.mkdirs();

            // 2. UTC Zaman Damgası (Dosya Adı)
            String utcTimestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now());

            File sourceFile = new File(sourcePath);
            File destFile = new File(targetDir, utcTimestamp + "_archive.jpg");

            // 3. Resmi Oku
            BufferedImage image = ImageIO.read(sourceFile);
            if (image == null) {
                System.err.println("[HATA]: Resim okunamadı!");
                return;
            }

            // 4. JPEG Sıkıştırma Ayarları (%80 Kalite)
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer = writers.next();

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(destFile)) {
                writer.setOutput(ios);

                ImageWriteParam param = writer.getDefaultWriteParam();
                if (param.canWriteCompressed()) {
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(0.8f);
                }

                writer.write(null, new IIOImage(image, null, null), param);
            }
            writer.dispose();

            System.out.println("[ARŞİV]: " + folderName.toUpperCase() + " klasörüne JPEG (%80) olarak kaydedildi.");

        } catch (Exception e) {
            System.err.println("[ARŞİV HATASI]: " + e.getMessage());
        }
    }
}