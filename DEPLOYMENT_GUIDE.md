# Deployment Guide

## Build Application
```bash
mvn clean package
```

## Deploy to Tomcat
1. Copy the generated WAR file to Tomcat's webapps directory:
   ```bash
   cp target/Sprint-1.0-SNAPSHOT.war "C:/xampp/tomcat/webapps/"
   ```

2. Start Tomcat server

## Test API Endpoints

### JSON API Endpoint
```bash
curl "http://localhost:8080/Sprint-1.0-SNAPSHOT/api/saveUser?name=Test&email=test@example.com&age=30"
```

Expected response:
```json
{
  "firstName": "Test",
  "lastName": null,
  "email": "test@example.com",
  "age": 30
}
```

### Existing JSP Endpoint
```bash
curl "http://localhost:8080/Sprint-1.0-SNAPSHOT/saveUser?name=Test&email=test@example.com&age=30"
```

## Troubleshooting
1. Check Tomcat logs for errors:  
   `C:/xampp/tomcat/logs/catalina.out`
   
2. Verify JSON-B dependencies in pom.xml are included

3. Ensure port 8080 is available (or adjust in Tomcat config)
