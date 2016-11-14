# TODO

1. Gå över till docker for mac
1. Lägg på loggning i Portal!
1. In med Sleuth

# Setup

Using default docker-machine:

	eval $(docker-machine env)

Using Docker for Mac:

	eval $(docker-machine env -u)

# Netflix Eureka (no containers)

1. 1 quotes
2. 2 quotes
3. restart quotes

# docker-compose v1

Start up and monitor logs:

	cd docker-compose-v1
	docker-compose up -d && docker-compose logs -f

In another terminal:

	docker-compose exec quotes-service  wget -qO- localhost:9090/quote | jq
	docker-compose exec portal wget -qO- localhost:8080/home | jq

Web browser: 

* [http://docker.me:8080/home](http://docker.me:8080/home)

*TODO* Restart ger ingen ny IP adress???

Scale issues:

	docker-compose scale quotes-service=2
	docker-compose ps

	docker-compose exec --index=1 quotes-service  wget -qO- localhost:9090/quote | jq
	docker-compose exec --index=2 quotes-service  wget -qO- localhost:9090/quote | jq

	docker-compose exec portal wget -qO- quotes-service:9090/quote | jq
	

Wiew host names in portal:	

	docker-compose exec portal cat /etc/hosts

Wiew IP address in quotes:	

	docker-compose exec --index=1 quotes-service hostname -i	docker-compose exec --index=2 quotes-service hostname -i
	
Shudown
	
	docker-comopse down
			
# docker-compose v2

Start up and monitor logs:

	cd docker-compose-v2
	# docker-compose up -d && docker-compose logs -f
	docker-compose up

In another terminal:

	# docker-compose exec quotes-service  wget -qO- localhost:9090/quote | jq
	docker-compose exec portal wget -qO- quotes-service:9090/quote | jq
	docker-compose exec portal wget -qO- localhost:8080/home

## Scale issues:

	docker-compose scale quotes-service=3

**NOTE**: Varför växlar inte anropen???

	docker-compose exec portal wget -qO- quotes-service:9090/quote | jq

Portal väljer alltid #1

	docker run -it --rm --network=dockercomposev2_default centos curl quotes-service:9090/quote | jq

Docker run väljer alltid #2 eller #3

Kill instance #1:

    docker ps | grep quotes-service_1
    docker kill 074b777e0e4f
   
Verify: 
    
	docker-compose ps
	              Name                            Command                State             Ports
	-----------------------------------------------------------------------------------------------------
	dockercomposev2_portal_1           java -Djava.security.egd=f ...   Up         0.0.0.0:8080->8080/tcp
	dockercomposev2_quotes-service_1   java -Djava.security.egd=f ...   Exit 137
	dockercomposev2_quotes-service_2   java -Djava.security.egd=f ...   Up
	dockercomposev2_quotes-service_3   java -Djava.security.egd=f ...   Up


Test:

		docker-compose exec portal wget -qO- localhost:8080/home

**NOTE**: FUNKAR, VARFÖR??!?!?!!??!!! Jämför med portal i DMDP

**TODO**: httpd baserad Web SPA istället!!!

## Restart issues:

	docker-compose scale quotes-service=0
	docker-compose scale quotes-service=1

...blir ingen ny ip adress dvs, får köra med skala upp till tre och sedan ta bort den första..

# Kubernetes med user space proxy

kubernetes user space vs system space proxy

http://stackoverflow.com/questions/36088224/what-does-userspace-mode-means-in-kube-proxys-proxy-mode
https://github.com/kubernetes/kubernetes/issues/3760
https://github.com/kubernetes/kubernetes/issues/19457
https://github.com/kubernetes/kubernetes/issues/12682
https://github.com/kubernetes/kubernetes/issues/13500



# Kubernetes med system space proxy