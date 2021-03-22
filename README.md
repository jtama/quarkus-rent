# one-rent project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/one-rent-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

## Hostel requests

### Get ALL

```bash
curl -X GET "http://localhost:8080/api/hostels" -H "accept: application/json"
```

### Create one

#### Valid

```bash
curl -X POST "http://localhost:8080/api/hostels" -H "X-user-name: Joe" -H "X-user-roles: USER,ADMIN" -H "Content-Type: application/json" -d "{\"name\": \"Ritz\"}"

curl -X POST "http://localhost:8080/api/hostels" -H "X-user-name: Joe" -H "X-user-roles: ADMIN" -H "Content-Type: application/json" -d "{\"name\": \"Four seasons Hostel\"}"
```

#### Invalid

```bash
curl -X POST "http://localhost:8080/api/rockets" -H "X-user-name: Joe" -H "X-user-roles: USER" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"name\": \"string\", \"type\": \"ECONOMY\"}"
```

### Book one

#### Valid

```bash
curl -X POST "http://localhost:8080/api/hostels/Ritz?month=2" -H "accept: application/json" -H "X-user-name: Joe" -H "X-user-roles: USER"
```

WARNING: Don't repeat this operation.

#### Invalid

```bash
curl -X POST "http://localhost:8080/api/hostels/book/Ritz?month=42" -H "accept: application/json" -H "X-user-name: Joe" -H "X-user-roles: USER"
```

## Rocket requests

### Get ALL

```bash
curl -X GET "http://localhost:8080/api/rockets" -H "accept: application/json"
```

### Create one

#### Valid

```bash
curl -X POST "http://localhost:8080/api/rockets" -H "X-user-name: Joe" -H "X-user-roles: USER,ADMIN" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"name\": \"Mercury-Redstone\", \"type\": \"LUXURY\"}"

curl -X POST "http://localhost:8080/api/rockets" -H "X-user-name: Joe" -H "X-user-roles: ADMIN" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"name\": \"APOLLO 1\", \"type\": \"EXPLOSIVE\"}"
```

#### Invalid

```bash
curl -X POST "http://localhost:8080/api/rockets" -H "X-user-name: Joe" -H "X-user-roles: USER" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"id\": 0, \"name\": \"string\", \"type\": \"ECONOMY\"}"
```

### Book one

#### Valid

```bash
curl -X POST "http://localhost:8080/api/rockets/APOLLO%201/book?month=2" -H "accept: application/json" -H "X-user-name: Joe" -H "X-user-roles: USER"
```

WARNING: Don't repeat this operation.

#### Invalid

```bash
//Invalid Month
curl -X POST "http://localhost:8080/api/rockets/APOLLO%201?month=2" -H "accept: application/json" -H "X-user-name: Joe" -H "X-user-roles: USER"
//No hostel booked
curl -X POST "http://localhost:8080/api/rockets/APOLLO%201?month=3" -H "accept: application/json" -H "X-user-name: Joe" -H "X-user-roles: USER"
```

## Full valid sequence


```bash
curl -X POST "http://localhost:8080/api/hostels" -H "X-user-name: Joe" -H "X-user-roles: USER,ADMIN" -H "Content-Type: application/json" -d "{ \"id\": 0, \"name\": \"Ritz\"}"
curl -X POST "http://localhost:8080/api/hostels/Ritz/book?month=2" -H "accept: application/json" -H "X-user-name: Joe" -H "X-user-roles: USER"
curl -X POST "http://localhost:8080/api/rockets" -H "X-user-name: Joe" -H "X-user-roles: ADMIN" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"name\": \"APOLLO 1\", \"type\": \"EXPLOSIVE\"}"
curl -X POST "http://localhost:8080/api/rockets/APOLLO%201/book?month=2" -H "accept: application/json" -H "X-user-name: Joe" -H "X-user-roles: USER"
```
