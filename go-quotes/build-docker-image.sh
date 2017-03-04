#!/usr/bin/env bash

# Test run:
#   go run *.go

export GOOS=linux
go build -o quotes-linux-amd64
export GOOS=darwin

docker build -t magnuslarsson/quotes:go-22 .
# docker push magnuslarsson/quotes:go-22
# docker run --rm -p 8080:8080 magnuslarsson/quotes:go-22
