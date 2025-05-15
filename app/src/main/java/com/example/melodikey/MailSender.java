package com.example.melodikey;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MailSender {

    private final String username = "harenarakotoarimanana2@gmail.com"; // Ton email
    private final String password = "m b w k e l a k v v y q f r t v"; // Mot de passe d'application

    public void sendVerificationEmail(String toEmail, String verificationCode) {
        new Thread(() -> {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Vérification de votre compte");
                message.setText("Votre code de vérification est : " + verificationCode);

                Transport.send(message);
                System.out.println("E-mail envoyé avec succès !");
            } catch (MessagingException e) {
                e.printStackTrace();
                System.err.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
            }
        }).start(); // Exécute l'envoi dans un thread secondaire
    }
}
