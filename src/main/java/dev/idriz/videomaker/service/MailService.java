package dev.idriz.videomaker.service;

import dev.idriz.videomaker.list.Pair;
import jakarta.mail.Message;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @SafeVarargs
    public final void sendMail(String to, String subject, String text, Pair<String, String>... attachments) {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            for (Pair<String, String> attachment : attachments) {
                FileSystemResource file = new FileSystemResource(new File(attachment.getRight()));
                mimeMessageHelper.addAttachment(attachment.getLeft(), file);
            }
            mimeMessage.setRecipients(Message.RecipientType.TO, to);
            mimeMessage.setSubject(subject);
            mimeMessage.setText(text);
        });
    }


}
