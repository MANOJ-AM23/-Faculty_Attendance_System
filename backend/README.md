Backend (Spring Boot)

This folder contains the Java Spring Boot backend (source remains under `src/main/java`).

To build and run locally within this workspace we use the workspace Maven and local JDK (used by the assistant during testing). Typical commands:

```powershell
mvn -DskipTests package
java -jar target/faculty-attendance-1.0.0.jar --spring.profiles.active=local
```

Set `spring.datasource.*` in `src/main/resources/application.properties` to point to your MySQL instance when ready.
