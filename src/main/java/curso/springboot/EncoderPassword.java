package curso.springboot;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncoderPassword {

	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String password = encoder.encode("admin");
		System.out.println(password);
	}

}
