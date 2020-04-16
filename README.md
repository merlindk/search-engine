# Docker

Docker related files:
* Dockerfiles
* docker-compose files

## Postgres
These instructions will get you a postgres database with all necessary tables to develop in you local

### Prerequisities
* Docker Installed: 
    * with Hyper-V: [Download](https://store.docker.com/editions/community/docker-ce-desktop-windows)
    * without Hyper-V: [Download](https://www.docker.com/products/docker-toolbox)

### For Windows:
* Install docker for windows (Requires Windows Pro or higher) https://hub.docker.com/editions/community/docker-ce-desktop-windows


### Build Image
Get inside the postgres repo directory for your operating system and run
\search-engine\postgres (linux or windows)

```
docker image build -f winDockerfile -t apc/postgres:latest .
```

Verify the image was created successfully:
```
docker images
```

### Create and Run the container
*For Linux:
```
 docker run --name apcpostgres  -p 5432:5432 -e POSTGRES_PASSWORD=postgres apc/postgres:latest -d postgres
```
*For Windows:
```
 docker run --name apcpostgres -d -p 5432:5432 -e POSTGRES_PASSWORD=postgres apc/postgres:latest
```
