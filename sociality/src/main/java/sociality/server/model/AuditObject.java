package sociality.server.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import sociality.server.miscellaneous.CustomDateTimeSerializer;

@Entity(name = "audits")
@Table(name = "audits")
public class AuditObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5273294627820297105L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "action_description")
	private String actionDescription;

	@Column(name = "created_at")
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private LocalDateTime createdAt;

//	@ElementCollection(fetch = FetchType.EAGER)
//	@Column(name = "additional_info")
//	Map<String, String> additionalInfo = new HashMap<>();

	@Column(name = "username")
	private String username;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id")
	private User user;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getId() {
		return id;
	}

//	public Map<String, String> getAdditionalInfo() {
//		return additionalInfo;
//	}
//
//	public void setAdditionalInfo(Map<String, String> additionalInfo) {
//		this.additionalInfo = additionalInfo;
//	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getActionDescription() {
		return actionDescription;
	}

	public void setActionDescription(String actionDescription) {
		this.actionDescription = actionDescription;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
