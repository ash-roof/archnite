<div align="center">
  <a href="https://github.com/OmarAshraf-02/archnite">
    <img src="images/logo.png" alt="Logo" width="300" height="80">
  </a>
</div>
<br/>

Archnite is public API for Arch Linux and AUR packages with an accompanying
client for browsing packages and generating long package install commands.

**Archnite is now live!** Check out the client [here](https://archnite.omarashraf.dev).

The API is also hosted [here](https://api.archnite.omarashraf.dev)
and is completely free for public use. Check out the [docs](./archnite-spring/docs/api-docs.md).

## Table of Contents

1. [About The Project](#about-the-project)
    - [Motivation & Inspiration](#motivation--inspiration)
    - [Architecture & Tech Stack](#architecture--tech-stack)
        - [Why Go & Spring Boot?](#why-go--spring-boot)
2. [Getting Started](#getting-started)
    - [Environment Variables](#environment-variables)
    - [Prerequisites](#prerequisites)
    - [Running The Project](#running-the-project)
        - [Docker Compose](#1-docker-compose)
        - [Docker](#2-docker)
        - [Manual Setup](#3-manual-setup)
3. [Contributing](#contributing)

## About The Project

### Motivation & Inspiration

I made Archnite due to a couple of reasons. Mainly, it was to
solve a small issue I had when setting up a new Arch Linux system;
I always found it tedious to look up package names and type out install commands.

I wanted to make a tool where I could use
a autocomplete/incremental search box similar to what a
search engine has, select my desired packages and get an
install command I could paste into my terminal.

I also wanted to make a unified API for Arch official and AUR packages that is
relatively fast (or at least faster than the Arch official packages JSON API,
which I found pretty slow in my experience) that anyone can use.

Side note: I got the inspiration for the name from [Ninite](https://ninite.com),
because I thought this tool would be similar to it at first,
before deciding to switch it to a search tool.

### Architecture & Tech Stack

Archnite was built using the following technologies:

- Spring Boot
  - Used to create the JSON API responsible for querying the
      database for package metadata.
- Go
  - Used to build the DB Populator standalone binary which automatically fetches
      updated package metadata, serializes it and populates the necessary
      PostgreSQL tables.
- PostgreSQL
  - Used to store package metadata and apply a trigram index to package names for
      fast package lookup, allowing for incremental search in our client.
- Angular
- Docker & Docker Compose
  - Fully containerized the app with a provided docker-compose.yml file
      to easily spin up all services.

#### Why Go & Spring Boot?

To be honest, this project would have been perfectly fine with just Go or
Spring Boot, but these are the technologies I wanted to get experience with.

That being said, I do like the stack I chose and I feel each technology fits
its use case well and gives a good foundation for expanding the app.

## Getting Started

Clone the project:

```bash
git clone https://github.com/OmarAshraf-02/archnite.git
cd archnite
```

**There are three ways you can run this project:**

1. The easiest way is to run the entire service stack with Docker Compose using
   the provided [docker-compose.yml](./docker-compose.yml) file. This will spin
   up an instance of PostgreSQL, setup the app's database used by the Spring Boot
   and Go projects and start containers running the Go, Spring Boot & Angular projects.

2. Run the individual project containers using Docker and each project's Dockerfile.
   This method requires the manual setup of a PostgreSQL instance on
   your machine and the creation of an empty database within this instance.

3. Run the individual projects locally using each project's needed dependencies
   installed on your machine. This method also requires a running
   PostgreSQL instance with an empty database.

For the rest of the [Getting Started](#getting-started) section,
these numbers will be used to reference each method
(1 = Docker Compose, 2 = Docker Individual Containers, 3 = Local Dependencies).

### Environment Variables

**IMPORTANT**: Before continuing, please setup your .env file containing
environment variables used to create/connect to the PostgreSQL database
needed by archnite. A [.env.example](./.env.example) file
has been created for this purpose:

1. Create a copy of the `.env.example` file named `.env`
   in the root of the archnite repo.

    You can use the following command for this on macOS, Linux or WSL
    (or any Unix-like environment/shell):

    ```bash
    cp .env.example .env
    ```

2. (Optional) You can edit the variables in `.env` depending on your database setup.

### Prerequisites

**As mentioned above, methods 2 and 3 require a local PostgreSQL instance
running on your machine with an empty database created. I recommend
spinning up a PostgreSQL Docker Container on your local machine
if you choose any of these, using the following command:**

```bash
docker run --name archnite-postgres \
  --env-file path/to/archnite/root/.env \
  -p 5432:5432 \
  -d postgres
```

Replace the first `5432` with any other port if you don't want
to use `5432` on your local machine (_just make sure to reflect
this change on `POSTGRES_PORT` in `.env`_).

This will create a PostgreSQL container named `archnite-postgres`
which contains an empty database, created using the environment
variables specified in the `.env` file.

You can now start and stop this container using:

```bash
docker start archnite-postgres
```

and

```bash
docker stop archnite-postgres
```

---

These are the dependencies you'll need to run the project based on
your chosen method (excluding PostgreSQL if using 2 or 3):

1. Docker & Docker Compose
2. Docker
3. For:
    - archnite-spring: JDK 21
    - archnite-db-populator: Go 1.23
    - archnite-client: Node.js 22 & The Angular CLI

### Running The Project

#### 1. Docker Compose

Run the project stack using (make sure you're in the project root):

```bash
docker compose up --build
```

You can optionally add the `-d` flag to run in detached mode.
You can now find:

- The Spring Boot project (archnite-spring) running on
  localhost:`SPRING_PORT` from your `.env`
- The Angular client (archnite-client) running on
  localhost:`CLIENT_PORT` from your `.env`
- The Go project (archnite-db-populator) binary running

Stop and remove the containers using:

```bash
docker compose down
```

#### 2. Docker

Once you have your `.env` file setup correctly and your PostgreSQL instance running:

##### For the Go project (archnite-db-populator)

```bash
cd archnite-db-populator
docker build -t archnite-db-populator .
docker run --env-file ../.env --network="host" archnite-db-populator:latest
```

##### For the Spring Boot project (archnite-spring)

```bash
cd archnite-spring
docker build -t archnite-spring .
docker run --env-file ../.env --network="host" archnite-spring:latest
```

You'll find the Spring Boot project running on localhost:`SPRING_PORT`

##### For the Angular project (archnite-client)

```bash
cd archnite-client
docker build -t archnite-client .
docker run --env-file ../.env --network="host" archnite-client:latest
```

You'll find the Angular project running on localhost:`CLIENT_PORT`

#### 3. Manual Setup

Once you have your `.env` file setup correctly and your PostgreSQL instance running:

##### Go project (archnite-db-populator)

First download the required dependencies:

```bash
cd archnite-db-populator
go mod tidy
```

Then run:

```bash
go build -o bin/populator ./cmd/populator
./bin/populator
```

##### Spring Boot project (archnite-spring)

To download required dependencies and build the project using
the included Maven wrapper (or just use `mvn` if you have it installed
locally):

```bash
cd archnite-spring
./mvnw clean install
```

Then

```bash
./mvnw spring-boot:run
```

Or just run the JAR File:

```bash
java -jar target/archnite-1.0.0.jar
```

For unit tests:

```bash
./mvnw test
```

##### Angular project (archnite-client)

Make sure you have the following installed:

- Node.js (LTS 22 Recommended)
- Angular CLI

To install the Angular CLI globally:

```bash
npm install -g @angular/cli
```

Then install dependencies:

```bash
cd archnite-client
npm install
```

Finally, run the development server with:

```bash
ng serve # you can also use npm start
```

The app will be available at `localhost:4200`

## Contributing

This is my first open-source project, so I'll try to keep things simple
and flexible for the time being. Feel free to open issues & discussions,
suggest improvements, features or bug fixes. Any contribution
is welcome and I'd love to learn new things about any part of this stack.
