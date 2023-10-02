package cz.upce.sempra.services;

import cz.upce.sempra.domains.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(EmailMessage emailMessage) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        // simpleMailMessage.setFrom(emailMessage.getFrom());
        simpleMailMessage.setTo(emailMessage.getTo());
        simpleMailMessage.setSubject(emailMessage.getSubject());
        simpleMailMessage.setText(emailMessage.getMessage());

        javaMailSender.send(simpleMailMessage);
    }
}