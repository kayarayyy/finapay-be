package com.bcaf.finapay.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.bcaf.finapay.dto.CustomerDetailsDto;
import com.bcaf.finapay.dto.LoanRequestDto;
import com.bcaf.finapay.dto.UserDto;
import com.bcaf.finapay.models.LoanRequest;
import com.bcaf.finapay.models.User;
import com.bcaf.finapay.utils.DateFormatterUtil;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender emailSender;
    @Value("${base.url.vercel}")
    private String baseUrl;

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
            message.setFrom("finapay@bcaf.co.id");
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

        String url = baseUrl + "/reset-password?id=" + id.toString();
        return url;
    }

    public void sendCustomerRegistrationEmail(User user) {
        String activationLink = generateActivationLink(user.getId().toString());
        String subject = "Aktivasi Akun FINAPay Anda";
        String body = "Halo " + user.getName() + ",\n\n" +
                "Selamat! Akun Anda telah berhasil didaftarkan di FINAPay.\n\n" +
                "Berikut adalah detail akun Anda:\n" +
                "Nama  : " + user.getName() + "\n" +
                "Email : " + user.getEmail() + "\n\n" +
                "Untuk mengaktifkan akun Anda, silakan klik tautan berikut:\n" +
                activationLink + "\n\n" +
                "Jika Anda tidak merasa melakukan pendaftaran, silakan abaikan email ini.\n\n" +
                "Terima kasih,\n" +
                "Tim FINAPay";

        sendEmail(user.getEmail(), subject, body);
    }

    public void sendActivationLink(User user) {
        String activationLink = generateActivationLink(user.getId().toString());
        String subject = "Aktivasi Akun FINAPay Anda";
        String body = "Halo " + user.getName() + ",\n\n" +
                "Berikut adalah detail akun Anda:\n" +
                "Nama  : " + user.getName() + "\n" +
                "Email : " + user.getEmail() + "\n\n" +
                "Untuk mengaktifkan akun Anda, silakan klik tautan berikut:\n" +
                activationLink + "\n\n" +
                "Jika Anda tidak merasa melakukan pendaftaran, silakan abaikan email ini.\n\n" +
                "Terima kasih,\n" +
                "Tim FINAPay";

        sendEmail(user.getEmail(), subject, body);
    }

    private String generateActivationLink(String id) {

        String url = baseUrl + "/activate?id=" + id.toString();
        return url;
    }

    public void sendCustomerGoogleRegistrationEmail(User user, String rawPassword) {
        String subject = "Registrasi dengan Google Berhasil - FINAPay";
        String body = String.format(
                "Halo " + user.getName() + ",\n\n" +
                        "Selamat! Akun Anda telah berhasil dibuat di FINAPay dengan Google. Abaikan jika anda tidak melakukannya\n\n"
                        +
                        "Berikut adalah detail akun Anda:\n" +
                        "Nama: " + user.getName() + "\n" +
                        "Email: " + user.getEmail() + "\n" +
                        "Password (Default): " + rawPassword + "\n\n" +
                        "Anda sekarang dapat menggunakan layanan kami. Jika ada pertanyaan, hubungi support kami.\n\n" +
                        "Terima kasih,\n" +
                        "Tim FINAPay");
        sendEmail(user.getEmail(), subject, body);
    }

    public void sendLoanApprovedEmail(LoanRequestDto loanRequest) {
        UserDto customer = loanRequest.getCustomer();
        if (customer == null || customer.getEmail() == null || customer.getName() == null) {
            logger.error("Data user tidak lengkap untuk mengirim email persetujuan pengajuan.");
            throw new IllegalArgumentException("Data user tidak lengkap.");
        }

        String subject = "Pengajuan Disetujui - FINAPay";
        String body = String.format(
                "Halo " + customer.getName() + ",\n\n" +
                        "Selamat! Pengajuan Anda telah disetujui dan saat ini sedang menunggu proses pencairan dana.\n\n"
                        +
                        "Berikut adalah detail pengajuan Anda:\n" +
                        "Total Dana yang Diterima: " + loanRequest.getAmount() + "\n" +
                        "Jumlah Cicilan per Bulan: " + loanRequest.getInstalment() + "\n" +
                        "Dengan Tenor: " + loanRequest.getTenor() + " Bulan\n" +
                        "Silakan pantau status pencairan dana di aplikasi FINAPay.\n\n" +
                        "Terima kasih telah menggunakan layanan kami.\n\n" +
                        "Hormat kami,\n" +
                        "Tim FINAPay");

        sendEmail(customer.getEmail(), subject, body);
    }

    public void sendLoanDisbursementEmail(LoanRequestDto loanRequest, CustomerDetailsDto customerDetails) {
        UserDto customer = loanRequest.getCustomer();
        String disbursedAt = loanRequest.getBackOfficeDisbursedAt();
        String jatuhTempoPertama = DateFormatterUtil.addOneMonthToLongIndonesianDate(disbursedAt);

        if (customer == null || customer.getEmail() == null || customer.getName() == null) {
            logger.error("Data user tidak lengkap untuk mengirim email pencairan dana.");
            throw new IllegalArgumentException("Data user tidak lengkap.");
        }

        String subject = "Dana Telah Dicairkan - FINAPay";
        String body = String.format(
                "Halo " + customer.getName() + ",\n\n" +
                        "Kami ingin menginformasikan bahwa dana dari pengajuan Anda telah berhasil dicairkan ke rekening "
                        + customerDetails.getNoRek() + " .\n\n" +
                        "Berikut adalah detail pengajuan Anda:\n" +
                        "Total Dana yang Diterima: " + loanRequest.getAmount() + "\n" +
                        "Jumlah Cicilan per Bulan: " + loanRequest.getInstalment() + "\n" +
                        "Dengan Tenor: " + loanRequest.getTenor() + " Bulan\n" +
                        "Tanggal Jatuh Tempo Pertama: " + jatuhTempoPertama + "\n\n" +
                        "Silakan cek rekening Anda dan pantau jadwal pembayaran cicilan di aplikasi FINAPay.\n\n" +
                        "Terima kasih telah menggunakan layanan kami.\n\n" +
                        "Hormat kami,\n" +
                        "Tim FINAPay");

        sendEmail(customer.getEmail(), subject, body);
    }

}
