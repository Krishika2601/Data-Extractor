package com.Data.demo;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api")


public class UploadController {

    @Autowired
    private PdfDataRepository pdfDataRepository;

    @Autowired
    private TemperatureDataRepository temperatureDataRepository;

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            // Ensure files were provided in the request
            if (files == null || files.length == 0) {
                return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
            }

            List<PdfData> pdfDataList = new ArrayList<>();
            List<List<TemperatureData>> temperatureDataLists = new ArrayList<>();

            for (MultipartFile file : files) {
                // Check if the file is empty
                if (file.isEmpty()) {
                    return new ResponseEntity<>("File is empty.", HttpStatus.BAD_REQUEST);
                }

                // Check file type if necessary, for example:
                // if (!file.getContentType().equals("application/pdf")) {
                //     return new ResponseEntity<>("Only PDF files are allowed.", HttpStatus.BAD_REQUEST);
                // }

                // Load the PDF document
                PDDocument document = PDDocument.load(file.getInputStream());

                // Extract text from the PDF document
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);

                // Extract key-value pairs from the first page
                Map<String, String> keyValuePairs = extractKeyValuePairs(text);

                // Save first page data
                PdfData firstPageData = saveFirstPageData(keyValuePairs);
                pdfDataList.add(firstPageData);

                // Extract temperature data from the second page and save it
                List<TemperatureData> temperatureDataList = saveSecondPageData(text);
                temperatureDataLists.add(temperatureDataList);

                document.close();
            }

            // You can return any data or a success message here if needed
            return ResponseEntity.ok("Files uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred while processing the files.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/display")
    public String displayPage(Model model) {
        List<String> distinctSerialNumbers = temperatureDataRepository.findDistinctSerialNumbers();
        model.addAttribute("distinctSerialNumbers", distinctSerialNumbers);
        return "display";
    }
//    @GetMapping("/serialnumber")
//    public ResponseEntity<SerialNumberResponse> displaySerialNumbers() {
//        List<String> distinctSerialNumbers = temperatureDataRepository.findDistinctSerialNumbers();
//        SerialNumberResponse response = new SerialNumberResponse("Serial numbers retrieved successfully.", HttpStatus.OK.value(), distinctSerialNumbers);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
    @GetMapping("/serialnumber")
    public ResponseEntity<Map<String, Object>> displaySerialNumbers() {
        List<String> distinctSerialNumbers = temperatureDataRepository.findDistinctSerialNumbers();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("message", "Serial numbers retrieved successfully.");
        responseData.put("status", HttpStatus.OK.value());
        responseData.put("serialNumbers", distinctSerialNumbers);
        return ResponseEntity.ok(responseData);
    }
    @PostMapping("/display")
    public ResponseEntity<?> displayTemperatureData(@RequestParam("serialNumber") List<String> serialNumbers) {
        Map<String, Object> responseData = new HashMap<>();
        List<List<TemperatureData>> temperatureDataLists = new ArrayList<>();
        List<List<PdfData>> pdfDataLists = new ArrayList<>();

        for (String serialNumber : serialNumbers) {
            List<TemperatureData> dataForSerialNumber = temperatureDataRepository.findBySerialNumber(serialNumber);
            temperatureDataLists.add(dataForSerialNumber);

            List<PdfData> pdfDataForSerialNumber = pdfDataRepository.findBySerialNumber(serialNumber);
            pdfDataLists.add(pdfDataForSerialNumber);
        }

        List<String> distinctSerialNumbers = temperatureDataRepository.findDistinctSerialNumbers();

        responseData.put("distinctSerialNumbers", distinctSerialNumbers);
        responseData.put("temperatureDataLists", temperatureDataLists);
        responseData.put("pdfDataLists", pdfDataLists);

        return ResponseEntity.ok(responseData);
    }

    private List<TemperatureData> saveSecondPageData(String text) {
        List<TemperatureData> temperatureDataList = new ArrayList<>();
        String[] lines = text.split("\\r?\\n");
        boolean startParsing = false;

        // Extract key-value pairs from the text
        Map<String, String> keyValuePairs = extractKeyValuePairs(text);

        for (String line : lines) {
            if (line.startsWith("2/2")) {
                startParsing = true;
                continue;
            }

            if (startParsing && !line.isEmpty()) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 3) {
                    String dateTime = parts[0] + " " + parts[1];
                    double temperature = Double.parseDouble(parts[2]);
                    TemperatureData temperatureData = new TemperatureData(dateTime, temperature);
                    temperatureData.setSerialNumber(keyValuePairs.getOrDefault("Serial", ""));
                    temperatureDataList.add(temperatureData);

                    // Save each TemperatureData object to the repository
                    temperatureDataRepository.save(temperatureData);
                }
            }
        }

        return temperatureDataList;
    }

    private PdfData saveFirstPageData(Map<String, String> keyValuePairs) {
        PdfData pdfData = new PdfData();
        pdfData.setSerialNumber(keyValuePairs.getOrDefault("Serial", ""));
        pdfData.setFirmwareVersion(keyValuePairs.getOrDefault("VER", ""));
        pdfData.setLogCycle(keyValuePairs.getOrDefault("Log Cycle", ""));
        pdfData.setLogInterval(keyValuePairs.getOrDefault("Log Interval", ""));
        pdfData.setFirstLog(keyValuePairs.getOrDefault("First Log", ""));
        pdfData.setLastLog(keyValuePairs.getOrDefault("Last Log", ""));
        pdfData.setDurationTime(keyValuePairs.getOrDefault("Duration Time", ""));
        pdfData.setDataPoints(Integer.parseInt(keyValuePairs.getOrDefault("Data Points", "0")));
        pdfData.setStartMode(keyValuePairs.getOrDefault("Start Mode", ""));
        pdfData.setStartDelay(keyValuePairs.getOrDefault("Start Delay", ""));
        pdfData.setStopMode(keyValuePairs.getOrDefault("Stop Mode", ""));
        pdfData.setHighestTemperature(parseTemperature(keyValuePairs.getOrDefault("Highest", "0.0")));
        pdfData.setLowestTemperature(parseTemperature(keyValuePairs.getOrDefault("Lowest", "0.0")));
        pdfData.setAverageTemperature(parseTemperature(keyValuePairs.getOrDefault("Average", "0.0")));
        pdfData.setMkt(parseTemperature(keyValuePairs.getOrDefault("MKT", "0.0")));

        return pdfDataRepository.save(pdfData);
    }

    private Map<String, String> extractKeyValuePairs(String text) {
        Map<String, String> keyValuePairs = new HashMap<>();
        String[] lines = text.split("\\r?\\n");

        for (String line : lines) {
            // Check if the line contains a key-value pair
            if (line.contains(":")) {
                // Split the line into key and value
                String[] parts = line.split(":", 2);
                String key = parts[0].trim();
                String value = parts[1].trim();

                // Add key-value pair to the map
                keyValuePairs.put(key, value);
            }
        }

        return keyValuePairs;
    }

    // Helper method to parse temperature values
    private double parseTemperature(String temperatureString) {
        // Remove non-numeric characters from the temperature string
        String numericString = temperatureString.replaceAll("[^\\d.]", "");
        // Parse the numeric string as a double
        return Double.parseDouble(numericString);
    }
}
