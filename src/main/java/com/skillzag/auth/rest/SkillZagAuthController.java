package com.skillzag.auth.rest;

import java.util.*;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

import com.skillzag.auth.dto.AuthDTO;
import com.skillzag.auth.util.ResponseHelper;
import org.apache.commons.codec.binary.Base64;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.skillzag.auth.dto.UserDTO;
import org.springframework.web.client.RestTemplate;

import static com.skillzag.auth.util.Constants.EMPTY_ROLE_MESSAGE;
import static java.util.Objects.isNull;


@Validated
@RequestMapping(value = "/users")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SkillZagAuthController {

    private static final Logger log = LoggerFactory.getLogger(SkillZagAuthController.class);

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
        if (isNull(userDTO.getRole())) {
            return new ResponseEntity<>(ResponseHelper.populateRresponse(EMPTY_ROLE_MESSAGE, HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }

        Keycloak keycloak = KeycloakBuilder.builder().serverUrl(authServerUrl)
                .grantType(OAuth2Constants.PASSWORD).realm("master").clientId("admin-cli")
                .username(userid).password(password)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();
        Map<String, List<String>> attributes = new HashMap<>();
        if (!isNull(userDTO.getPhoneNumber())) {
            attributes.put("phoneNumber", Arrays.asList(userDTO.getPhoneNumber()));
        }
        attributes.put("role", Arrays.asList(userDTO.getRole()));
        if (!isNull(userDTO.getInstitutionName())) {
            attributes.put("institutionName", Arrays.asList(userDTO.getInstitutionName()));
        }
        if (!isNull(userDTO.getInstitutionID())) {
            attributes.put("institutionID", Arrays.asList(userDTO.getInstitutionID()));
        }
        if (!isNull(userDTO.getAddress1())) {
            attributes.put("address1", Arrays.asList(userDTO.getAddress1()));
        }
        if (!isNull(userDTO.getAddress2())) {
            attributes.put("address2", Arrays.asList(userDTO.getAddress2()));
        }
        keycloak.tokenManager().getAccessToken();
        String token = keycloak.tokenManager().getAccessTokenString();
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setFirstName(userDTO.getFirstname());
        user.setLastName(userDTO.getLastname());
        user.setEmail(userDTO.getEmail());
        user.setAttributes(attributes);
        user.setUsername(userDTO.getEmail());
        // Get realm
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersRessource = realmResource.users();

        Response response = usersRessource.create(user);

        userDTO.setStatusCode(response.getStatus());
        userDTO.setStatus(response.getStatusInfo().toString());

        if (response.getStatus() == 201) {
            String userId = CreatedResponseUtil.getCreatedId(response);
            log.info("Created userId {}", userId);
            // create password credential
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(userDTO.getPassword());
            UserResource userResource = usersRessource.get(userId);
            // Set password credential
            userResource.resetPassword(passwordCred);
            // Get realm role student
            RoleRepresentation realmRoleUser = realmResource.roles().get(userDTO.getRole()).toRepresentation();
            // Assign realm role student to user
            userResource.roles().realmLevel().add(Arrays.asList(realmRoleUser));
        }
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping(value = "/unprotected-api")
    public ResponseEntity<?> getName() {
        return ResponseEntity.ok("This api is NOT protected.");
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> userLogin(@RequestBody AuthDTO userDTO) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("client_id", "skillzag-app");
        requestBody.add("grant_type", "password");
        requestBody.add("username", userDTO.getEmail());
        requestBody.add("password", userDTO.getPassword());

        HttpEntity formEntity = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);
        ResponseEntity<?> response;
        try {
            response =
                    restTemplate.exchange(authServerUrl + "/realms/skillzag-realm/protocol/openid-connect/token"
                            , HttpMethod.POST, formEntity, Object.class);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getCause().toString(), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(response.getBody());

    }

    @GetMapping(value = "/decrypt-token")
    public ResponseEntity<?> decryptToken(@RequestHeader String authorization) {
        java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
        String[] parts = authorization.split("\\.");

        String base64EncodedHeader = parts[0];
        String base64EncodedBody = parts[1];

        Base64 base64Url = new Base64(true);

        log.info("~~~~~~~~~ JWT Header ~~~~~~~");
        String header = new String(base64Url.decode(base64EncodedHeader));
        log.info("JWT Header {} ", header);

        log.info("~~~~~~~~~ JWT Body ~~~~~~~");
        String body = new String(base64Url.decode(base64EncodedBody));
        log.info("JWT Body {} ", body);

        return ResponseEntity.ok(body);
    }

}