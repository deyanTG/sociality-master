package sociality.server.controllers;

import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.ConnectController;

public class CustomSocialityConnectController extends ConnectController {

	public CustomSocialityConnectController(ConnectionFactoryLocator connectionFactoryLocator,
			ConnectionRepository connectionRepository) {
		super(connectionFactoryLocator, connectionRepository);
	}

	/**
	 * Just a dummy constructor
	 */
	public CustomSocialityConnectController() {
		super(null, null);
	}

	@Override
	protected String connectView(String providerId) {
		return determineUrlForProvider(providerId);
	}

	@Override
	protected String connectedView(String providerId) {
		return determineUrlForProvider(providerId);
	}

	private String determineUrlForProvider(String providerId) {
		if ("facebook".equalsIgnoreCase(providerId)) {
			return getDefaultFacebookStatusUrl();
		} else {
			if ("twitter".equalsIgnoreCase(providerId)) {
				return getDefaultTwitterUrl();
			}
		}
		return null;
	}

	private String getDefaultTwitterUrl() {
		return "redirect:http://sociality.com/sociality-client/index.html#/twitter";
	}

	private String getDefaultFacebookStatusUrl() {
		return "redirect:http://sociality.com/sociality-client/index.html#/facebook";
	}

}
