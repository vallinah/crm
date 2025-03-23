package site.easy.to.build.crm.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.CustomerLoginInfo;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerLoginInfoService;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;

@Controller
@RequestMapping("/customer/budget")
public class BudgetController {
    private final BudgetService budgetService;
    private final CustomerService customerService;
    private final AuthenticationUtils authenticationUtils;
    private final UserService userService;
    private final CustomerLoginInfoService customerLoginInfoService;

    public BudgetController(BudgetService budgetService, CustomerService customerService,
            AuthenticationUtils authenticationUtils, UserService userService,
            CustomerLoginInfoService customerLoginInfoService) {
        this.budgetService = budgetService;
        this.customerService = customerService;
        this.authenticationUtils = authenticationUtils;
        this.userService = userService;
        this.customerLoginInfoService = customerLoginInfoService;
    }

    @GetMapping("/create")
    public String showCreatingForm(Model model) {
        model.addAttribute("budget", new Budget());
        return "budget/create-budget";
    }

    @PostMapping("/create")
    public String createNewContract(@ModelAttribute("budget") @Validated Budget budget,
            BindingResult bindingResult, Authentication authentication,
            Model model)
            throws IOException, GeneralSecurityException {

        int customerId = authenticationUtils.getLoggedInUserId(authentication);
        CustomerLoginInfo customerLoginInfo = customerLoginInfoService.findById(customerId);
        Customer customer = customerService.findByEmail(customerLoginInfo.getEmail());

        if (customer == null) {
            return "error/500";
        }

        if (bindingResult.hasErrors()) {
            return "budget/create-budget";
        }
        budget.setCustomer(customer);

        Budget budgetContract = budgetService.save(budget);

        return "budget/create-budget";
    }

    @GetMapping("/my-budgets")
    public String getAllBudgets(Model model, Authentication authentication) {
        int customerId = authenticationUtils.getLoggedInUserId(authentication);
        CustomerLoginInfo customerLoginInfo = customerLoginInfoService.findById(customerId);
        Customer customer = customerService.findByEmail(customerLoginInfo.getEmail());
        List<Budget> budgets = budgetService.getCustomerBudgets(customer.getCustomerId());
        model.addAttribute("budgets", budgets);
        return "budget/my-budget";
    }

}
