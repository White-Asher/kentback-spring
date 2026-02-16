package example.com.kentbackspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 애플리케이션 진입점.
 */
@SpringBootApplication
public class KentbackSpringApplication {

    public static void main(String[] args) {
        // 스프링 부트 컨텍스트를 시작한다.
        SpringApplication.run(KentbackSpringApplication.class, args);
    }
}
