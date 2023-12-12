package semillero.ecosistema.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import semillero.ecosistema.entities.Supplier;
import semillero.ecosistema.entities.User;
import semillero.ecosistema.enumerations.UserRole;
import semillero.ecosistema.repositories.SupplierRepository;
import semillero.ecosistema.repositories.UserRepository;

import java.util.List;

@Service
public class EmailService {
    private JavaMailSender mailSender;

    @Value("${EMAIL_USERNAME}")
    private String fromEmail;

    private UserRepository userRepository;

    private SupplierService supplierService;

    private void sendEmail(String toEmail,
                          String subject,
                          String body
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        mailSender.send(message);
        System.out.println("Mail Send...");
    }

    private void sendNewSupplierToUser() throws Exception {
        try {
            List<Supplier> suppliers = supplierService.findAllCreatedAtLastWeek();
            if (!suppliers.isEmpty()) {
                // change this
                String suppliersString = suppliers.toString();
                userRepository
                        .findAllByRoleAndDeleted(UserRole.USUARIO_REGULAR, false)
                        .forEach(user -> sendEmail(user.getEmail(),
                                                    "hola",
                                                    suppliersString));
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
