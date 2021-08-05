1. Create a public client and realm in keycloak


curl -X POST \
http://localhost:8055/services/auth/users/create \
-H 'cache-control: no-cache' \
-H 'content-type: application/json' \
-H 'postman-token: 7b24a4d9-4856-9b10-14da-ecbb32a281ba' \
-d '{ "email": "krishna@gmail.com", "firstname": "krishna", "lastname": "basu", "mobile": "999999999", "password": "krishna", "role": "b2b", "username": "krishna@gmail.com" }'


curl -X POST \
http://localhost:8080/auth/realms/skillzag-realm/protocol/openid-connect/token \
-H 'accept: application/json' \
-H 'cache-control: no-cache' \
-H 'content-type: application/x-www-form-urlencoded' \
-H 'postman-token: 0740491e-7176-0832-6984-839da22a35c9' \
-d 'client_id=skillzag-app&grant_type=password&username=krishna%40gmail.com&password=krishna123'

curl -X GET \
http://localhost:8055/services/auth/users/protected-api \
-H 'authorization: bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJPTWh2YW9XMHgxZW9xNWdTd1l2ajFzTjJ0VDU2NzE5aHM2eFdsZml2bDJFIn0.eyJleHAiOjE2MjgwMDY2MzMsImlhdCI6MTYyODAwNjMzMywianRpIjoiMjc4MzhhYTgtOWUxNi00OGQ1LWIxMGEtMmM2MjE5NzI2MGRlIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL3NraWxsemFnLXJlYWxtIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImEyY2E4M2ZmLTJjYTAtNDI2Mi05NDNhLTY5OTA2Mzg2ZmQ1NCIsInR5cCI6IkJlYXJlciIsImF6cCI6InNraWxsemFnLWFwcCIsInNlc3Npb25fc3RhdGUiOiIzMDBkZTI5Yi1mMjdmLTQ0MDAtYmU5Ni02N2FiYjg1MDE3ZTkiLCJhY3IiOiIxIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImIyYyIsImIyYiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJwYWRtaW4iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJrcmlzaG5hIGJhc3UiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJrcmlzaG5hQGdtYWlsLmNvbSIsImdpdmVuX25hbWUiOiJrcmlzaG5hIiwiZmFtaWx5X25hbWUiOiJiYXN1IiwiZW1haWwiOiJrcmlzaG5hQGdtYWlsLmNvbSJ9.aOegwQAB9w_tkQqlUBgn3SHJVtWA_lXI-3sYpiXLr0eKWlLkzdOHZV2YhMCCNxeyo1HAvg2W8BDnwCEgBBd98-d2dzeLdy4bZLPoJd-O7GIrev8qciUNabB6CHXAOCX66iRNqQ-E_0IW8BtzJbwSJuYjydhXHzohMzh1sG7B2I1JT4RkzKaWWzs4fD6klBH3qXe6c5UIV87nc42Mf_uUHVpj9rtuzDFVYx5LETYw1oYLCvkIrfKo_zA3GlnAUD6OpBi7nbtCU5qZ2srcjJzK4R9Z37pQ17SrYLrWzZxwykDXeXw89d2f9YstV1Kh-RsiaR0km-tXWpoBWKsOaXGAHw' \
-H 'cache-control: no-cache' \
-H 'postman-token: 62b41508-741b-f38a-e791-0b2003e2b4ed'



