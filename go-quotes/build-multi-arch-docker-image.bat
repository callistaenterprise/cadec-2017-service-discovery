
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


> curl http://192.168.1.224:8080/api/quote -UseBasicParsing
>>
StatusCode        : 200
StatusDescription : OK
Content           : {"hardwareArchitecture":"amd64","operatingSystem":"windows","ipAddress":"4d131b511ab9/fe80::9846:5b
                    e3:c0bb:2d91%Ethernet172.24.224.172","quote":"In Go, the code does exactly what it says on the page
                    ."...
RawContent        : HTTP/1.1 200 OK
                    Content-Length: 217
                    Content-Type: application/json
                    Date: Wed, 20 Dec 2017 12:07:09 GMT

                    {"hardwareArchitecture":"amd64","operatingSystem":"windows","ipAddress":"4d131b511ab9/fe80:...
Forms             :
Headers           : {[Content-Length, 217], [Content-Type, application/json], [Date, Wed, 20 Dec 2017 12:07:09 GMT]}
Images            : {}
InputFields       : {}
Links             : {}
ParsedHtml        :
RawContentLength  : 217

REM LINUX VERSION

set GOOS=linux
go build -o quotes-linux-amd64
REM export GOOS=darwin

docker build -f Dockerfile-linux-amd64 -t magnuslarsson/quotes:24-go-linux-amd64 .
docker push magnuslarsson/quotes:24-go-linux-amd64
docker run -d -p 8080:8080 --name quotes magnuslarsson/quotes:24-go-linux-amd64



> curl http://localhost:8080/api/quote -UseBasicParsing
>>

StatusCode        : 200
StatusDescription : OK
Content           : {"hardwareArchitecture":"amd64","operatingSystem":"linux","ipAddress":"0c4e0824f479/172.17.0.2","qu
                    ote":"I like a lot of the design decisions they made in the [Go] language. Basically, I like all of
                     t...
RawContent        : HTTP/1.1 200 OK
                    Content-Length: 222
                    Content-Type: application/json
                    Date: Wed, 20 Dec 2017 11:44:37 GMT

                    {"hardwareArchitecture":"amd64","operatingSystem":"linux","ipAddress":"0c4e0824f479/172.17....
Forms             :
Headers           : {[Content-Length, 222], [Content-Type, application/json], [Date, Wed, 20 Dec 2017 11:44:37 GMT]}
Images            : {}
InputFields       : {}
Links             : {}
ParsedHtml        :
RawContentLength  : 222


REM MULTI-ARCH VERSION

manifest-tool-windows-amd64.exe  --docker-cfg "\Users\magnus\.docker\config.json" push from-spec manifest-quotes-multiarch.yml