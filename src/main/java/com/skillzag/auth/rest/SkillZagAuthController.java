package com.skillzag.auth.rest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillzag.auth.dto.*;
import com.skillzag.auth.util.ResponseHelper;
import com.skillzag.auth.util.Utility;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.skillzag.auth.util.Constants.*;
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
    @Value("${app.image-upload-path}")
    private String imageContext;

    final ObjectMapper mapper = new ObjectMapper();

    @PostMapping(path = "/create")
    public ResponseEntity<?> createUser(String request,
                                        @RequestParam(value = "file", required = false) MultipartFile file) throws JsonProcessingException {
        UserDTO userDTO = mapper.readValue(request, new TypeReference<UserDTO>() {
        });
        if (isNull(userDTO.getRole()) || !(Arrays.asList("b2b", "b2c", "b2badmin", "platformadmin").contains(userDTO.getRole()))) {
            return new ResponseEntity<>(ResponseHelper.populateRresponse(EMPTY_ROLE_MESSAGE, HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }

        if (isNull(userDTO.getEmail()) || !(Utility.isValidEmail(userDTO.getEmail()))) {
            return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_EMAIL, HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }

        if (!isNull(userDTO.getIsBulkUpload()) && userDTO.getIsBulkUpload()) {
            if (isNull(userDTO.getPassword()) || StringUtils.isEmpty(userDTO.getPassword())) {
                return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_PASSWORD, HttpStatus.BAD_REQUEST.value()),
                        HttpStatus.BAD_REQUEST);
            }
        }
        if (isNull(userDTO.getInstitutionID()) || StringUtils.isEmpty(userDTO.getInstitutionID())) {
            return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_INSTITUTION, HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }
        if (isNull(userDTO.getFirstname()) || StringUtils.isEmpty(userDTO.getFirstname())) {
            return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_FIRSTNAME, HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }
        if (isNull(userDTO.getLastname()) || StringUtils.isEmpty(userDTO.getLastname())) {
            return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_LASTNAME, HttpStatus.BAD_REQUEST.value()),
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
        if (!isNull(userDTO.getValidFrom())) {
            attributes.put("validFrom", Arrays.asList(userDTO.getValidFrom().toString()));
        }
        if (!isNull(userDTO.getValidTo())) {
            attributes.put("validTo", Arrays.asList(userDTO.getValidTo().toString()));
        }
        if (!isNull(userDTO.getSubscriptionType())) {
            attributes.put("subscriptionType", Arrays.asList(userDTO.getSubscriptionType()));
        }
        if (!isNull(userDTO.getSubscriptionStartDate())) {
            attributes.put("subscriptionStartDate", Arrays.asList(userDTO.getSubscriptionStartDate().toString()));
        }
        if (!isNull(userDTO.getSubscriptionEndDate())) {
            attributes.put("subscriptionEndDate", Arrays.asList(userDTO.getSubscriptionEndDate().toString()));
        }

        if (!isNull(file) && !StringUtils.isEmpty(file.getOriginalFilename())) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = FilenameUtils.getExtension(fileName);
            final String uuid = UUID.randomUUID().toString().replace("-", "");
            String finalFileName = uuid + "." + extension;
            Path path = Paths.get(imageContext + finalFileName);
            log.info("Path is path {}", path.toString());
            try {
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(imageContext)
                    .path(fileName)
                    .toUriString();
            log.info("event=createUser uploaded image location {}", fileDownloadUri);

            attributes.put("imagePath", Arrays.asList(path.toString()));
        }
        keycloak.tokenManager().getAccessToken();
        String token = keycloak.tokenManager().getAccessTokenString();
        UserRepresentation user = new UserRepresentation();
        if (!(!isNull(userDTO.getIsBulkUpload()) && userDTO.getIsBulkUpload())) {
            user.setEnabled(true);
        } else {
            user.setEnabled(false);
        }
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
            if (!(!isNull(userDTO.getIsBulkUpload()) && userDTO.getIsBulkUpload())) {
                passwordCred.setTemporary(true);
                passwordCred.setValue("SkillZag@Password1");
            } else {
                passwordCred.setTemporary(false);
                passwordCred.setValue(userDTO.getPassword());
            }
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            UserResource userResource = usersRessource.get(userId);
            // Set password credential
            userResource.resetPassword(passwordCred);
            // Get realm role student
            RoleRepresentation realmRoleUser = realmResource.roles().get(userDTO.getRole()).toRepresentation();
            // Assign realm role student to user
            userResource.roles().realmLevel().add(Arrays.asList(realmRoleUser));
            if (!(!isNull(userDTO.getIsBulkUpload()) && userDTO.getIsBulkUpload())) {
                userResource.sendVerifyEmail();
            }
        }
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping(value = "/update/{id}")
    public ResponseEntity<?> updateUsers(@PathVariable("id") String userId, @RequestBody UserAttribute userAttribute) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("authorization", "bearer " + getAdminToken());
        HttpEntity<UserAttribute> formEntity = new HttpEntity<UserAttribute>(userAttribute, headers);
        ResponseEntity<?> response;
        try {
            String url = authServerUrl + "/admin/realms/skillzag-realm/users/" + userId;
            log.info("event=updateUsers url {}", url);
            response = restTemplate.exchange(url, HttpMethod.PUT, formEntity, Object.class);

        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_CREDENTIAL_MESSAGE,
                    HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllUsers() {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "bearer " + getAdminToken());
        HttpEntity formEntity = new HttpEntity<MultiValueMap<String, String>>(null, headers);
        ResponseEntity<?> response;
        try {
            response = restTemplate.exchange(authServerUrl + "/admin/realms/skillzag-realm/users"
                    , HttpMethod.GET, formEntity, Object.class);

        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_CREDENTIAL_MESSAGE,
                    HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping(value = "/by-userid/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable("userId") String userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "bearer " + getAdminToken());
        HttpEntity formEntity = new HttpEntity<MultiValueMap<String, String>>(null, headers);
        ResponseEntity<?> response;
        try {
            response = restTemplate.exchange(authServerUrl + "/admin/realms/skillzag-realm/users/?username="
                    + userId, HttpMethod.GET, formEntity, Object.class);

        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_CREDENTIAL_MESSAGE,
                    HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping(value = "/by-role/{role}")
    public ResponseEntity<?> getUserByType(@PathVariable("role") String role) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "bearer " + getAdminToken());
        HttpEntity formEntity = new HttpEntity<MultiValueMap<String, String>>(null, headers);
        ResponseEntity<?> response;
        final List<Contract> res;
        try {
            response = restTemplate.exchange(authServerUrl + "/admin/realms/skillzag-realm/users",
                    HttpMethod.GET, formEntity, Object.class);

            final List<Contract> responseObj = mapper.convertValue(response.getBody(), new TypeReference<List<Contract>>() {
            });
            res = responseObj.stream().filter(f -> f.getAttributes().getRole().contains(role)).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_CREDENTIAL_MESSAGE,
                    HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(res);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> userLogin(@RequestBody AuthDTO userDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (!isNull(userDTO.getHasTermsChecked()) && !userDTO.getHasTermsChecked()) {
            return new ResponseEntity<>(ResponseHelper.populateRresponse(CHECK_TERMS_CONDITION,
                    HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("client_id", "skillzag-app");
        requestBody.add("grant_type", "password");
        requestBody.add("username", userDTO.getEmail());
        requestBody.add("password", userDTO.getPassword());

        HttpEntity formEntity = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);
        ResponseEntity<?> response;
        try {
            response = restTemplate.exchange(authServerUrl + "/realms/skillzag-realm/protocol/openid-connect/token"
                    , HttpMethod.POST, formEntity, Object.class);

        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_CREDENTIAL_MESSAGE,
                    HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }

        return decodeToken(response.getBody().toString());

    }

    private ResponseEntity<?> decodeToken(String authorization) throws JsonProcessingException {
        String[] parts = authorization.split("\\.");
        String base64EncodedBody = parts[1];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody));
        Map<String, Object> responseObj = new ObjectMapper().readValue(body, Map.class);
        Map<String, Object> res = new HashMap<>();
        res.put("status", "success");
        res.put("role", responseObj.get("role"));
        res.put("email", responseObj.get("email"));
        res.put("institutionID", responseObj.get("institutionID"));
        if (!isNull(responseObj.get("imagePath")) && !StringUtils.isEmpty(responseObj.get("imagePath").toString())) {
            res.put("imagePath", responseObj.get("imagePath").toString().replace(imageContext, ""));
        }
        res.put("token", authorization);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/upload/{id}")
    public ResponseEntity uploadImageToLocalFileSystem(@RequestParam("file") MultipartFile file,
                                                       @PathVariable("id") String userId, String attribute) {
        if (!isNull(file) && !StringUtils.isEmpty(file.getOriginalFilename())) {
            UserAttribute userAttribute;
            try {
                userAttribute = mapper.readValue(attribute, new TypeReference<UserAttribute>() {
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_USER_ATTRIBUTE, HttpStatus.BAD_REQUEST.value()),
                        HttpStatus.BAD_REQUEST);
            }
            if (!isNull(userAttribute)) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                String extension = FilenameUtils.getExtension(fileName);
                final String uuid = UUID.randomUUID().toString().replace("-", "");
                String finalFileName = uuid + "." + extension;
                Path path = Paths.get(imageContext + finalFileName);
                log.info("Path is path {}", path.toString());
                try {
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(imageContext)
                        .path(fileName)
                        .toUriString();
                log.info("event=createUser uploaded image location {}", fileDownloadUri);

                Attributes attributes = userAttribute.getAttributes();
                attributes.setImagePath(Arrays.asList(path.toString()));
                userAttribute.setAttributes(attributes);
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                headers.add("authorization", "bearer " + getAdminToken());
                HttpEntity<UserAttribute> formEntity = new HttpEntity<UserAttribute>(userAttribute, headers);
                ResponseEntity<?> response;
                try {
                    String url = authServerUrl + "/admin/realms/skillzag-realm/users/" + userId;
                    log.info("event=updateUsers url {}", url);
                    response = restTemplate.exchange(url, HttpMethod.PUT, formEntity, Object.class);

                } catch (Exception e) {
                    return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_CREDENTIAL_MESSAGE,
                            HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(ResponseHelper.populateRresponse(REQUEST_SUCCESSFUL,
                        HttpStatus.OK.value()), HttpStatus.OK);
            }
            return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_USER_ATTRIBUTE, HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_IMAGEFILE, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity downloadImageFromLocal(@PathVariable String fileName) {
        String contentType = "application/octet-stream";
        Path path = Paths.get(imageContext + fileName);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping(value = "/decrypt-token")
    public ResponseEntity<?> decryptToken(@RequestHeader String authorization) throws JsonProcessingException {
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

        Map<String, Object> responseObj = new ObjectMapper().readValue(body, Map.class);

        return ResponseEntity.ok(responseObj);
    }

    private String getAdminToken() {
        Keycloak keycloak = KeycloakBuilder.builder().serverUrl(authServerUrl)
                .grantType(OAuth2Constants.PASSWORD).realm("master").clientId("admin-cli")
                .username(userid).password(password)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();
        keycloak.tokenManager().getAccessToken();
        return keycloak.tokenManager().getAccessTokenString();
    }

    @PostMapping(value = "/{userId}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable("userId") String userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "bearer " + getAdminToken());
        HttpEntity formEntity = new HttpEntity<MultiValueMap<String, String>>(null, headers);
        ResponseEntity<?> response;
        CloseableHttpResponse resetPWDResponse;
        String id;
        try {
            response = restTemplate.exchange(authServerUrl + "/admin/realms/skillzag-realm/users/?username="
                    + userId, HttpMethod.GET, formEntity, Object.class);
            final List<Contract> pojo = mapper.convertValue(response.getBody(), new TypeReference<List<Contract>>() {
            });
            id = pojo.get(0).getId();
            log.info("event=resetPassword user-id {}", id);
            CloseableHttpClient httpclient = HttpClients.createDefault();

            String urlResetPassword = authServerUrl + "/admin/realms/skillzag-realm/users/" + id + "/execute-actions-email";
            HttpPut putRequest = new HttpPut(urlResetPassword);
            putRequest.addHeader("Authorization", "bearer " + getAdminToken());
            putRequest.addHeader("content-type", String.valueOf(MediaType.APPLICATION_JSON));
            putRequest.setHeader("Accept", String.valueOf(MediaType.APPLICATION_JSON));
            StringEntity jSonEntity = new StringEntity("[\"UPDATE_PASSWORD\"]");
            putRequest.setEntity(jSonEntity);
            resetPWDResponse = httpclient.execute(putRequest);
            log.info("event=resetPassword password has been reset entity {}", resetPWDResponse.getEntity());
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHelper.populateRresponse(INVALID_CREDENTIAL_MESSAGE,
                    HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(resetPWDResponse.getEntity());
    }
}