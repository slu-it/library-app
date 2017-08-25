# LibraryUi

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 1.3.1.

## Install all dependencies

Run `npm install`

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `-prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).
Before running the tests make sure you are serving the app via `ng serve`.

## Build and push docker image

Run `docker build -t caaqe/library-ui .` in root directory of project to build the
docker image for the library-ui (this uses NGINX as web server internally).

Run `docker push caaqe/library-ui` to push docker image to docker hub. Make sure you login
to docker hub with a user having write access rights to _caaqe_ organization.

## Run library ui via docker image

To start the library ui via docker image just use the following commands:

`docker pull caaqe/library-ui`

`docker run -d -p 9090:80 caaqe/library-ui`

Now you should be able to access the library ui using [localhost:9090](http://localhost:9090)

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).
