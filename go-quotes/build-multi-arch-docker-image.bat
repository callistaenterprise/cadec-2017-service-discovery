
REM WINDOWS VERSION

echo %GOPATH%
REM C:\Users\magnus\Documents\go

set GOOS=
go build -o quotes-windows-amd64.exe

quotes-windows-amd64
REM Starting ML Go version of quote-service on port 8080
REM 2017/12/19 16:23:17 Starting ML HTTP service at 8080

curl http://localhost:8080/api/quote -UseBasicParsing

#
#!/usr/bin/env bash

# Test run:
#   go run *.go

docker build -f Dockerfile-windows-amd64 -t magnuslarsson/quotes:24-go-windows-amd64 .
docker push magnuslarsson/quotes:24-go-windows-amd64
docker run -d -p 8080:8080 --name quotes magnuslarsson/quotes:24-go-windows-amd64

REM LINUX VERSION

set GOOS=linux
go build -o quotes-linux-amd64
REM export GOOS=darwin

docker build -f Dockerfile-linux-amd64 -t magnuslarsson/quotes:24-go-linux-amd64 .
docker push magnuslarsson/quotes:24-go-linux-amd64
docker run -d -p 8080:8080 --name quotes magnuslarsson/quotes:24-go-linux-amd64
