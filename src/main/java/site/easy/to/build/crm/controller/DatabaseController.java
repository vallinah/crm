package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import site.easy.to.build.crm.service.database.DatabaseService;

@Controller
@RequestMapping("/database")
public class DatabaseController {
    @Autowired
    private DatabaseService databaseService;

    @PostMapping("/reset-database")
    public String resetDatabase(RedirectAttributes redirectAttributes) {
        databaseService.resetDatabase();
        redirectAttributes.addFlashAttribute("message", "La base de données a été réinitialisée avec succès !");
        return "redirect:/";
    }
}
