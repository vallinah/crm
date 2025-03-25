package site.easy.to.build.crm.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import site.easy.to.build.crm.service.customer.CustomerImportService;
import site.easy.to.build.crm.service.database.DatabaseService;

@Controller
@RequestMapping("/database")
public class DatabaseController {
    @Autowired
    private DatabaseService databaseService;
    private final CustomerImportService importService;

    // Injection par constructeur (recommandé)
    public DatabaseController(CustomerImportService importService) {
        this.importService = importService;
    }

    @GetMapping("/reset-database")
    public String resetPage() {
        return "database/reset-database";
    }

    @GetMapping("/import-csv")
    public String importPage() {
        return "database/import-csv";
    }

    @PostMapping("/reset-database")
    public String resetDatabase(RedirectAttributes redirectAttributes) {
        databaseService.resetDatabase();
        redirectAttributes.addFlashAttribute("message", "La base de données a été réinitialisée avec succès !");
        return "redirect:/";
    }

    @PostMapping("/customers/import")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            Model model) {

        if (file.isEmpty()) {
            model.addAttribute("error", "Le fichier est vide");
            return "database/import-csv";
        }

        if (!Objects.equals(file.getContentType(), "text/csv")) {
            model.addAttribute("error", "Seuls les fichiers CSV sont acceptés");
            return "database/import-csv";
        }

        try {
            CustomerImportService.ImportResult result = importService.importCustomersFromCsv(file);
            model.addAttribute("success",
                    String.format("Importation réussie: %d clients importés", result.getSavedCount()));
        } catch (CustomerImportService.ImportException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("errors", e.getErrors());
            model.addAttribute("fileName", file.getOriginalFilename());
        }

        return "database/import-csv";
    }
}
