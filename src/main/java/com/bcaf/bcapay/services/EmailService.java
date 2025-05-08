package com.bcaf.bcapay.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.bcaf.bcapay.models.User;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        if (to == null || to.isEmpty() || subject == null || text == null) {
            logger.error("Gagal mengirim email: parameter tidak boleh null atau kosong.");
            throw new IllegalArgumentException("Email, subject, dan body tidak boleh kosong.");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("bcapay@bcaf.co.id");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
            logger.info("Email berhasil dikirim ke {}", to);
        } catch (MailException e) {
            logger.error("Gagal mengirim email ke {}: {}", to, e.getMessage());
            throw new RuntimeException("Gagal mengirim email, coba lagi nanti.");
        }
    }

    public void sendInitialPasswordEmail(String to, String generatedPassword) {
        if (generatedPassword == null || generatedPassword.isEmpty()) {
            logger.error("Gagal mengirim email: password sementara tidak boleh kosong.");
            throw new IllegalArgumentException("Password sementara tidak boleh kosong.");
        }

        String subject = "Akun Pegawai Baru - FINAPay";
        String body = "Selamat, akun Anda telah dibuat.\n\n"
                + "Berikut adalah detail akun Anda:\n"
                + "Email: " + to + "\n"
                + "Password sementara: " + generatedPassword + "\n\n"
                + "Harap segera masuk dan ubah password Anda.\n\n"
                + "Terima kasih.";

        sendEmail(to, subject, body);
    }


    public void sendRequestResetPassword(String name, String email, String id) {
        String subject = "Reset Password - FINAPay";
        String body = String.format(
                "Akun Anda meminta reset password di FINAPay.\n\n" +
                        "Abaikan jika ini bukan Anda,\n\n" +
                        "Berikut adalah detail akun Anda:\n" +
                        "Nama: " + name + "\n" +
                        "Email: " + email + "\n\n" +
                        "Klik link di bawah untuk mengatur ulang password Anda:\n\n" +
                        generateResetLink(id) + "\n\n" +
                        "Link ini hanya berlaku selama 24 jam.\n\n" +
                        "Terima kasih,\n" +
                        "Tim FINAPay");

        sendEmail(email, subject, body);
    }

    private String generateResetLink(String id) {
        
        String baseUrl = "http://localhost:4200/reset-password/"; // Sesuaikan dengan domain frontend
        return baseUrl + id.toString();
    }

    public void sendCustomerRegistrationEmail(User user) {
        String subject = "Registrasi Akun Berhasil - FINAPay";
        String body = String.format(
            "Halo " + user.getName() + ",\n\n" +
                    "Selamat! Akun Anda telah berhasil dibuat di FINAPay.\n\n" +
                    "Berikut adalah detail akun Anda:\n" +
                    "Nama: " + user.getName() + "\n" +
                    "Email: " + user.getEmail() + "\n\n" +
                    "Anda sekarang dapat menggunakan layanan kami. Jika ada pertanyaan, hubungi support kami.\n\n" +
                    "Terima kasih,\n" +
                    "Tim FINAPay");
        sendEmail(user.getEmail(), subject, body);
    }

}
