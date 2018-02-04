SET FOREIGN_KEY_CHECKS = 0;
drop table if exists users;
drop table if exists oauth_client_details;
drop table if exists oauth_refresh_token;
drop table if exists UserConnection;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS users (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  enabled bool DEFAULT TRUE,
  expert bool DEFAULT FALSE,
  first_name varchar(255) DEFAULT NULL,
  last_name varchar(255) DEFAULT NULL,
  password varchar(255) DEFAULT NULL,
  provider varchar(255) DEFAULT NULL,
  role varchar(255) DEFAULT 'COMPANY_USER',
  username varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UK_user_username (username)
);


create table IF NOT EXISTS oauth_client_details (
  client_id VARCHAR(100) PRIMARY KEY,
  resource_ids VARCHAR(256),
  client_secret VARCHAR(256),
  scope VARCHAR(256),
  authorized_grant_types VARCHAR(256),
  web_server_redirect_uri VARCHAR(256),
  authorities VARCHAR(256),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(4096),
  autoapprove VARCHAR(256)
);


create table IF NOT EXISTS oauth_refresh_token (
  token_id VARCHAR(100) PRIMARY KEY,
  token BLOB,
  authentication BLOB
);

create table UserConnection (userId varchar(255) not null,
    providerId varchar(255) not null,
    providerUserId varchar(255),
    rank int not null,
    displayName varchar(255),
    profileUrl varchar(512),
    imageUrl varchar(512),
    accessToken varchar(512) not null,
    secret varchar(512),
    refreshToken varchar(512),
    expireTime bigint,
    primary key (userId, providerId, providerUserId));
create unique index UserConnectionRank on UserConnection(userId, providerId, rank);