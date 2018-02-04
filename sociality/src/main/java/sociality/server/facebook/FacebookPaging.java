package sociality.server.facebook;

import org.springframework.social.facebook.api.PagingParameters;

public class FacebookPaging {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PagingParameters params;

	public FacebookPaging(PagingParameters params) {
		this.params = params;
	}

}
