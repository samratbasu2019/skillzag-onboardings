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
http://localhost:8055/skillzag/auth/users/view-token \
-H 'authorization: bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJPTWh2YW9XMHgxZW9xNWdTd1l2ajFzTjJ0VDU2NzE5aHM2eFdsZml2bDJFIn0.eyJleHAiOjE2MjgxNDc1MjAsImlhdCI6MTYyODE0NzIyMCwianRpIjoiM2QyYTYxNWItYzFkZC00ODFhLThlYTgtMWRlMDhjMDhjYWYxIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL3NraWxsemFnLXJlYWxtIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjY4OGNiOGFiLWQ1ZmMtNDlkMS1iZmE5LWEzNTM4ZTYxODBkZCIsInR5cCI6IkJlYXJlciIsImF6cCI6InNraWxsemFnLWFwcCIsInNlc3Npb25fc3RhdGUiOiJhNDJiNjAwMi1iMGI3LTQ1N2YtOTQxMy00NWY2ZDAzYmRjNjgiLCJhY3IiOiIxIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImIyYyIsImIyYiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJwYWRtaW4iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJyb2xlIjoiYjJjIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwaG9uZU51bWJlciI6IjExMTExMTExIiwiaW5zdGl0dXRpb25JRCI6IjAwMSIsIm5hbWUiOiJzYW50YW51IGRlYm5hdGgiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzYW50YW51QGdtYWlsLmNvbSIsImdpdmVuX25hbWUiOiJzYW50YW51IiwiZmFtaWx5X25hbWUiOiJkZWJuYXRoIiwiZW1haWwiOiJzYW50YW51QGdtYWlsLmNvbSJ9.D5bfDGonz-fzgYUFGkc5vB4DnQba0rVxxFMsz07OVuv_vpwtLs9XDwkmXOpOv8IZny_EULqpCSn67tKrXpIRGdpF9xE7ycHdsfcwjQKTYmXCtjd2HUlPy5R0hVHZWi9kz-N24jIYF64pq_kpM6LucbHF3OWLmx4qigX-_1XteKjVh5XHBxDtty3IQtR0Y6mHnvnB_-a5XswsPeLJTp3Jw_m88hXU8Yr-WSUaf4j83Fr2-9XGkt5nDVUhgzFTYG-ftGEup7ADLLKl78L4mEi-gYPYkJJkVcWw9Vumjw1g8UrGh0gpS9Qch1UwvD_81Q8thZe0t0DoJBHo08FNJMQatA' \
-H 'cache-control: no-cache' \
-H 'postman-token: b1e0ffd1-3e68-4bc8-e2fa-1964177e8e33'



