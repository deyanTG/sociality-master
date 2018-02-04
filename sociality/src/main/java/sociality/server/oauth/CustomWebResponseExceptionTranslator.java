package sociality.server.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

import sociality.server.exceptions.CustomOAuth2Exception;

@Component
public class CustomWebResponseExceptionTranslator extends DefaultWebResponseExceptionTranslator {

    private boolean debug = true;

    private final Logger logger = LoggerFactory.getLogger(CustomWebResponseExceptionTranslator.class);

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
        ResponseEntity<OAuth2Exception> defaultResponse = super.translate(e);

        OAuth2Exception defaultException = defaultResponse.getBody();
        HttpStatus status = defaultResponse.getStatusCode();
        CustomOAuth2Exception exception =
                new CustomOAuth2Exception(defaultException.getMessage(), defaultException, status, debug);
        logger.debug("oauth error", exception);
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(defaultResponse.getHeaders().toSingleValueMap());

        return new ResponseEntity<>(exception, headers, status);
    }
}
