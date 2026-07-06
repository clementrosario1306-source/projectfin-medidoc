package medidoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "medidoc")
public class MedidocApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedidocApplication.class, args);
        System.out.println("===========================================");
        System.out.println("   MediDoc Server Started Successfully!   ");
        System.out.println("   URL : http://localhost:8080             ");
        System.out.println("   DB  : medidoc_db on localhost:3306      ");
        System.out.println("===========================================");
    }
}