package com.learn.demo.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

public class AssetCodeGenerator {

    private static final Map<String, String> BRAND_MAP = Map.ofEntries(
        Map.entry("dell", "DELL"),
        Map.entry("hp", "HP"),
        Map.entry("hewlett", "HP"),
        Map.entry("lenovo", "LEN"),
        Map.entry("asus", "ASUS"),
        Map.entry("samsung", "SAM"),
        Map.entry("lg", "LG"),
        Map.entry("acer", "ACR"),
        Map.entry("apple", "APL"),
        Map.entry("sony", "SNY"),
        Map.entry("epson", "EPS"),
        Map.entry("canon", "CAN"),
        Map.entry("brother", "BRO"),
        Map.entry("logitech", "LOG"),
        Map.entry("microsoft", "MSF"),
        Map.entry("cisco", "CSC"),
        Map.entry("tplink", "TPL"),
        Map.entry("tp-link", "TPL")
    );

    private static final Map<String, String> TYPE_MAP = Map.ofEntries(
        Map.entry("laptop", "LAP"),
        Map.entry("notebook", "LAP"),
        Map.entry("monitor", "MON"),
        Map.entry("display", "MON"),
        Map.entry("printer", "PRN"),
        Map.entry("mobile", "MOB"),
        Map.entry("phone", "MOB"),
        Map.entry("tablet", "TAB"),
        Map.entry("ipad", "TAB"),
        Map.entry("keyboard", "KBD"),
        Map.entry("mouse", "MOU"),
        Map.entry("router", "NET"),
        Map.entry("switch", "NET"),
        Map.entry("desktop", "DSK"),
        Map.entry("pc", "DSK"),
        Map.entry("camera", "CAM"),
        Map.entry("webcam", "CAM"),
        Map.entry("scanner", "SCN"),
        Map.entry("chair", "FRN"),
        Map.entry("desk", "FRN"),
        Map.entry("table", "FRN"),
        Map.entry("furniture", "FRN"),
        Map.entry("server", "SRV"),
        Map.entry("headset", "AUD"),
        Map.entry("headphone", "AUD"),
        Map.entry("projector", "PRJ")
    );

    public static String getBrandPrefix(String brand, String assetName) {
        String search = (brand != null ? brand : assetName).toLowerCase();
        for (Map.Entry<String, String> entry : BRAND_MAP.entrySet()) {
            if (search.contains(entry.getKey())) return entry.getValue();
        }
        // fallback: first word of brand or assetName uppercased max 4 chars
        String fallback = (brand != null && !brand.isBlank()) ? brand : assetName;
        String[] parts = fallback.trim().split("\\s+");
        return parts[0].toUpperCase().substring(0, Math.min(4, parts[0].length()));
    }

    public static String getTypePrefix(String assetName, String typeName) {
        String search = (assetName + " " + (typeName != null ? typeName : "")).toLowerCase();
        for (Map.Entry<String, String> entry : TYPE_MAP.entrySet()) {
            if (search.contains(entry.getKey())) return entry.getValue();
        }
        // fallback: last word of assetName max 3 chars
        String[] parts = assetName.trim().split("\\s+");
        String last = parts[parts.length - 1];
        return last.toUpperCase().substring(0, Math.min(3, last.length()));
    }

    public static String buildAssetCode(String brand, String assetName, String typeName, long count) {
        String brandPrefix = getBrandPrefix(brand, assetName);
        String typePrefix = getTypePrefix(assetName, typeName);
        return String.format("%s-%s-%03d", brandPrefix, typePrefix, count);
    }

    public static String generateQrCodeBase64(String content) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 200, 200);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (WriterException | IOException e) {
            return null;
        }
    }
}