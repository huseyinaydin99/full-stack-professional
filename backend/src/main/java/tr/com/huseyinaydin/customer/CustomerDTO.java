package tr.com.huseyinaydin.customer;

import java.util.List;

//بسم الله الرحمن الرحيم
/**
 *
 * @author Huseyin_Aydin
 * @since 1994
 * @category Spring Boot, Java, Backend, Frontend, FullStack...
 *
 */

public record CustomerDTO(
        Integer id,
        String name,
        String email,
        Gender gender,
        Integer age,
        List<String> roles,
        String username,
        String profileImageId
) { }