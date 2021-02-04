package com.ctrlflowio.userAuth.email;

public interface EmailSender {
    void send(String to, String subject, String email);
}
