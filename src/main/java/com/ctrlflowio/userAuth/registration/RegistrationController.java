package com.ctrlflowio.userAuth.registration;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/register")
@AllArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @GetMapping(path = "/confirmEmail")
    public String confirmEmail(@RequestParam String token) {
        return registrationService.confirmEmail(token);
    }

    @PostMapping
    public String register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }
}
