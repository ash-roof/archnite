FROM node:lts-slim AS build
WORKDIR /src
RUN npm install -g @angular/cli@19.2.6

COPY package*.json ./
RUN npm ci

COPY . ./
RUN ng build --configuration=production

FROM nginx:alpine AS final
EXPOSE 80
COPY --from=build src/dist/archnite-client/browser  /usr/share/nginx/html
