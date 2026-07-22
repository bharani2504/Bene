package com.example.bene.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(String toMail, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toMail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("dummyyyy425@gmail.com");

        javaMailSender.send(message);
    }
}
