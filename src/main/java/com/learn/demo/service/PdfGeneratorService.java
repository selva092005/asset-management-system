package com.learn.demo.service;

import java.io.File;
import java.io.FileOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
@Slf4j
public class PdfGeneratorService {

    /**
     * Converts a well-formed HTML/XHTML string into a PDF file on disk.
     *
     * @param htmlContent XHTML content to render
     * @param filename Target PDF file name (saved in uploads directory)
     * @return Absolute path of the generated PDF file, or null if generation fails
     */
    public String generatePdfFile(String htmlContent, String filename) {
        try {
            File dir = new File("uploads");
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (created) {
                    log.info("Created uploads directory for PDF generation.");
                }
            }

            File outputFile = new File(dir, filename);
            try (FileOutputStream os = new FileOutputStream(outputFile)) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(htmlContent);
                renderer.layout();
                renderer.createPDF(os);
            }

            log.info("Successfully generated PDF invoice receipt at: {}", outputFile.getAbsolutePath());
            return outputFile.getAbsolutePath();
        } catch (Exception e) {
            log.error("Failed to dynamically generate PDF receipt for file: {}", filename, e);
            return null;
        }
    }
}
