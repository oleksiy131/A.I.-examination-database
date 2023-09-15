/*
 * package edu.sru.thangiah.controller;
 * 
 * import org.springframework.stereotype.Controller; import
 * org.springframework.web.bind.annotation.PostMapping; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * org.springframework.web.bind.annotation.RequestParam; import
 * org.springframework.web.multipart.MultipartFile; import
 * org.springframework.ui.Model; import
 * edu.sru.thangiah.service.ExcelImportService;
 * 
 * @RequestMapping("/import") public class ExcelImportController {
 * 
 * private final ExcelImportService excelImportService;
 * 
 * public ExcelImportController(ExcelImportService excelImportService) {
 * this.excelImportService = excelImportService; }
 * 
 * @PostMapping("/students") public String importStudents(@RequestParam("file")
 * MultipartFile file, Model model) { try {
 * excelImportService.importStudentsFromExcel(file);
 * model.addAttribute("message", "Students imported successfully"); } catch
 * (Exception e) { model.addAttribute("message", "Error importing students: " +
 * e.getMessage()); } return "redirect:import/students"; // Redirect } }
 */