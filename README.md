# Background material

https://www.katacoda.com/courses/docker-orchestration/
http://collabnix.com/archives/1504
https://sreeninet.wordpress.com/2016/07/29/service-discovery-and-load-balancing-internals-in-docker-1-12/
http://blog.scottlogic.com/2016/08/30/docker-1-12-swarm-mode-round-robin.html
http://container-solutions.com/hail-new-docker-swarm/

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

# Kubernetes

[http://kubernetes.io/docs/user-guide/kubectl/kubectl_run/]()

[http://kubernetes.io/docs/user-guide/kubectl/kubectl_expose/]()

## Minikube

    minikube start
    minikube ip
    minikube status
    minikube dashboard
    minikube stop

	eval $(minikube docker-env)

## Google Cloud Platform

	#gcloud auth login
	#gcloud auth list
	#> Credentialed Accounts:
	#> - magnus.larsson@callistaenterprise.se ACTIVE	
	gcloud auth application-default login
	> osascript: OpenScripting.framework - scripting addition "/Library/ScriptingAdditions/Adobe Unit Types.osax" cannot be used with the current OS because it has no OSAXHandlers entry in its Info.plist.
	> Your browser has been opened to visit:
	>     https://accounts.google.com/o/oauth2/auth?redirect_uri=http%3A%2F%2Flocalhost%3A8085%2F&prompt=select_account&response_type=code&client_id=764086051850-6qr4p6gpi6hn506pt8ejuq83di341hur.apps.googleusercontent.com&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fcloud-platform&access_type=offline
	>
	> Credentials saved to file: [/Users/magnus/.config/gcloud/application_default_credentials.json]
	
	gcloud auth application-default print-access-token	
	export PROJECT_ID=k8s-labb-3
	gcloud config set project $PROJECT_ID

	gcloud config set compute/zone europe-west1-b
	
Var tvungen att pilla i web consolen för att få igång Computer Engine...

https://console.developers.google.com/apis/dashboard?project=k8s-labb-3&duration=PT1H
	
	gcloud container clusters create my-cluster
	> Creating cluster my-cluster...done.
	> Created [https://container.googleapis.com/v1/projects/k8s-labb-3/zones/europe-west1-b/clusters/my-cluster].
	> kubeconfig entry generated for my-cluster.
	> NAME        ZONE            MASTER_VERSION  MASTER_IP      MACHINE_TYPE   NODE_VERSION  NUM_NODES  STATUS
	> my-cluster  europe-west1-b  1.4.6           104.155.34.40  n1-standard-1  1.4.6         3          RUNNING	
	kubectl cluster-info
	kubectl get nodes
	
	gcloud container clusters list
	gcloud container clusters describe my-cluster
	
Tear down cluster:

	gcloud container clusters delete my-cluster
    

## Kubernetes med user space proxy

kubernetes user space vs system space proxy

http://stackoverflow.com/questions/36088224/what-does-userspace-mode-means-in-kube-proxys-proxy-mode
https://github.com/kubernetes/kubernetes/issues/1107
https://github.com/kubernetes/kubernetes/issues/3760
https://github.com/kubernetes/kubernetes/issues/19457 (cpu tabeller!!!)
https://github.com/kubernetes/kubernetes/issues/12682 (test + graphs!!!)
https://github.com/kubernetes/kubernetes/issues/13500 (proposal, new?)


## Kubernetes med system space proxy

## Deploy quotes-service and portal.js

Quotes: 

With kubectl run and expose:

Deployment:

	kubectl run quotes-v3 --image=magnuslarsson/quotes:3 --port=8080 
	kubectl expose deployment quotes-v3 --type=LoadBalancer --name quotes-service

RC:

	kubectl run quotes-v3 --image=magnuslarsson/quotes:3 --port=8080 --generator=run/v1
	kubectl expose rc quotes-v3 --type=LoadBalancer --name quotes-service


With kubectl create:

	cd quotes
	kubectl create -f k8s/quotes-controller-v3.yaml
	kubectl create -f k8s/quotes-service.yaml
	
Verify:

	kubectl get pods
	kubectl get replicationController
	kubectl get deployments
	kubectl get svc
	> NAME             CLUSTER-IP      EXTERNAL-IP      PORT(S)    AGE
	> kubernetes       10.127.240.1    <none>           443/TCP    4h
	> quotes-service   10.127.254.95   104.199.48.230   8080/TCP   3h

	QHOST=104.199.48.230
	kubectl scale rc quotes-v3 --replicas=3

	kubectl describe pod quotes | grep "IP:"
	> IP:		10.124.2.6
	> IP:		10.124.0.4
	> IP:		10.124.1.6

	curl $QHOST:8080/api/quote	
	curl 192.168.99.104:30080/api/quote	


Portal:

With kubectl run and expose as RC:

	kubectl run portal-v1 --image=magnuslarsson/portal.js:1 --port=80 --generator=run/v1
	kubectl expose rc portal-v1 --type=LoadBalancer --name portal

With kubectl create:

	cd portal.js
	kubectl create -f k8s/portal-controller-v1.yaml
	kubectl create -f k8s/portal-service.yaml
	
	curl 192.168.99.104:30090

Verify:

	kubectl get svc
	> portal           10.127.250.82    104.199.73.84   80/TCP     1m

	PHOST=104.199.73.84

	curl $PHOST

## K8s commands

	kubectl cluster-info
	Kubernetes-dashboard
	kubectl get nodes
	ubectl describe node gke-kubia-85f6-node-0rrx
	kubectl run kubia --image=luksa/kubia --port=8080 --generator=run/v1
	
	You can get a list of all the possible resource types by invoking kubectl get without specifying the type.
	
	
# Docker Swarm

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
      
Create cluster:      

	docker $(docker-machine config swarm-manager-1) swarm init --advertise-addr $(docker-machine ip swarm-manager-1)
	docker $(docker-machine config swarm-worker-1)  swarm join \
	  --token SWMTKN-... \
	  $(docker-machine ip swarm-manager-1):2377
	docker $(docker-machine config swarm-worker-2)  swarm join \
	  --token SWMTKN-... \
	  $(docker-machine ip swarm-manager-1):2377

Direct docker commands to a manager in the cluster:

	eval $(docker-machine env swarm-manager-1)    

Inspect the cluster;
	
	docker info
	docker node ls
	> ID                           HOSTNAME         STATUS  AVAILABILITY  MANAGER STATUS
	> 532rjhzhxyulwq6br67ptwbdf    swarm-worker-2   Ready   Active
	> 8l7ht3po49emchj5qax17rbt9    swarm-worker-1   Ready   Active
	> cmq9ynvzepelvuoxmrx8ey2pn *  swarm-manager-1  Ready   Active        Leader

	docker service  ls

Leave a swarm:

	docker $(docker-machine config swarm-worker-2)  swarm leave

MISC

	docker $(docker-machine config swarm-manager-1) swarm init --listen-addr $(docker-machine ip swarm-manager-1):2377
	docker $(docker-machine config swarm-worker-1) swarm join $(docker-machine ip swmaster):2377 --listen-addr $(docker-machine ip swnode):2377

Remove nodes:

    docker-machine rm swarm-manager-1
    docker-machine rm swarm-worker-1
    docker-machine rm swarm-worker-2

## Docker Swarm visualiser

	docker service create \
	  --name=viz \
	  --publish=8000:8080/tcp \
	  --constraint=node.role==manager \
	  --mount=type=bind,src=/var/run/docker.sock,dst=/var/run/docker.sock \
	  manomarks/visualizer

URL for Docker for Mac:

	http://localhost:8000

URL for Docker Swarm:

First find the IP address for one of the nodes in the cluster:

	docker-machine ip swarm-worker-1
	> 192.168.99.102

Open a web browser using the ip address:

	http://192.168.99.102:8000

## Show containers IP addresses

	docker $(docker-machine config swarm-manager-1) inspect -f '{{ .Config.Hostname }} {{ .NetworkSettings.Networks.ingress.IPAddress }}' $(docker $(docker-machine config swarm-manager-1) ps -q)
	docker $(docker-machine config swarm-worker-1) inspect -f '{{ .Config.Hostname }} {{ .NetworkSettings.Networks.ingress.IPAddress }}' $(docker $(docker-machine config swarm-worker-1) ps -q)
	docker $(docker-machine config swarm-worker-2) inspect -f '{{ .Config.Hostname }} {{ .NetworkSettings.Networks.ingress.IPAddress }}' $(docker $(docker-machine config swarm-worker-2) ps -q)

	docker inspect -f '{{ .Config.Hostname }} {{ .NetworkSettings.Networks.ingress.IPAddress }}' $(docker ps -q)

## Deploy quotes-service and portal.js	
quotes-service:

	# docker-compose bundle
	# docker deploy dockercomposev2

	docker network create --driver overlay my-network
	
	docker service create --replicas 1 --name quotes-service -p 8080:8080 --network my-network --update-delay 10s --update-parallelism 1 magnuslarsson/quotes:3
	docker service ls
	docker service ps quotes-service
	docker service inspect quotes-service	
	curl localhost:8080/api/quote
	curl $(docker-machine ip swarm-manager-1):8080/api/quote
	
	docker service scale quotes-service=3

portal.js:

	docker service create --replicas 1 --name portal -p 9080:80 --network my-network --update-delay 10s --update-parallelism 1 magnuslarsson/portal.js:1

	# curl localhost:30000/home

Web Browser URL:

	http://localhost:9080
	http://192.168.99.102:9080
	
**Note #1:** Scaling works fine using external curl command!!!

**Note #2:** Scaling doesn't works with portal.js (uses one and the same IP address)!!!
Kan appache komma förbi Virtual Extensible LAN (VXLAN)???

**Note #3:** Upgrade a bundle is done with the doker deploy command

### Teardown

	docker service rm quotes-service
	
# AWS ECS

[https://rossfairbanks.com/2015/03/31/hello-world-in-ec2-container-service.html]()

[https://aws.amazon.com/blogs/aws/new-aws-application-load-balancer/]()

# Setup a ECS cluster

See https://console.aws.amazon.com/ecs

Configure and create a cluster with one node:
(since we, for now, only use ECS tasks and not ECS services nor an ELB there is no use with > 1 node...)

Lista nycklar:

	aws kms list-keys

...ger iam-user not authorized fel..

    ecs-cli configure --region eu-west-1 --access-key $AWS_ACCESS_KEY_ID --secret-key $AWS_SECRET_ACCESS_KEY --cluster ecs-ml-cluster

    ecs-cli up --keypair aws-key1 --capability-iam --size 3 --instance-type t2.small

Remove the cluster, if required:

    ecs-cli down -f

## Open ports in the clusters security group

For now we need to open the following ports

* 8761 (Discovery server, Eureka)
* 8888 (Config Server)
* 9999 (Local OAuth Server)
* 8443 (API-gateway/Edge server)
    

      