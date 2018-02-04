package sociality.server.advice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.NotAuthorizedException;
import org.springframework.social.RateLimitExceededException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.google.common.collect.ImmutableMap;

import sociality.server.exceptions.DebugError;
import sociality.server.exceptions.Error;
import sociality.server.exceptions.ItemNotFoundException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

	public boolean debug = true;

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	@ExceptionHandler({ ItemNotFoundException.class, NoHandlerFoundException.class, UsernameNotFoundException.class })
	public Error handleItemNotFound(ServletWebRequest request, Exception exception) {
		return buildError(request, HttpStatus.NOT_FOUND, exception);
	}

	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	@ResponseBody
	@ExceptionHandler({ AuthenticationException.class, NotAuthorizedException.class })
	public Error handleAuthentication(ServletWebRequest request, Exception exception) {
		return buildError(request, HttpStatus.UNAUTHORIZED, exception);
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler({ MethodArgumentNotValidException.class })
	public Error handleValidationException(ServletWebRequest request, MethodArgumentNotValidException exception) {
		List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
		Map<String, Object> attributes = new HashMap<>();
		for (FieldError error : fieldErrors) {
			attributes.put(error.getField(), error.getDefaultMessage());
		}
		return buildError(request, HttpStatus.BAD_REQUEST, exception, attributes, sociality.server.exceptions.Error.Code.VALIDATION_ERROR);
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler({ RateLimitExceededException.class })
	public Error handleTwitterRateLimitException(ServletWebRequest request, Exception exception) {
		return buildError(request, HttpStatus.BAD_REQUEST, exception);
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	@ExceptionHandler({ Exception.class })
	public Error handleUnknownException(ServletWebRequest request, Exception exception) {
		return buildError(request, HttpStatus.INTERNAL_SERVER_ERROR, exception);
	}

	private Error buildError(ServletWebRequest request, HttpStatus status, Exception exception) {
		Map<String,Object> attr = null;
		if(exception instanceof RateLimitExceededException){
			attr = ImmutableMap.of("type","twitterRateLimit");
		}
		return buildError(request, status, exception, attr);
	}

	private Error buildError(ServletWebRequest request, HttpStatus status, Exception exception,
			Map<String, Object> additionalAttributes) {
		return buildError(request, status, exception, additionalAttributes, null);
	}

	private Error buildError(ServletWebRequest request, HttpStatus status, Exception exception,
			Map<String, Object> additionalAttributes, Error.Code code) {
		Error error;
		if (debug) {
			error = new DebugError(status, exception, code, additionalAttributes);
		} else {
			error = new Error(status, code, additionalAttributes);
		}
		logger.error("\nUUID: {} \nURI: {} \nAdditional Attributes: {}", error.getUuid().toString(),
				request.getRequest().getRequestURI(), additionalAttributes, exception);
		return error;
	}

}
