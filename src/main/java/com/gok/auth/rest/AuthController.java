package com.gok.auth.rest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

import com.gok.auth.dto.PasswordDTO;
import org.apache.commons.codec.binary.Base64;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gok.auth.dto.UserDTO;
import org.springframework.web.client.RestTemplate;


@Validated
@RequestMapping(value = "/users")
@RestController
public class AuthController {

    protected final Log log = LogFactory.getLog(this.getClass());

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${app.secret}")
    private String clientSecret;
    @Value("${app.keycloak.userid}")
    private String userid;
    @Value("${app.keycloak.password}")
    private String password;
    @Value("${app.user.url}")
    private String userUrl;

    @PostMapping(path = "/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO) {

        Keycloak keycloak = KeycloakBuilder.builder().serverUrl(authServerUrl)
                .grantType(OAuth2Constants.PASSWORD).realm("master").clientId("admin-cli")
                .username(userid).password(password)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();

        keycloak.tokenManager().getAccessToken();
        String token = keycloak.tokenManager().getAccessTokenString();
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstname());
        user.setLastName(userDTO.getLastname());
        user.setEmail(userDTO.getEmail());
        //user.setRequiredActions(Arrays.asList("Update Password"));
        user.setAttributes(Collections.singletonMap("mobile", Arrays.asList(userDTO.getMobile())));
        // Get realm
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersRessource = realmResource.users();

        Response response = usersRessource.create(user);

        userDTO.setStatusCode(response.getStatus());
        userDTO.setStatus(response.getStatusInfo().toString());

        if (response.getStatus() == 201) {
            String userId = CreatedResponseUtil.getCreatedId(response);
            log.info("Created userId : " + userId);
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            // Set password credential
            log.info("User password is : " + userDTO.getPassword());
            // userResource.resetPassword(passwordCred);
            // Get realm role
            RoleRepresentation realmRoleUser = realmResource.roles().get(userDTO.getRole()).toRepresentation();
            // Assign realm role to user
            userResource.roles().realmLevel().add(Arrays.asList(realmRoleUser));
            PasswordDTO up = new PasswordDTO();
            up.setTemporary(true);
            up.setType("password");
            up.setValue(userDTO.getPassword());
            setUserPassword(token, up, userId);
        }
        return ResponseEntity.ok(userDTO);
    }

    private void setUserPassword(String token, PasswordDTO passwordDTO, String userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(token);
        HttpEntity<PasswordDTO> entity = new HttpEntity<PasswordDTO>(passwordDTO, headers);

        ResponseEntity<String> body = restTemplate.exchange(
                userUrl + userId + "/reset-password"
                , HttpMethod.PUT, entity, String.class);
    }

    @PostMapping(path = "/signin")
    public ResponseEntity<?> signin(@RequestBody UserDTO userDTO) {

        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", clientSecret);
        clientCredentials.put("grant_type", "password");

        Configuration configuration =
                new Configuration(authServerUrl, realm, clientId, clientCredentials, null);
        AuthzClient authzClient = AuthzClient.create(configuration);
        AccessTokenResponse response = null;
        try {
            response =
                    authzClient.obtainAccessToken(userDTO.getUsername(), userDTO.getPassword());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid user credentials");
        }

        return ResponseEntity.ok(response);
    }


    @GetMapping(value = "/unprotected-api")
    public ResponseEntity<String> getName() {
        return ResponseEntity.ok("This api is NOT protected.");
    }


    @GetMapping(value = "/protected-api")
    public ResponseEntity<String> getEmail(@RequestHeader String authorization) {
        java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
        String[] parts = authorization.split("\\.");

        String base64EncodedHeader = parts[0];
        String base64EncodedBody = parts[1];

        Base64 base64Url = new Base64(true);

        log.info("~~~~~~~~~ JWT Header ~~~~~~~");
        String header = new String(base64Url.decode(base64EncodedHeader));
        log.info("JWT Header : " + header);

        log.info("~~~~~~~~~ JWT Body ~~~~~~~");
        String body = new String(base64Url.decode(base64EncodedBody));
        log.info("JWT Body : " + body);

        return ResponseEntity.ok("This api is protected. JWT Body " + body);
    }

}