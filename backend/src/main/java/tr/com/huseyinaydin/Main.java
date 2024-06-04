package tr.com.huseyinaydin;

import tr.com.huseyinaydin.customer.Customer;
import tr.com.huseyinaydin.customer.CustomerRepository;
import tr.com.huseyinaydin.customer.Gender;
import tr.com.huseyinaydin.s3.S3Buckets;
import tr.com.huseyinaydin.s3.S3Service;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;

//بسم الله الرحمن الرحيم
/**
 *
 * @author Huseyin_Aydin
 * @since 1994
 * @category Spring Boot, Java, Backend, Frontend, FullStack...
 *
 */

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            createRandomCustomer(customerRepository, passwordEncoder);
            // testBucketUploadAndDownload(s3Service, s3Buckets);
        };
    }

    private static void testBucketUploadAndDownload(S3Service s3Service,
                                                    S3Buckets s3Buckets) {
        s3Service.putObject(
                s3Buckets.getCustomer(),
                "foo/bar/huseyin",
                "Hello World".getBytes()
        );

        byte[] obj = s3Service.getObject(
                s3Buckets.getCustomer(),
                "foo/bar/huseyin"
        );

        System.out.println("Hooray: " + new String(obj));
    }

    private static void createRandomCustomer(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        var faker = new Faker();
        Random random = new Random();
        Name name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        int age = random.nextInt(16, 99);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        String email = "rastgeleemail-" + firstName.toLowerCase() + "." + lastName.toLowerCase() + "@huseyinbaba.com";
        Customer customer = new Customer(
                firstName +  " " + lastName,
                email,
                passwordEncoder.encode("huseyinP@ssw4rd"),
                age,
                gender);
        customerRepository.save(customer);
        System.out.println(email);
    }
}