package sociality.server.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ApiClientsDao {

	@Autowired
	private SessionFactory sessionFactory;

	public List<Map<String, Object>> getApiClients() {
		String sqlQuery = "select client_id,scope,authorized_grant_types,web_server_redirect_uri,access_token_validity,refresh_token_validity from oauth_client_details";
		List<Map<String, Object>> result = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		return result;
	}

	public Map<String, Object> getApiClient(String clientId) {
		String sqlQuery = "select client_id,scope,authorized_grant_types,web_server_redirect_uri,access_token_validity,refresh_token_validity from oauth_client_details where client_id = :client_id";
		Map<String, Object> result = (Map<String, Object>) sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
				.setString("client_id", clientId).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult();
		return result;
	}

	public void updateApi(String newClientId, String oldClientId, String redirectUri, Integer accessTokenValidity,
			Integer refreshTokenValidity) {
		String updateSql = "update oauth_client_details set client_id = COALESCE( :client_id, client_id),web_server_redirect_uri = COALESCE( :redirect_uri, web_server_redirect_uri)  ,access_token_validity = COALESCE( :access_token_validity, access_token_validity),refresh_token_validity = COALESCE( :refresh_token_validity, refresh_token_validity) where client_id = :old_client_id";
		sessionFactory.getCurrentSession().createSQLQuery(updateSql).setString("client_id", newClientId)
				.setString("redirect_uri", redirectUri).setInteger("access_token_validity", accessTokenValidity)
				.setInteger("refresh_token_validity", refreshTokenValidity).setString("old_client_id", oldClientId)
				.executeUpdate();
	}

	public void addClient(String clientId, String redirectUri, Integer accessTokenValidity,
			Integer refreshTokenValidity) {
		String updateSql = "insert into oauth_client_details(client_id,resource_ids,scope,authorized_grant_types,web_server_redirect_uri,authorities,access_token_validity,refresh_token_validity,autoapprove) values(:client_id,1,'trust','implicit',:redirect_uri,'ROLE_CLIENT',:access_token_validity,:refresh_token_validity,false)";
		sessionFactory.getCurrentSession().createSQLQuery(updateSql).setParameter("client_id", clientId)
				.setParameter("redirect_uri", redirectUri).setParameter("access_token_validity", accessTokenValidity)
				.setParameter("refresh_token_validity", refreshTokenValidity).executeUpdate();
	}
}
