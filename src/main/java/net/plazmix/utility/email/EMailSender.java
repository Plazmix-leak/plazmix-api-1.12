package net.plazmix.utility.email;

import net.plazmix.utility.query.AsyncUtil;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EMailSender {

    private final Session session;

    private final String senderMail, username, password, host;

    /**
     * Инициализация MailSender
     *
     * @param senderMail - email адрес отправителя
     * @param username - имя пользователя отправителя
     * @param password - пароль отправилеля
     * @param host - сервис отправления ('smtp.yandex.ru', 'smtp.gmail.com', 'smtp.global.mail.ru', и т.д.)
     */
    public EMailSender(String senderMail, String username, String password, String host) {
        this.senderMail = senderMail;
        this.username = username;
        this.password = password;
        this.host = host;
        this.session = createSession();
    }

    /**
     * Отправить сообщение на почту
     *
     * @param subject - тема сообщения
     * @param content - содержимое сообщения
     */
    public void sendMessage(String subject, String content, String toMail) {
        Runnable messageSender = () -> {
            try {
                Message message = new MimeMessage(session);

                message.setFrom(new InternetAddress(senderMail));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(toMail));
                message.setSubject(subject);
                message.setContent(content, "text/html; charset=UTF-8");

                Transport.send(message);
            } catch (MessagingException ex) {
                ex.printStackTrace();
            }
        };

        AsyncUtil.submitAsync(messageSender);
    }

    /**
     * Создать и инициализировать сессию
     */
    private Session createSession() {
        Properties properties = new Properties();

        properties.put("global.mail.smtp.host", host);
        properties.put("global.mail.smtp.socketFactory.port", "465");
        properties.put("global.mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("global.mail.smtp.auth", "true");
        properties.put("global.mail.smtp.port", "465");

        return Session.getInstance(properties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }

        });
    }

}
