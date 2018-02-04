package sociality.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@org.codehaus.jackson.map.annotate.JsonSerialize(using = OAuth2ExceptionJackson1Serializer.class)
@org.codehaus.jackson.map.annotate.JsonDeserialize(using = OAuth2ExceptionJackson1Deserializer.class)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = OAuth2ExceptionJackson2Serializer.class)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = OAuth2ExceptionJackson2Deserializer.class)
public class CustomOAuth2Exception extends OAuth2Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8574893475399540678L;

	public CustomOAuth2Exception(String msg, Throwable t) {
        super(msg, t);
        super.addAdditionalInformation("timestamp", String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)));
        super.addAdditionalInformation("uuid", UUID.randomUUID().toString());
    }

    public CustomOAuth2Exception(String msg, Throwable t, HttpStatus status) {
        this(msg, t);
        super.addAdditionalInformation("status", String.valueOf(status.value()));
    }

    public CustomOAuth2Exception(String msg, Throwable t, HttpStatus status, boolean debug) {
        this(msg, t, status);
        if (debug) {
            StringWriter trace = new StringWriter();
            t.printStackTrace(new PrintWriter(trace));
            super.addAdditionalInformation("message", t.getMessage());
            super.addAdditionalInformation("exception", t.getClass().getName());
            super.addAdditionalInformation("trace", trace.toString());
        }
    }

    @Override
    public String getOAuth2ErrorCode() {
        Throwable cause = this.getCause();
        if (InvalidGrantException.class.isAssignableFrom(cause.getClass())) {
            return sociality.server.exceptions.Error.Code.BAD_CREDENTIALS.toString();
        }
        if (OAuth2Exception.class.isAssignableFrom(cause.getClass())) {
            return ((OAuth2Exception) cause).getOAuth2ErrorCode();
        }
        return sociality.server.exceptions.Error.Code.AUTHENTICATION_ERROR.toString();
    }

}
