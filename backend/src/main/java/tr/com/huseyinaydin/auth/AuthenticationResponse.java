package tr.com.huseyinaydin.auth;

import tr.com.huseyinaydin.customer.CustomerDTO;

//بسم الله الرحمن الرحيم
/**
 *
 * @author Huseyin_Aydin
 * @since 1994
 * @category Spring Boot, Java, Backend, Frontend, FullStack...
 *
 */

public record AuthenticationResponse (String token, CustomerDTO customerDTO){ }
