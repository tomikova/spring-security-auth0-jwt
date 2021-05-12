# Spring Security Auth0 Demo Project

Project was made for demo purposes and demonstrates Auth0 flow where `Spring Vault` is used as an interface for communication with `Vault` which acts as a storage for dynamically generated and periodically rotated public/private key pair used for asymmetric JWT token signature (RSA algorithm).

Project consists of following parts:
- Auth server
- Resource server
- Frontend client application (soon)

## Auth Server

### Exposed endpoints

- `/auth/login` - Authenticates user by supported authentication method (username and password as of now for simplicity). Result of successful authentication is 
  JWT `access_token` and JWT `refresh_token` signed by private key.   
  Refresh token for user is stored in Vault and has much longer expiration time than access token.
  For identifying public key JWT header of access token has `kid` claim also stored in Vault.

#### Response  
```json
{
    "success": true,
    "result": {
        "token": "eyJraWQiOiI4MWQ4N2I0NC1lMDlhLTRjOGEtOGFmMS01NDRmMmY1ODhmODMiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJJaXR6TWFkbWFuIiwiaXNzIjoiTWFkd29ya3MiLCJyZWFsbSI6ImFjY2Vzc190b2tlbiIsImV4cCI6MTYyMDc3NjE1OH0.T6VHu_TPBIzSHRPcQwHuJ_MptWZ1y2L1Ailc-4KMVnVgVoiB5iGCtksIFN7KWSVSJpXZ9F6IJWQU4bopFnWNehbKjQWiHAN4TfI8lI-AMKxBnM_Nj5sfXpaPke8N4k9c1mKJN1X3QaxARidcurcUVFu82Dpg0Cd23r4H3Fuj10nawnJo_7ZAUmqlp1v4b2WnvMptSXIWi0Xc7e2XwJCMO--uQDNftS4oS3TtwNomFuYa0fo-hqQWg_4cgx44EFYgCxw8tf16ZV1RyKXxFeeTAk9Y3eyvY6BN1CpmumKmvPUV9TQ63KWQhXPwbF8hAU36YoxE9yxes3y1Fs3DPdriBw",
        "refreshToken": "eyJraWQiOiI4MWQ4N2I0NC1lMDlhLTRjOGEtOGFmMS01NDRmMmY1ODhmODMiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJJaXR6TWFkbWFuIiwiaXNzIjoiTWFkd29ya3MiLCJyZWFsbSI6InJlZnJlc2hfdG9rZW4iLCJleHAiOjE2MjA3ODY2NTh9.frRXilgUpykO5sKO1AVi7qLPoefUnn6QbdD4zGZzjM3JDLs86x0-MWyh6EJnq-Yf1wUjVYs50vJOOFHRcdrpLaN2PebTho01gx93MCsXprvA943P0aRpYhmG-UGkb7P386w6QzaKBUcukt3JBlPE3tnqSID1s_gR6bgahn2wjK9bwO3DIrUaXU4nH6mblThcFAxQPjIVUFj5N1vzRcODJLOaG2Ey8R6rzuFcZFY9wJPUs4yzLco8RDBnMEWnu58bzdS66eFEIiPR2EefXR-uG7PwpJ7ruPz4MhOVjyZzqLQilIFa2cmi_o9-K3kV-imfsWJ-x_vlxpKUaL0XM95xeg"
    }
}
```
  

- `/auth/public-token` - Used to retrieve public key from Vault by `kid` claim.

#### Response
```json
{
  "success": true,
  "result": {
    "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhjDW2K63TS6TWVtNuO3cfcCn5pZKNomNyZP4YIE3OoOLsdo4DKmfX+XDode9ybdxyTDeN+Um9shAmtrlMoOmtzzJCG2hLQggEw5NbD7pUjOfEjqH2zHuSznlIWll00wyeLhzHTzg8o+R3iHZtIZIfW1VD/bLw6MCoSpdY4BbNWPjGC1pVzU8HGxfPccZzAQTK/x4qiWE6u3L/cO3tkxtbjoEuDyMJL2y+j8zNEMVymsETiduwoRmPZ9obogo9XFGM+PEfZZ8jUW1zbh5xh+D9BmOBfklb0neBpzfMvRd8HFIamHhfaM2xwIsO+ZwkOGzgHeTvxKje1NmNmXOvwHiRQIDAQAB"
  }
}
```

- `/auth/verify-token` - When auth server is delegated with task of verifying token, token can be sent to this endpoint.

#### Response
```json
{
    "success": true,
    "result": "eyJraWQiOiI4MWQ4N2I0NC1lMDlhLTRjOGEtOGFmMS01NDRmMmY1ODhmODMiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJJaXR6TWFkbWFuIiwiaXNzIjoiTWFkd29ya3MiLCJyZWFsbSI6ImFjY2Vzc190b2tlbiIsImV4cCI6MTYyMDc3NjE1OH0.T6VHu_TPBIzSHRPcQwHuJ_MptWZ1y2L1Ailc-4KMVnVgVoiB5iGCtksIFN7KWSVSJpXZ9F6IJWQU4bopFnWNehbKjQWiHAN4TfI8lI-AMKxBnM_Nj5sfXpaPke8N4k9c1mKJN1X3QaxARidcurcUVFu82Dpg0Cd23r4H3Fuj10nawnJo_7ZAUmqlp1v4b2WnvMptSXIWi0Xc7e2XwJCMO--uQDNftS4oS3TtwNomFuYa0fo-hqQWg_4cgx44EFYgCxw8tf16ZV1RyKXxFeeTAk9Y3eyvY6BN1CpmumKmvPUV9TQ63KWQhXPwbF8hAU36YoxE9yxes3y1Fs3DPdriBw"
}
```

- `/auth/refresh-token` - New access token will be issued if valid (verified and not revoked) refresh token is sent.

#### Response
```json
{
    "success": true,
    "result": {
        "token": "eyJraWQiOiI4MWQ4N2I0NC1lMDlhLTRjOGEtOGFmMS01NDRmMmY1ODhmODMiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJJaXR6TWFkbWFuIiwiaXNzIjoiTWFkd29ya3MiLCJyZWFsbSI6ImFjY2Vzc190b2tlbiIsImV4cCI6MTYyMDc3NjQ2Nn0.NDQZcA8J84yAzdaD28P_IbKyQOQ0MKn1fRXMGOcPx28l9eQG5k_3XB1fTUkAQNiU1fvLAK_6rELQ1Q9mhFehuwb2pRRjjn1c5R5hujcyCyFhEzrM8AQeIinGiQoJ9Xi5qHkSSV0aKKHn1IOEo0EFcvMUUwpenPAd5YjWwlhNXmNeMGBnu-sPUIxrKYjhXGS7DPnHjj7cjCKDnFNeWc0rhz22X5Sklt4W7O1bANeEKhZwdxrpDyqeeOJ_kFzIwbSi0O00LIpKY_gr684YSjYve2GkYvgsj2DCiUJq_AUHGSWVjQs4nXiZkeKUi7NJMgGs7RGjL9m-zKLzpCiD_KE2qQ"
    }
}
```

### Features

- Asymmetric JWT signature
- Dynamic public/private key rotation by configurable scheduler
- Vault as a secret storage
- Distributed locking with [ShedLock](https://github.com/lukas-krecan/ShedLock)
- Hazelcast IMDG as a locking provider and event bus across auth server instances

As code to generate new key pair is in auth server itself and triggered by scheduler, to be able to run this task only once across all running instances `ShedLock` is used.  Please note that ShedLock is not distributed scheduler and here is used only to simulate this task by acquiring exclusive lock on `Hazelcast`.  
Key pair generation should be isolated process and triggered by other solutions for distributed scheduling. In that scenario Auth server should be notified of this event to be able to reload keys.  
In this project event listener is registered on particular Hazelcast map. Event is fired when data of last key rotation timestamp is added or updated in this map.  
That way every instance of Auth server is notified and can reach to Vault for new keys.

Secrets in Vault might look like below where `keyData` is key for currently used key par and kid and other json keys represent previously generated kids and corresponding public keys as there will be still active tokens in need of old public token. After some time or iteration old key-value pairs can be removed from Vault.

```json
{
  "db1575be-20f5-46e2-926f-7ec219d0ad0e": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg5eMDfw9r+atgZDK49sVeXkSwpNGffgtsB3AnzYh1C2X5R/uPe0wypRMBgA9CEGuAH1rYskAwhER96gNDPM4z9iPxfZ/aXnDKkb+kgvNurTMdNcYiQ/8ADlLvdbM7ebOB4zrrVpczB1xFaM+dvS47XQ26VfkV0osLkD1FmvQoqPc4/ay7XsSYcUOO/dqdLurvq6WgiedQ5Up5LnmVB0y44X0hFCMtPAG4OkcZtybbzgOwNY+gh36fiXgNUo5EjVIp7MJJtWpRm46ikwFuEOEFbP3dAsl0c1LNDZX/IULtUQTG2Mjhj7e7w86Ax1VoKkJpzG/uBSqaMt6RRpe/4b83QIDAQAB",
  "e43f708a-b64f-495e-a382-b07585fbc544": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAojPqAWzHF4F8z3Ybm3jMRwzAavcnGHMO32glHghc8o/c+aSjjUOyzbroqUWaXhven/4WbVOMcllOM6B6ha4FOkxf1iO7pBO/alVv4+nAxcaY7hcD+nIVlQyUyGFN9VPwsJeIeSA8oF6OXiKs/cjT2wlT71QCJu6kze+aZXyuZZGHYSS4bB2+Ut1cKGuI22iQxM08ys5oC6BJW6GXO6V0IM+a41El5TNLR/Qw/qvYZ15h7Bdi+pZa5xsEbfGuZy4mFPQBgxW0pxQ3p/EybUJFyPObHJgusoqoiGxAXI5ADXa1nDwMgzM+GiLv/N1FHPxTvafO7KLQX8QmdaxQlSSDdQIDAQAB",
  "keyData": {
    "kid": "364615d5-a11c-40e7-8952-8be0c3055188",
    "priKey": "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC8EUWqaKQycfUCYD4YiIEQg878lZCzPeYWxlqBNBJe/k1tbVFi9itzEP9OIdr/uMAoxGJ2oA7kBykpuXdPyfck97tyteG4xmQ24LXmMUgBDxdr+ybmtNELvipIyjUvWTecdyk8M/HiPCX2aGVUo0+NovRQU6dYRtiXrw6GYKQawjJdVY15D7/OKNf46U0p0RQ15/bauJSfqU0kfXzlUw8kUhRm4ofN4ejND7FQ+K9Lr666/vrsyQ+SCpLfwx4qY5drzdjuM6XaUbpZD7Gs1yRrYFK7zWwUlWXMU4bPNcUFyOnuC7r3boi8jAX2+2VI3YNa99EwKdRUATGqtqFTlHNvAgMBAAECggEBAIRlgG7UFevxb7PJf02UI5A1yqzkuiaFSAr2ftaAiwJW8rk7gVUyyinKaIFfsiXesWDByDOMwI7lP6RBDe6c1yEuSccaphqHiBteHJA+V1tvfWSmPZ+i4ZvrtybhO4nmvBCpjtz0EK/c+ji7C8MG6UVj160JB0FNNsOqGIafWEgBAr069uhQx32YIpDIrCCZ7wFwCpsKVsq3h0OvcKfPlLn4mTm3FsLW4zYpKU8ZAMEgoljNEsa1ygXL0+AdKFlKYgqw456fKvONhbHIx6Wb8ikuFW4c60vFBzqeAMl/eVdFC3nWnfW4dCtqThYyAVluhMC5W6MyJfTVZndde+y967kCgYEA7Xt4WxOukXpC2ppJuE64JGoYFe4DJC3P2q84JijaGe3ztat5lsokOhooUW4+gAoliuwSCyfEmk3IaX+GojDddu4svpLt04iVQDVF6+NQHm3aLeHEw/vE7ktuGj0D6dvdBQXgEJOtK3v01ga4x4UQ5C3inyf3YFKBHqHGwjwOKs0CgYEAyrtm9BaULhkg9Gzy4mzPr1+vNSoYH6fMLLtnBHm4bSZb3dK33nRjC1QUhB9qygfPEZKoqveOrAMoDo3KdMJIW2aaRVKhCfO3cW049HSB0IAPqMr+szObbrMfJRh4vdW08mboYLIUEAVShmPRF7HmY43XG+8eQUvlKhAzVLH2TysCgYEArv0zA1FuaY4AYxobRi7jKxnuI4KdV/RV25sPMbcads7KrMvsrTrIFPQfT1l/vlM7tLEc3pFwIg88pNguOabuGWuJFugnTJ6w834NxrJZ4AIsKXDZz1vekYSNXdIl5xV2N/RLVYurp4YQNAEB+SrI9ooFGieV9aj1sb+dOJSOD+UCgYB6Fk9S0UIdXL6u0+mVF+geeeX+g0IR1jAsBBNu64p4GPCb7mkSS07WJKVSR8U8s2Us9QAkLX868Y+u7A6vL8z5Vhmzg6Y9YwrnANqaxIrksCo+ATlPW9XP3Yj1Av67e7ZDgFuS18sjNsFS80uZFGZlL6cKSH8U3Yq9QRJYf++QDwKBgQDHwDXIX9w94gdBmTbcGIhG1mmoKgmReo1E8nrb/M9AzPbAOCWCnX2HY4OdtfMXj1ingiGqdMmW/momHFNuvy01+TnEUce6tJVMb7ftKejFZzctQiycoBuz0iwnrVPnsPxrjJWsBVi7bhLLT8RMO7iCP8hXAg1BRCvdSgusPn5zQg==",
    "pubKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvBFFqmikMnH1AmA+GIiBEIPO/JWQsz3mFsZagTQSXv5NbW1RYvYrcxD/TiHa/7jAKMRidqAO5AcpKbl3T8n3JPe7crXhuMZkNuC15jFIAQ8Xa/sm5rTRC74qSMo1L1k3nHcpPDPx4jwl9mhlVKNPjaL0UFOnWEbYl68OhmCkGsIyXVWNeQ+/zijX+OlNKdEUNef22riUn6lNJH185VMPJFIUZuKHzeHozQ+xUPivS6+uuv767MkPkgqS38MeKmOXa83Y7jOl2lG6WQ+xrNcka2BSu81sFJVlzFOGzzXFBcjp7gu6926IvIwF9vtlSN2DWvfRMCnUVAExqrahU5RzbwIDAQAB"
  }
}
```

### Customization

```yaml
# access_token expiration - 5min
  ttl-mills: 300000
  # how often will private/public keys be rotated - 15min
  key-rotation-frequency-mills: 900000
  # when will first rotation occur
  # 0 - immediately at service start
  initial-key-rotation-delay-mills: 20000
  # minimum time to hold lock on distributed task after acquiring
  min-lock-lease-time: 5s
  # maximum time to hold lock on distributed task after acquiring
  max-lock-lease-time: 10s
```

## Resource server

Resource server implements custom Spring security flow where most of the defaults are disabled as well as `SecurityAutoConfiguration`.

### Custom implementations

- `JwtAuthenticationFilter` - Decides if authentication is needed based on supplied anonymous urls. It also parses JWT access token from Authorization header where it is expected as `Bearer` token. Authentication is then delegated to custom authentication manager.


- `UniversalAuthenticationManager` via `JwtAuthenticationProvider` finally retrieves `Authentication` from authentication service.


- `JwtAuthenticationService` - Key part of authentication process. JWT headers are parsed from access token and `kid` is used to obtain public key needed for verification. For that purpose service will consult Auth server with parsed kid.  
  Auth server will respond with correct public key. Retrieved public key will then be stored in in-memory cache (Hazelcast implementation with configurable cache TTL) for further use.  
  Auth server will be consulted next time if cache in Resource server expired or if new `kid` appears as a result of key pair rotation on Auth server.  
  After JWT access token is successfully verified by public key for simplicity every user will have one default `GrantedAuthority` of `APP_USER` to test authorization mechanism.
  

- `UniversalAuthenticationEntryPoint` - Unauthorized exception handler.
  
### Resource 

Resource server exposes `/inventory` endpoint for fetching inventory data populated on start-up in in-memory `H2 database`.

#### Request

```text
curl -H 'Accept: application/json' -H "Authorization: Bearer eyJraWQiOiI4MWQ4N2I0NC1lMDlhLTRjOGEtOGFmMS01NDRmMmY1ODhmODMiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJJaXR6TWFkbWFuIiwiaXNzIjoiTWFkd29ya3MiLCJyZWFsbSI6ImFjY2Vzc190b2tlbiIsImV4cCI6MTYyMDc3NjYwOX0.V77yKWxeL4G7QDu7CacszKF-XuyPYrWGu7ocYjQ7di8jjr0tLo0XfAuLr_tikqIrBV95nk2a53IvoMz-jxn3yJw8qyasndBOMHLrWDsHAVjaty_pM_nS7EWNyQ-xACDI8faBPnEu08ZdrT24yAnXG07LLodyEiorR6EoDZeLNou2icgl3gQrNLObXBkn48X6nWHqldAaS1-s1HAIK97g9NirjRMIBvMjvR0_2NpQolR-WIs8iQOVZ6kq5kTqNBng0xnrENUJ_94NH9tUTFjyF80vPRkA3UoJg2ZGgfqv-cnunQOC5OWZtWPUnooyPhnHDICCs4T-L7lzYfrmqs0pXQ" http://localhost:8090/inventory
```

#### Response
```json
{
    "success": true,
    "result": [
        {
            "name": "IKEA chair",
            "quantity": 50,
            "unitPrice": 986.99,
            "currencyCode": "HRK"
        },
        {
            "name": "MacBook Pro",
            "quantity": 24,
            "unitPrice": 15500.99,
            "currencyCode": "HRK"
        },
        {
            "name": "MacBook Air",
            "quantity": 37,
            "unitPrice": 10500.99,
            "currencyCode": "HRK"
        }
    ]
}
```

## Setup

**1. Hazelcast**

```text
docker run -e HZ_NETWORK_PUBLICADDRESS=localhost:5701 -p 5701:5701 --name=dev-hazelcast hazelcast/hazelcast:4.1.3
```

Stop it with 
```text
docker stop dev-hazelcast
```

**2. Vault**

```text
docker run --cap-add=IPC_LOCK -e VAULT_DEV_ROOT_TOKEN_ID=myroot -e VAULT_DEV_LISTEN_ADDRESS=0.0.0.0:8200 -p 8200:8200 -d --name=dev-vault vault
```

Stop it with
```text
docker stop dev-vault
```

You should be able to login to Vault web interface on `http://localhost:8200/ui`. On login form enter value of VAULT_DEV_ROOT_TOKEN, here it is `myroot`.

**3. Auth server**

Clone or download this repository and navigate to `/auth-server` directory.  
You can run this project:  
- with your IDE, like IntelliJ IDEA
- with Gradle executing command
```text
gradlew bootRun
```
- by building this project with gradle command
```text
gradlew bootJar
```
after that you can run your jar as
```text
java -jar auth-server-0.0.1-SNAPSHOT.jar --server.port=8082
```

To observe distributed locking and event messaging start multiple instances of Auth server on different ports:  
```text
java -jar auth-server-0.0.1-SNAPSHOT.jar --server.port=8082
java -jar auth-server-0.0.1-SNAPSHOT.jar --server.port=8083
...
```

**4. Resource server**  
Same as previous step with Auth server
```text
java -jar resource-server-0.0.1-SNAPSHOT.jar --server.port=8090
```

## Future work

- Registering all instances to Service registry like `Eureka`
- Implementation of gateway like `Spring Cloud Gateway` or `Zuul`
- Frontend client application (Angular/React/Vue)

## License
[MIT](https://choosealicense.com/licenses/mit/)
