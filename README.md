# GitHub Repository API

This API was developed for a Cocus interview code challenge Project using Java language,
Spring Boot Framework and Maven with Intellij IDE.

The purpose of this API is to get all the repositories in GitHub of a given username with is branches and las commit sha of each branch.


## API Documentation

#### Retrieve all repositories from a given username

```http
  GET /repos
```

| Parameters  | Type   | Description                                                                                                         |
|:------------|:-------|:--------------------------------------------------------------------------------------------------------------------|
| Object      | JSON   | Client sends a body with a json that wuth the key: "username" and  the value must be the wanted username to search. |
| Header      | Accept | Client can not send this parameter, but in case it sends it will only be accepted application/json                  |

## How to run locally

Clone the project: 

```bash
  git clone https://github.com/HenriqueGuim/CocusChallenge.git
```

Go to the project directory

```bash
  cd CocusChallenge
```

Install dependencies

```bash
  mvn cleanex install
```

Set the environment variable needed

```bash
  export GITHUB_TOKEN={YOUR_GITHUB_TOKEN}
```

Start the server

```bash
  mvn spring-boot:run
```



## How to build into a docker image
Run the following command in the project directory
```bash
  docker build -t apigithub . --build-arg=GITHUB_TOKEN={YOUR_GITHUB_TOKEN}
```

## How to run into a docker container
Set the environment variable needed

```bash
  export GITHUB_TOKEN={YOUR_GITHUB_TOKEN}
```
Then run the following command in the project directory
```bash
  docker compose up 
```
If you want to run in background, run the following command
```bash
  docker compose up -d
```

## How to access the Swagger UI
After running the application, access the following URL in your browser
```bash
  http://localhost:8080/swagger-ui.html
```
Or to access the YAML file
```bash
  http://localhost:8080/v3/api-docs.yaml
```


## Author

- Henrique Guimarães (https://github.com/HenriqueGuim)


## References

- How to create a good readme (https://readme.so)
- Source API for this project (https://developer.github.com/v3)

## Implementations
- External API
- Swagger
- GitHub