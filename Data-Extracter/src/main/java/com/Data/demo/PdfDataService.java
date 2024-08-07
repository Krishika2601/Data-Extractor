package com.Data.demo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PdfDataService {

    @Autowired
    private PdfDataRepository pdfDataRepository;

    public void savePdfData(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            PdfData pdfData = new PdfData();
            pdfData.setExtractedData(text);
            pdfDataRepository.save(pdfData);
        }
    }
}
