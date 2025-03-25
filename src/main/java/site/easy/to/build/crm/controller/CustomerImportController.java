package site.easy.to.build.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import site.easy.to.build.crm.service.customer.CustomerImportService;

@Controller
public class CustomerImportController {

    private final CustomerImportService importService;

    public CustomerImportController(CustomerImportService importService) {
        this.importService = importService;
    }

    @GetMapping("/customers/import")
    public String showImportForm() {
        return "customer/import-form";
    }

    @PostMapping("/customers/import")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Le fichier est vide");
            return "redirect:/customers/import";
        }

        if (!file.getContentType().equals("text/csv")) {
            redirectAttributes.addFlashAttribute("message", "Seuls les fichiers CSV sont acceptés");
            return "redirect:/customers/import";
        }

        try {
            CustomerImportService.ImportResult result = importService.importCustomersFromCsv(file);
            redirectAttributes.addFlashAttribute("success",
                    "Importation réussie: " + result.getSavedCount() + " clients importés");
            return "redirect:/customers";

        } catch (CustomerImportService.ImportException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            return "redirect:/customers/import";
        }
    }
}