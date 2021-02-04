package com.ctrlflowio.userAuth.registration;

import com.ctrlflowio.userAuth.appuser.AppUser;
import com.ctrlflowio.userAuth.appuser.AppUserRole;
import com.ctrlflowio.userAuth.appuser.AppUserService;
import com.ctrlflowio.userAuth.email.ConfirmEmailTemplateGenerator;
import com.ctrlflowio.userAuth.email.EmailSender;
import com.ctrlflowio.userAuth.registration.token.ConfirmationToken;
import com.ctrlflowio.userAuth.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final EmailValidator emailValidator;

    @Transactional
    public String confirmEmail(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        confirmationTokenService.setConfirmedAt(token);

        appUserService.enableAppUser(confirmationToken.getAppUser().getEmail());

        return "Confirmed";
    }

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());

        if (!isValidEmail) {
            throw new IllegalStateException("Email is not valid.");
        }

        final AppUser user = new AppUser(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                AppUserRole.USER
        );

        final String token = appUserService.createUser(user);

        emailSender.send(
                user.getEmail(),
                "Confirm your email address",
                ConfirmEmailTemplateGenerator.generateTemplate(user.getFirstName(), token)
        );

        return token;
    }
}
