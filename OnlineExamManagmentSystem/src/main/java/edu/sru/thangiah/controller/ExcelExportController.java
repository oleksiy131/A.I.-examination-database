package edu.sru.thangiah.controller;

import edu.sru.thangiah.service.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/export")
public class ExcelExportController {

    private final ExcelExportService excelExportService;

    @Autowired
    public ExcelExportController(ExcelExportService excelExportService) {
        this.excelExportService = excelExportService;
    }

    @GetMapping("/students")
    public String exportStudentData() {
        try {
            excelExportService.exportStudentData(null);
            return "Student data exported successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to export student data!";
        }
    }
}
