package sociality.server.miscellaneous;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Responses {

	public static <T> ResponseEntity<T> ok(T t) {
		return new ResponseEntity<T>(t, HttpStatus.OK);
	}

	public static <T> ResponseEntity<T> ok() {
		return new ResponseEntity<T>(HttpStatus.OK);
	}

	public static <T extends Error> ResponseEntity<T> error(T t) {
		return new ResponseEntity<T>(t, HttpStatus.BAD_REQUEST);
	}
}
