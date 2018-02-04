package sociality.server.resolvers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonArgArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String JSON_BODY_ATTRIBUTE = "JSON_REQUEST_BODY";

	@Autowired
	private ObjectMapper mapper;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(JsonArg.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws IOException {
		JsonArg jsonParam = parameter.getParameterAnnotation(JsonArg.class);

		boolean required = jsonParam.required();
		String jsonParamName = jsonParam.name();

		if (StringUtils.isEmpty(jsonParamName)) {
			jsonParamName = parameter.getParameterName();
		}

		String body = this.getRequestBody(webRequest);

		JsonNode rootNode = mapper.readTree(body);
		JsonNode node = rootNode.path(jsonParamName);
		
		if(MissingNode.getInstance().equals(node) && !required){
			return null;
		}
		
		if (MissingNode.getInstance().equals(node) && required || node.toString().equals("\"\"") && required) {
			throw new JsonMappingException(String.format("Required parameter '%s' is missing", jsonParamName));
		}


		return mapper.readValue(node.toString(), parameter.getParameterType());
	}

	private String getRequestBody(NativeWebRequest webRequest) {
		HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
		String jsonBody = (String) servletRequest.getAttribute(JSON_BODY_ATTRIBUTE);
		if (jsonBody == null) {
			try {
				String body = StreamUtils.copyToString(servletRequest.getInputStream(), StandardCharsets.UTF_8);
				servletRequest.setAttribute(JSON_BODY_ATTRIBUTE, body);
				return body;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return jsonBody;

	}

}
