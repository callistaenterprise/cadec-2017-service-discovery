# TODO

1. Gå över till docker for mac
1. Lägg på loggning i Portal!

# Setup

	eval $(docker-machine env)

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

Scale issues:

	docker-compose scale quotes-service=2
	docker-compose ps

	docker-compose exec --index=1 quotes-service  wget -qO- localhost:9090/quote | jq
	docker-compose exec --index=2 quotes-service  wget -qO- localhost:9090/quote | jq

	docker-compose exec portal wget -qO- quotes-service:9090/quote | jq
	

Wiew host names in portal:	

	docker-compose exec portal cat /etc/hosts

Wiew IP address in quotes:	

	docker-compose exec index=1 quotes-service hostname -i	docker-compose exec index=2 quotes-service hostname -i
		
# docker-compose v2

Restart issues:
