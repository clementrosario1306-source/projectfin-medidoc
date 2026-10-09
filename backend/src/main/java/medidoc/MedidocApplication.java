package medidoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
    scanBasePackages = "medidoc",
    exclude = { UserDetailsServiceAutoConfiguration.class }
)
public class MedidocApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedidocApplication.class, args);
        System.out.println("===========================================");
        System.out.println("   MediDoc Server Started Successfully!   ");
        System.out.println("   URL : https://projectfin-medidoc-4.onrender.com");
        System.out.println("   DB  : medidoc_db on MongoDB Atlas       ");
        System.out.println("===========================================");
    }
}