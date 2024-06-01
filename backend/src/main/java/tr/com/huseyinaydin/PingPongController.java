package tr.com.huseyinaydin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//بسم الله الرحمن الرحيم
/**
 *
 * @author Huseyin_Aydin
 * @since 1994
 * @category Spring Boot, Java, Backend, Frontend, FullStack...
 *
 */

@RestController
public class PingPongController {

    private static int COUNTER = 0;

    record PingPong(String result){}

    @GetMapping("/ping")
    public PingPong getPingPong() {
        return new PingPong("Pong: %s".formatted(++COUNTER));
    }
}