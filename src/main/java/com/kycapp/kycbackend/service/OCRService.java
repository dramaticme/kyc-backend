package com.kycapp.kycbackend.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Service
public class OCRService {

    private final ITesseract tesseract;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public OCRService() {
        tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata"); // tessdata path
        tesseract.setLanguage("eng");
    }

    // Extract text and store file in MongoDB GridFS
    public String extractText(InputStream inputStream, String filename, ObjectId[] outId) throws IOException, TesseractException {
        // 1️⃣ Store file in MongoDB GridFS
        ObjectId fileId = gridFsTemplate.store(inputStream, filename);
        if (outId != null && outId.length > 0) {
            outId[0] = fileId;
        }

        // 2️⃣ Retrieve GridFSFile and resource for OCR
        GridFSFile gridFSFile = gridFsTemplate.findOne(
                org.springframework.data.mongodb.core.query.Query.query(
                        org.springframework.data.mongodb.core.query.Criteria.where("_id").is(fileId)
                )
        );

        if (gridFSFile == null) {
            throw new RuntimeException("Failed to retrieve stored file from GridFS.");
        }

        GridFsResource resource = gridFsTemplate.getResource(gridFSFile);

        // 3️⃣ Write to temp file for OCR processing
        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + filename);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            resource.getInputStream().transferTo(fos);
        }

        // 4️⃣ Extract text
        String result;
        try {
            if (filename.toLowerCase().endsWith(".pdf")) {
                result = extractTextFromPDF(tempFile);
            } else {
                result = extractTextFromImage(tempFile);
            }
        } finally {
            if (tempFile.exists()) tempFile.delete();
        }

        return result;
    }

    // Extract text from GridFS by documentId
    public String extractTextFromGridFS(String documentId) throws IOException, TesseractException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(
                org.springframework.data.mongodb.core.query.Query.query(
                        org.springframework.data.mongodb.core.query.Criteria.where("_id").is(new ObjectId(documentId))
                )
        );

        if (gridFSFile == null) throw new RuntimeException("File not found in GridFS: " + documentId);

        GridFsResource resource = gridFsTemplate.getResource(gridFSFile);

        return extractText(resource.getInputStream(), gridFSFile.getFilename(), null);
    }

    // PDF processing
    private String extractTextFromPDF(File pdfFile) throws IOException, TesseractException {
        StringBuilder sb = new StringBuilder();
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer renderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = renderer.renderImageWithDPI(page, 150, ImageType.RGB);
                image = resizeImageIfLarge(image, 2000, 2000);
                try {
                    sb.append(tesseract.doOCR(image)).append("\n");
                } catch (TesseractException e) {
                    sb.append("[OCR Failed page ").append(page + 1).append(": ").append(e.getMessage()).append("]\n");
                }
            }
        }
        return sb.toString();
    }

    // Image processing (TIFF, PNG, JPEG, BMP, GIF)
    private String extractTextFromImage(File imageFile) throws IOException {
        BufferedImage img = ImageIO.read(imageFile);
        if (img == null) throw new RuntimeException("Unsupported image format. Ensure JAI Image I/O is installed.");
        img = resizeImageIfLarge(img, 2000, 2000);

        try {
            return tesseract.doOCR(img);
        } catch (TesseractException e) {
            throw new RuntimeException("Tesseract OCR failed: " + e.getMessage(), e);
        }
    }

    // Resize large images
    private BufferedImage resizeImageIfLarge(BufferedImage original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();
        if (width <= maxWidth && height <= maxHeight) return original;

        double ratio = Math.min((double) maxWidth / width, (double) maxHeight / height);
        int newWidth = (int) (width * ratio);
        int newHeight = (int) (height * ratio);

        Image tmp = original.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}
