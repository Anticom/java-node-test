# Running proper npm projects on Nashorn Engine

The project goal is to be able to use proper npm based projects with
dependencies etc. without any major drawbacks inside a Java Project.

The JavaScript portion should run on Nashorn / Rhino so we can pass data back
and forth conveniently.

## Building the docker image

```sh
$ docker build -t anticom/enginetest:latest .
```

## Running a container from the built image

In the current proof of concept no ports need to be exposed to check anything.

To create a container for tinkering arround that will clean up after itself,
run:
```sh
$ docker run --rm --name enginetest -it anticom/enginetest:latest
```
