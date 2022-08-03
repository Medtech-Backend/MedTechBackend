package com.project.medtech.service;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public EmailSenderService(@Lazy JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @SneakyThrows
    public String send(String email, String type) {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        String subject = "";
        String content = "";
        char[] otp = new char[0];

        // FIXME: 03.08.2022 В данном случае можно было загнать типы в ENUM и внутри хранить шаблон сообщения, а для подставления значении использовать String.format(#{Шаблон}, #{подставляемые значения});
        if (type.equals("otp")) {
            otp = generate(true);
            helper.setFrom("trustmed.team3@gmail.com");
            helper.setTo(email);
            subject = "Here's your One Time Password!";
            content = "<p>Hello! </p>"
                    + "<p>For security reason, you're required to use the following "
                    + "One Time Password to login:</p>"
                    + "<p><b>" + new String(otp) + "</b></p>"
                    + "<br>";
        } else if(type.equals("resetCode")) {
            otp = generate(false);
            helper.setFrom("trustmed.team3@gmail.com");
            helper.setTo(email);
            subject = "Here's your Reset Code (OTP)!";
            content = "<p>Hello! </p>"
                    + "<p>For security reason, you're required to use the following "
                    + "Reset Code to create a new Password:</p>"
                    + "<p><b>" + new String(otp) + "</b></p>"
                    + "<br>";
        }
        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);

        return new String(otp);
    }

    static char[] generate(boolean generate) {
        String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Small_chars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String values = Capital_chars + Small_chars + numbers;
        Random random = new Random();
        char[] password = generate ? new char[8] : new char[6];
        if (generate) {
            for (int i = 0; i < 8; i++) {
                password[i] = values.charAt(random.nextInt(values.length()));
            }
        } else {
            for (int i = 0; i < 6; i++) {
                password[i] = numbers.charAt(random.nextInt(numbers.length()));
            }
            return password;
        }
        return password;
    }

}
