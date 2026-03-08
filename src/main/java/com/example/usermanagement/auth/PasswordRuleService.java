package com.example.usermanagement.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PasswordRuleService {

    private final PasswordRuleConfigRepository repository;

    public PasswordRuleService(PasswordRuleConfigRepository repository) {
        this.repository = repository;
    }

    public PasswordRuleConfig getActiveRules() {
        return repository.findById(1L).orElseGet(() -> repository.save(PasswordRuleConfig.defaultRules()));
    }

    public PasswordRuleConfig updateRules(PasswordRuleConfig incoming) {
        PasswordRuleConfig rules = getActiveRules();
        rules.updateFrom(incoming);
        return repository.save(rules);
    }

    public PasswordValidationResult validate(String password) {
        PasswordRuleConfig rules = getActiveRules();
        if (password == null || password.length() < rules.getMinLength()) {
            return PasswordValidationResult.rejected("PASSWORD_RULE_MIN_LENGTH", "Password does not meet minimum length");
        }
        if (rules.isRequireUppercase() && !password.chars().anyMatch(Character::isUpperCase)) {
            return PasswordValidationResult.rejected("PASSWORD_RULE_UPPERCASE", "Password requires uppercase letter");
        }
        if (rules.isRequireLowercase() && !password.chars().anyMatch(Character::isLowerCase)) {
            return PasswordValidationResult.rejected("PASSWORD_RULE_LOWERCASE", "Password requires lowercase letter");
        }
        if (rules.isRequireDigit() && !password.chars().anyMatch(Character::isDigit)) {
            return PasswordValidationResult.rejected("PASSWORD_RULE_DIGIT", "Password requires digit");
        }
        if (rules.isRequireSpecial() && password.chars().noneMatch(c -> !Character.isLetterOrDigit(c))) {
            return PasswordValidationResult.rejected("PASSWORD_RULE_SPECIAL", "Password requires special character");
        }
        return PasswordValidationResult.accepted();
    }
}
