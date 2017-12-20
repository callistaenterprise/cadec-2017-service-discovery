
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

./manifest-tool-darwin-amd64.dms --username=magnuslarsson --password=xxx push from-spec manifest-quotes-multiarch.yml

docker run mplatform/mquery magnuslarsson/quotes:24-go
Image: magnuslarsson/quotes:24-go
 * Manifest List: Yes
 * Supported platforms:
   - linux/amd64
   - windows/amd64:10.0.14393.1944
   
Docker for Mac
==============

docker run -d -p 8080:8080 --name quotes magnuslarsson/quotes:24-go
Unable to find image 'magnuslarsson/quotes:24-go' locally
24-go: Pulling from magnuslarsson/quotes
6cbedf1a098b: Pull complete
Digest: sha256:7f05a93194d9e790ce2d4bce6576abe32e0ce560844476bf2373163af18d0d4a
Status: Downloaded newer image for magnuslarsson/quotes:24-go
655a29c9fd094e3e180b60fec78b7c9bf0d2dcc0e52d18fa86ae6660dfdbef

curl localhost:8080/api/quote
{"hardwareArchitecture":"amd64","operatingSystem":"linux","ipAddress":"655a29c9fd09/172.17.0.2","quote":"In Go, the code does exactly what it says on the page.","language":"EN"}

Docker for Windows - Windows Containers
=======================================

>docker run -d -p 8080:8080 --name quotes magnuslarsson/quotes:24-go
Unable to find image 'magnuslarsson/quotes:24-go' locally
24-go: Pulling from magnuslarsson/quotes
Digest: sha256:7f05a93194d9e790ce2d4bce6576abe32e0ce560844476bf2373163af18d0d4a
Status: Downloaded newer image for magnuslarsson/quotes:24-go
6271c339b5a8d847eb3f4cfb55006900f81ae161b8654ee28e6971db883773c2

~$ curl 192.168.1.224:8080/api/quote
{"hardwareArchitecture":"amd64","operatingSystem":"windows","ipAddress":"6271c339b5a8/fe80::c8b:838f:ab07:6790%Ethernet172.24.237.142","quote":"In Go, the code does exactly what it says on the page.","language":"EN"}



Docker for Windows - Linux Containers
=====================================

docker run -d -p 8080:8080 --name quotes magnuslarsson/quotes:24-go
Unable to find image 'magnuslarsson/quotes:24-go' locally
24-go: Pulling from magnuslarsson/quotes
Digest: sha256:7f05a93194d9e790ce2d4bce6576abe32e0ce560844476bf2373163af18d0d4a
Status: Downloaded newer image for magnuslarsson/quotes:24-go
0fe5514a7d70e04581bf37e31109a779ddbd3623a91ba801a0a8f018242da31e

$ curl localhost:8080/api/quote
{"hardwareArchitecture":"amd64","operatingSystem":"linux","ipAddress":"0fe5514a7d70/172.17.0.2","quote":"I like a lot of the design decisions they made in the [Go] language. Basically, I like all of them.","language":"EN"}

