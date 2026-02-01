package api.giybat.uz;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
// 1. Try to load from the current directory and the project root
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // If that fails, try the default root
        if (dotenv.entries().isEmpty()) {
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        }

        System.out.println("--- Loading .env variables ---");
        // 2. Transfer to System properties
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
            if (entry.getKey().startsWith("AWS")) {
                System.out.println("Successfully Loaded: " + entry.getKey());
            }
        });
        System.out.println("------------------------------");

        SpringApplication.run(Application.class, args);

	}

}
