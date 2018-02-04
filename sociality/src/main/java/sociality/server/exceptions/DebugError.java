package sociality.server.exceptions;

import org.springframework.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

public class DebugError extends Error {

    private String message;

    private String exception;

    private String trace;

    public DebugError(HttpStatus status, Exception e) {
        super(status);
        if (e != null) {
            this.exception = e.getClass().getName();
            this.message = e.getMessage();
            StringWriter trace = new StringWriter();
            e.printStackTrace(new PrintWriter(trace));
            this.trace = trace.toString();

            if (BaseException.class.isAssignableFrom(e.getClass())) {
                super.error = ((BaseException) e).getCode();
            }
        }
    }

    public DebugError(HttpStatus status, Exception e, Code error) {
        this(status, e);
        if(error != null) {
            super.error = error;
        }
    }

    public DebugError(HttpStatus status, Exception e, Code error, Map<String, Object> attributes) {
        this(status, e, error);
        this.attributes = attributes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

}
