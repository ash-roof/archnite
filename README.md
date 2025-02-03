# archnite

archnite is a full stack web app to help users of both Arch Linux and Arch-based distros quickly generate package install commands
for their favorite packages from the Arch official repos as well as the AUR using pacman or your preferred AUR helper.

## Table of Contents

1. [About The Project](#about-the-project)
    - [Motivation & Inspiration](#motivation--inspiration)
    - [Architecture & Tech Stack](#architecture--tech-stack)
        - [Why Go & Spring Boot?](#why-go--spring-boot)
2. [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Running Locally](#running-locally)
3. [Environment Variables](#environment-variables)
4. [Contributing](#contributing)
5. [Roadmap](#roadmap)

## About The Project

### Motivation & Inspiration

I made archnite due to a couple of reasons. Mainly, it was to solve a small issue I had when setting up a
new Arch Linux system; I always found it tedious to look up package names and typing out install commands.

I wanted to make a tool similar to [Ninite](https://ninite.com/) (where I got the inspiration for the name, yes boring I know) where I could use
a autocomplete/incremental search box similar to what a search engine has, and/or pick from a list of popular packages, select my
desired packages and get an install command I could paste into my terminal.

I also started the project with the purpose of it being a learning experience so I could try out some technologies I've always wanted to experiment with.

### Architecture & Tech Stack

archnite was built using the following technologies:

- Spring Boot
  - Used to create the JSON API responsible for querying the database for package metadata.
- Go
  - Used to build the DB Populator standalone binary which automatically fetches updated package metadata, serializes it and populates
  the necessary PostgreSQL tables.
- PostgreSQL
  - Used to store package metadata and apply a trigram index to package names for fast package lookup, allowing for incremental search in our client.
- Angular
- Docker & Docker Compose
  - Fully containerised the app with a provided docker-compose.yml file to easily spin up all project services.

#### Why Go & Spring Boot?

To be honest, this project would have been perfectly fine and probably better off with just Go or Spring Boot, but as I said this is
mainly a learning-based endeavor and these are the technologies I wanted to try out and get experience with (especially Go).

That being said, I do like the stack I chose and I feel each technology fits its use case well and gives a good foundation for expanding the app.

## Getting Started

### Prerequisites

### Running Locally

## Environment Variables

## Contributing

## Roadmap
