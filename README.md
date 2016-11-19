# TODO


1. Miljöer:
	1. Docker for Mac - Swarm Mode
	1. Minikube
	1. Docker Swarn (locally/AWS)
	1. No Docker (Eureka/Ribbon)
	1. K8S (GCE)
	1  AWS ECS/ALB
1. Config server för enkalre konfiguration alt rest anrop
	1. Vill man påverka startup tiden så är det nästan bara config repo som gäller?
	2. Env var och om deploy? (nja, snyggar emed config repo) 
	
## DONE

1. Gå över till docker for mac
1. Lägg på loggning i Portal!
1. (In med Spring Cloud Sleuth
1. Lägg på ip-adress i svaret!
1. Skippa Docker DAP bundles

# Setup

Using default docker-machine:

	eval $(docker-machine env)

Using Docker for Mac:

	eval $(docker-machine env -u)

# quotes-service

Build Docker image:

	./gradlew clean build
	#eval "$(docker-machine env default)"
	docker build -t magnuslarsson/quotes .

Tag and push Docker image:
	
	version=3
	docker tag magnuslarsson/quotes magnuslarsson/quotes:${version}
	docker push magnuslarsson/quotes:${version}


# portal.js

Cleanup:

    rm -r bower_components
    rm -r node_modules

Install:

	npm install && bower install

Start:

	node_modules/gulp/bin/gulp.js serve

Build (into `./dist`):

	node_modules/gulp/bin/gulp.js build

Create Docker image, tag and push:

	docker build -t magnuslarsson/portal.js .
	docker tag magnuslarsson/portal.js magnuslarsson/portal.js:1
	docker push magnuslarsson/portal.js:1
	
**NOTE: **Reverse proxy config is placed in `conf/browsersync.conf.js`.

# Netflix Eureka (no containers)

1. 1 quotes
2. 2 quotes
3. restart quotes

Test quotes locally:

	cd quotes
	./gradlew clean build
	java -jar build/libs/*.jar
	java -jar build/libs/*.jar --server.port=8081
	
	url -s  localhost:8080/api/quote | jq .
	url -s  localhost:8081/api/quote | jq .
	

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

# Docker Swarm

## Docker Swarm visualiser

	docker service create \
	  --name=viz \
	  --publish=8000:8080/tcp \
	  --constraint=node.role==manager \
	  --mount=type=bind,src=/var/run/docker.sock,dst=/var/run/docker.sock \
	  manomarks/visualizer

## Singel node swarm

Only works on experimental/beta docker v1.12...

	docker swarm init
	docker node ls
	docker node inspect moby
	
### Access Docker Enging in Docker for Mac

	screen ~/Library/Containers/com.docker.docker/Data/com.docker.driver.amd64-linux/tty
	
To exit (kill the screen): 

	“Ctrl-A” and “K”
		
## Swarm Cluster

Create nodes:

    docker-machine create \
      --driver virtualbox \
      --virtualbox-cpu-count 2 \
      --virtualbox-memory 2048 \
      --virtualbox-disk-size 20000 \
      swarm-manager-1

    docker-machine create \
      --driver virtualbox \
      --virtualbox-cpu-count 2 \
      --virtualbox-memory 2048 \
      --virtualbox-disk-size 20000 \
      swarm-worker-1

    docker-machine create \
      --driver virtualbox \
      --virtualbox-cpu-count 2 \
      --virtualbox-memory 2048 \
      --virtualbox-disk-size 20000 \
      swarm-worker-2
      
Remove nodes:

    docker-machine rm swarm-manager-1
    docker-machine rm swarm-worker-1
    docker-machine rm swarm-worker-2

## Deploy quotes-service and portal.js	
quotes-service:

	# docker-compose bundle
	# docker deploy dockercomposev2

	docker network create --driver overlay my-network
	
	docker service create --replicas 1 --name quotes-service -p 8080:8080 --network my-network --update-delay 10s --update-parallelism 1 magnuslarsson/quotes:3
	docker service ls
	docker service ps 79hzv4y2x7tu
	docker service inspect 79hzv4y2x7tu
	
	curl localhost:8080/api/quote
	
	docker service scale quotes-service=3

portal.js:

	docker service create --replicas 1 --name portal -p 9080:80 --network my-network --update-delay 10s --update-parallelism 1 magnuslarsson/portal.js

	# curl localhost:30000/home
	
**Note #1:** Scaling works fine using exxternal curl command!!!

**Note #2:** Scaling doesn't works with portal.js (uses one and the same IP address)!!!

**Note #3:** Upgrade a bundle is done with the doker deploy command

### Teardown

	docker service rm quotes-service
	

      