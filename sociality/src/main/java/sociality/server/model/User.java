package sociality.server.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import sociality.server.facebook.FacebookSettings;
import sociality.server.miscellaneous.CustomDateTimeSerializer;
import sociality.server.twitter.TwitterSettings;

/**
 * Created by Tan on 18-Apr-16. User model class
 */
@Entity(name = "users")
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = { "username" }))
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5215772083924862628L;

	public enum Role {
		REGULAR, ROLE_MODERATOR, ROLE_ADMIN
	}

	public User() {
	}

	public User(Role role) {
		this.role = role;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "created_at")
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	@JsonIgnore
	private String password;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private Role role;

	@Column(name = "enabled")
	private Boolean enabled = false;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "twitterSettings_id")
	@JsonIgnore
	private TwitterSettings twitterSettings;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "facebookSettings_id")
	@JsonIgnore
	private FacebookSettings facebookSettings;

	public TwitterSettings getTwitterSettings() {
		return twitterSettings;
	}

	public void setTwitterSettings(TwitterSettings twitterSettings) {
		this.twitterSettings = twitterSettings;
	}

	public FacebookSettings getFacebookSettings() {
		return facebookSettings;
	}

	public void setFacebookSettings(FacebookSettings facebookSettings) {
		this.facebookSettings = facebookSettings;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		User user = (User) o;

		return id != null ? id.equals(user.id) : user.id == null;

	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
