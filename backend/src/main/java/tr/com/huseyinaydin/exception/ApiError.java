package tr.com.huseyinaydin.exception;

import java.time.LocalDateTime;

//بسم الله الرحمن الرحيم
/**
 *
 * @author Huseyin_Aydin
 * @since 1994
 * @category Spring Boot, Java, Backend, Frontend, FullStack...
 *
 */

public record ApiError(
        String path,
        String message,
        int statusCode,
        LocalDateTime localDateTime
) { }