1. Create a public client in keycloak 12.0.4. Pls check the props files in order to create realm and 
   public client. Also, this application accepts b2b and b2c user roles. These roles need to be configured in Keycloak

import sample postman collection for test.

curl -X POST \
http://localhost:8055/skillzag/auth/users/create \
-H 'cache-control: no-cache' \
-H 'content-type: application/json' \
-H 'postman-token: 7ce0eb6d-db6c-2fda-545d-3f1ec4d20a04' \
-d '{
"email": "debarati@gmail.com",
"firstname": "debarati",
"lastname": "basu",
"phoneNumber": "8217009274",
"password": "Debarati@123",
"role": "b2c",
"institutionName": "KALYANI",
"institutionID": "002"
}'

curl -X POST \
http://localhost:8080/auth/realms/skillzag-realm/protocol/openid-connect/token \
-H 'accept: application/json' \
-H 'cache-control: no-cache' \
-H 'content-type: application/x-www-form-urlencoded' \
-H 'postman-token: a1bd5365-8a5c-4183-1b79-61955222de0d' \
-d 'client_id=skillzag-app&grant_type=password&username=rahul%40gmail.com&password=rahul123'


curl -X GET \
http://localhost:8055/skillzag/auth/users/decrypt-token \
-H 'authorization: bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJPTWh2YW9XMHgxZW9xNWdTd1l2ajFzTjJ0VDU2NzE5aHM2eFdsZml2bDJFIn0.eyJleHAiOjE2MjgxNjI5ODgsImlhdCI6MTYyODE2MjY4OCwianRpIjoiMmE4ZWE5N2MtMDBkMy00ZjlmLWFlNjctY2Y4ZjgzYzUxNWNjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL3NraWxsemFnLXJlYWxtIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjY0ZTJkY2EzLTY0M2EtNGQ0MS1hODllLTI4MWJhNTA2MGViNiIsInR5cCI6IkJlYXJlciIsImF6cCI6InNraWxsemFnLWFwcCIsInNlc3Npb25fc3RhdGUiOiI1YmUxMmJmOS01Y2QyLTRjYWMtOTc3ZC01ODUzYjg5NTQzZmQiLCJhY3IiOiIxIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImIyYyIsImIyYiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJwYWRtaW4iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJyb2xlIjoiYjJjIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwaG9uZU51bWJlciI6IjkwNzg3ODExNzAiLCJpbnN0aXR1dGlvbklEIjoiMDAyIiwibmFtZSI6InNhbXJhdCBiYXN1IiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2FtdGluYS5iYXN1QGdtYWlsLmNvbSIsImdpdmVuX25hbWUiOiJzYW1yYXQiLCJmYW1pbHlfbmFtZSI6ImJhc3UiLCJlbWFpbCI6InNhbXRpbmEuYmFzdUBnbWFpbC5jb20ifQ.MfP7RRSk7ocmzmVcfd3CDRDHDQrxg8nk4DXJ7h4ibOKp8RmbUzPRcLRAkemlT9oyb6cxY1SiuoU6ojbcDLirDsULyrCWOm1mScdHFDD0IU9YkIExJuSDN_8X6pG4TqnAQ8zqtLdAEUI-4b6rSJpuhtZzOMr8sAWAWXcKk6He_rNO9P-yfzbLNhuTmsguk8QHbjguH1gES5b14IWgd6UU5vAlMxyiW30XCqVKJKKjD8pDqhVQkiQYiiy4X0KL2H4FK9NlSYuZ02YrZbEJhT6BDhdf5EUMapD0BWK_rv_JHaq66RL1FXAPU2KUzRhqfYFTDjdBxtQTO5SCf94TnPbHEQ' \
-H 'cache-control: no-cache' \
-H 'postman-token: 800544dc-5d28-94d1-bb11-1639d0cac6ff'



