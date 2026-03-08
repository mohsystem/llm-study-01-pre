package com.example.usermanagement.admin;

import com.example.usermanagement.auth.PasswordRuleConfig;
import com.example.usermanagement.auth.PasswordRuleService;
import com.example.usermanagement.auth.dto.AuthDtos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/accounts")
public class AdminAccountController {

    private final PasswordRuleService passwordRuleService;

    public AdminAccountController(PasswordRuleService passwordRuleService) {
        this.passwordRuleService = passwordRuleService;
    }

    @PutMapping("/password-rules")
    public ResponseEntity<PasswordRuleConfig> updatePasswordRules(@RequestBody AuthDtos.PasswordRuleRequest request) {
        return ResponseEntity.ok(passwordRuleService.updateRules(request.toConfig()));
    }

    @GetMapping("/password-rules")
    public ResponseEntity<PasswordRuleConfig> getPasswordRules() {
        return ResponseEntity.ok(passwordRuleService.getActiveRules());
    }
}
