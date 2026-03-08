package com.example.usermanagement.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "password_rule_config")
public class PasswordRuleConfig {

    @Id
    private Long id;
    private int minLength;
    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireDigit;
    private boolean requireSpecial;

    protected PasswordRuleConfig() {
    }

    public PasswordRuleConfig(Long id, int minLength, boolean requireUppercase, boolean requireLowercase, boolean requireDigit, boolean requireSpecial) {
        this.id = id;
        this.minLength = minLength;
        this.requireUppercase = requireUppercase;
        this.requireLowercase = requireLowercase;
        this.requireDigit = requireDigit;
        this.requireSpecial = requireSpecial;
    }

    public static PasswordRuleConfig defaultRules() {
        return new PasswordRuleConfig(1L, 8, true, true, true, false);
    }

    public Long getId() { return id; }
    public int getMinLength() { return minLength; }
    public boolean isRequireUppercase() { return requireUppercase; }
    public boolean isRequireLowercase() { return requireLowercase; }
    public boolean isRequireDigit() { return requireDigit; }
    public boolean isRequireSpecial() { return requireSpecial; }

    public void updateFrom(PasswordRuleConfig source) {
        this.minLength = source.minLength;
        this.requireUppercase = source.requireUppercase;
        this.requireLowercase = source.requireLowercase;
        this.requireDigit = source.requireDigit;
        this.requireSpecial = source.requireSpecial;
    }
}
