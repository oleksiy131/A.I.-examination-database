package edu.sru.thangiah.controller;

import edu.sru.thangiah.service.ExcelParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api")
public class FileUploadController {

    @Autowired
    private ExcelParserService excelParserService;
    
    @GetMapping("/class")
    public String showClassImportPage() {
        return "class-import";
    }

    @PostMapping("/uploadpoopy")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            excelParserService.parseExcelFile(file);
            return new ResponseEntity<>("File uploaded and processed successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to upload file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
