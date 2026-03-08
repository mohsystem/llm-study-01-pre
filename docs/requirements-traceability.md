# Functional Requirements Traceability (FR-A1 to FR-A12)

This matrix maps each provided requirement to the API contract in `docs/openapi-auth.yaml`.

| Requirement | Coverage |
|---|---|
| FR-A1 User registration | `POST /api/auth/register` with `RegisterRequest` and `RegisterResponse`. Duplicate account rejection uses deterministic `DeterministicErrorResponse` (`DUPLICATE_ACCOUNT`). |
| FR-A2 User login | `POST /api/auth/login` with `LoginRequest` accepting username/email via `identity`, returns token in `LoginResponse`. |
| FR-A3 Session refresh | `POST /api/auth/refresh` returns `TokenResponse` (`REFRESHED`). |
| FR-A4 User logout | `POST /api/auth/logout` returns JSON `StatusResponse` (e.g., `LOGGED_OUT`). |
| FR-A5 Password change | `POST /api/auth/change-password` with `currentPassword` + `newPassword`; success/status or deterministic reject. |
| FR-A6 Password reset request | `POST /api/auth/reset-request` with `identity`; returns JSON status. |
| FR-A7 Password reset confirmation | `POST /api/auth/reset-confirm` with `resetToken` + `newPassword`; returns JSON status or deterministic reject. |
| FR-A8 Password rules management | `PUT` and `GET /api/admin/accounts/password-rules` using `PasswordRules` schema. |
| FR-A9 Password rule application | Rule enforcement reflected by `422` deterministic rejects in registration/change/reset confirm with `PASSWORD_RULE_VIOLATION`. |
| FR-A10 MFA challenge + verify | `POST /api/auth/mfa/challenge` and `POST /api/auth/mfa/verify` with JSON response models for challenge and final auth status. |
| FR-A11 API key lifecycle | `POST/GET /api/auth/api-keys` and `DELETE /api/auth/api-keys/{keyId}` with JSON schemas (`ApiKeyResponse`, `ApiKeyMetadata`, `StatusResponse`). |
| FR-A12 Credential request handling | Deterministic accepted/rejected response shape established via `StatusResponse` and `DeterministicErrorResponse`; duplicate-account behavior explicitly modeled. |
