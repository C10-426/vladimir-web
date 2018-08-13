## How to develop

1. run `mvn eclipse:eclipse` under root directory
2. import it into eclipse

## How to run

1. install `mvnw` by running `mvn -N io.takari:maven:wrapper -Dmaven={MVN_VERSION}`
2. run `./mvnw spring-boot:run`

## How to debug

1. add a remote java application with localhost and {POST}
2. run `./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address={POST}"`
3. debug `Appilcation.java` as the remote java config
