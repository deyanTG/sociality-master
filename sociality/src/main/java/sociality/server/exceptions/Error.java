package sociality.server.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

public class Error {

	public enum Code {
		UNKNOWN,

		ITEM_NOT_FOUND, VALIDATION_ERROR,

		AUTHENTICATION_ERROR, BAD_CREDENTIALS, BAR_REQUEST
	}

	protected int status;

	protected UUID uuid;

	protected Code error;

	protected String timestamp;

	protected Map<String, Object> attributes;

	public Error() {
		this.uuid = UUID.randomUUID();
		this.timestamp = String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
		this.error = Code.UNKNOWN;
	}

	public Error(HttpStatus status) {
		this();
		if (status != null) {
			this.status = status.value();
		}
	}

	public Error(HttpStatus status, Code error) {
		this(status);
		if (error != null) {
			this.error = error;
		}
	}

	public Error(HttpStatus status, Code error, Map<String, Object> attributes) {
		this(status, error);
		this.attributes = attributes;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Code getError() {
		return error;
	}

	public void setError(Code error) {
		this.error = error;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
}
