package net.passioncloud.upload;

import net.passioncloud.upload.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(UploadApplication.class, args);
    }

    // Boot CommandLineRunner to delete and recreate the upload folder at startup.
    @Bean
    CommandLineRunner init(StorageService storageService) {
        System.out.println("Boot CommandLineRunner. Initializing storage service.");
        return args -> {
            storageService.deleteAll();
            storageService.init();
        };
    }
}
