package tr.com.huseyinaydin.customer;

//بسم الله الرحمن الرحيم
/**
 *
 * @author Huseyin_Aydin
 * @since 1994
 * @category Spring Boot, Java, Backend, Frontend, FullStack...
 *
 */

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) { }
