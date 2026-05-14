package com.learn.demo.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class AssetCodeGenerator {

    // ─────────────────────────────────────────────
    // NORMALIZE — handle any case/space/special char
    // "Hero "  → "hero"
    // "HERO"   → "hero"
    // "H-ero"  → "hero"
    // ─────────────────────────────────────────────
    public static String normalize(String input) {
        if (input == null || input.isBlank()) return "";
        return input.trim()
                    .toLowerCase()
                    .replaceAll("\\s+", " ")
                    .replaceAll("[^a-z0-9 ]", "");
    }

    // ─────────────────────────────────────────────
    // COMPANY CODE — first letter of company name
    // "Hero"    → "H"
    // "Tata"    → "T"
    // "Wipro"   → "W"
    // "Infosys" → "I"
    // ─────────────────────────────────────────────
// ─────────────────────────────────────────────
// COMPANY CODE — first letter of each word
// "Cavin Kare"      → "CK"
// "Cavin Infotech"  → "CI"
// "Tech Nova"       → "TN"
// "Skyline"         → "S"
// ─────────────────────────────────────────────
public static String getCompanyCode(String companyName) {

    if (companyName == null || companyName.isBlank()) {
        return "XX";
    }

    String clean = normalize(companyName);

    String[] words = clean.split("\\s+");

    StringBuilder code = new StringBuilder();

    for (String word : words) {
        if (!word.isBlank()) {
            code.append(Character.toUpperCase(word.charAt(0)));
        }
    }

    return code.toString();
}

    // ─────────────────────────────────────────────
    // LOCATION CODE — first 2 letters of location name
    // "Chennai"     → "CH"
    // "Coimbatore"  → "CO"
    // "Hyderabad"   → "HY"
    // "Bangalore"   → "BA"
    // "Mumbai"      → "MU"
    // ─────────────────────────────────────────────
    public static String getLocationCode(String locationName) {
        if (locationName == null || locationName.isBlank()) return "XX";
        String clean = normalize(locationName).replaceAll(" ", "");
        return clean.substring(0, Math.min(2, clean.length())).toUpperCase();
    }

    // ─────────────────────────────────────────────
    // TYPE CODE — first 2-3 letters from DB typeName
    // "IT"        → "IT"
    // "Mobile"    → "MOB"
    // "Furniture" → "FUR"
    // "Equipment" → "EQP"
    // ─────────────────────────────────────────────
    public static String getTypeCode(String typeName) {
        if (typeName == null || typeName.isBlank()) return "GEN";
        String clean = typeName.trim().toUpperCase().replaceAll("\\s+", "");
        return clean.substring(0, Math.min(3, clean.length()));
    }

    // ─────────────────────────────────────────────
    // BUILD PREFIX — company + location + type
    // "Hero" + "Chennai" + "IT"        → "H-CH-IT-"
    // "Hero" + "Chennai" + "Mobile"    → "H-CH-MOB-"
    // "Hero" + "Chennai" + "Furniture" → "H-CH-FUR-"
    // "Tata" + "Hyderabad" + "IT"      → "T-HY-IT-"
    // ─────────────────────────────────────────────
    public static String buildPrefix(String companyName, String locationName, String typeName) {
        String companyCode  = getCompanyCode(companyName);
        String locationCode = getLocationCode(locationName);
        String typeCode     = getTypeCode(typeName);
        return companyCode + "-" + locationCode + "-" + typeCode + "-";
    }

    // ─────────────────────────────────────────────
    // BUILD ASSET CODE — prefix + 5 digit count
    // prefix="H-CH-IT-", count=1  → "H-CH-IT-00001"
    // prefix="H-CH-IT-", count=38 → "H-CH-IT-00038"
    // ─────────────────────────────────────────────
    public static String buildAssetCode(String companyName, String locationName, String typeName, long count) {
        String prefix = buildPrefix(companyName, locationName, typeName);
        return String.format("%s%05d", prefix, count);
    }

    // ─────────────────────────────────────────────
    // QR CODE — no change
    // ─────────────────────────────────────────────
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
