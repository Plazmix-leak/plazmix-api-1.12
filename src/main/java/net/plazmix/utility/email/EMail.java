package net.plazmix.utility.email;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class EMail {

    @Getter
    @Setter
    private String smtpHost = "smtp.yandex.ru";

    private final Map<String, EMailSender> mailSenderMap = new HashMap<>();


    /**
     * Получение MailSender из кеша.
     *
     * Если его там нет, то он автоматически туда добавляется,
     * возвращая тот объект, что был добавлен туда.
     *
     * @param username - имя пользователя отправителя
     * @param password - пароль отправилеля
     */
    public EMailSender getMailSender(String username, String password) {
        return mailSenderMap.computeIfAbsent(username, f -> new EMailSender(username, username, password, smtpHost));
    }

    /**
     * Отправить сообщение на почту
     *
     * @param subject - тема сообщения
     * @param content - содержимое сообщения
     * @param toMail - email адрес получателя
     */
    public void sendMessage(EMailSender EMailSender, String subject, String content, String toMail) {
        EMailSender.sendMessage(subject, content, toMail);
    }

}
