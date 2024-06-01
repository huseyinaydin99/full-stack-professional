package tr.com.huseyinaydin.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

//بسم الله الرحمن الرحيم
/**
 *
 * @author Huseyin_Aydin
 * @since 1994
 * @category Spring Boot, Java, Backend, Frontend, FullStack...
 *
 */

@Configuration
public class S3Config {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.s3.mock}")
    private boolean mock;

    @Bean
    public S3Client s3Client() {
        if (mock) {
            return new FakeS3();
        }
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .build();
    }
}