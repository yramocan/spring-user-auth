package com.ctrlflowio.userAuth.registration;

import com.ctrlflowio.userAuth.appuser.AppUser;
import com.ctrlflowio.userAuth.appuser.AppUserRole;
import com.ctrlflowio.userAuth.appuser.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final AppUserService appUserService;
    private final EmailValidator emailValidator;

    public String register(RegistrationRequest request) {
        Boolean isValidEmail = emailValidator.test(request.getEmail());

        if (!isValidEmail) {
            throw new IllegalStateException("Email is not valid.");
        }

        return appUserService.createUser(
            new AppUser(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getPassword(),
                    AppUserRole.USER
            )
        );
    }
}
