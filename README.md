# Background material

https://www.katacoda.com/courses/docker-orchestration/
http://collabnix.com/archives/1504
https://sreeninet.wordpress.com/2016/07/29/service-discovery-and-load-balancing-internals-in-docker-1-12/
http://blog.scottlogic.com/2016/08/30/docker-1-12-swarm-mode-round-robin.html
http://container-solutions.com/hail-new-docker-swarm/

# Notes

## JVM DNS settings...

In docker containers: /usr/lib/jvm/jre/lib/security/java.security

## Docker for Mac strul

docker-compose up ger ibland följande fel (ej down innan)

	Unexpected API error for dockercomposev2_portal_1 (HTTP code 500)
	Response body:
	dial unix /Users/magnus/Library/Containers/com.docker.docker/Data/*00000003.00000948: connect: connection refused
	
	Unexpected API error for dockercomposev2_quotes-service_3 (HTTP code 500)
	Response body:
	dial unix /Users/magnus/Library/Containers/com.docker.docker/Data/*00000003.00000948: connect: connection refused

**UPPGRADERA TILL 1.12.5!!!**

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

    docker-machine create \
      --driver virtualbox \
      --virtualbox-cpu-count 2 \
      --virtualbox-memory 4096 \
      --virtualbox-disk-size 20000 \
      local
      
	eval $(docker-machine env local)

Using Docker for Mac (NOT STABLE ENOUGH???):

	eval $(docker-machine env -u)

# quotes-service

Build Docker image:

	cd quotes
	./gradlew clean build
	docker build -t magnuslarsson/quotes .

Tag and push Docker image:
	
	version=16
	docker tag magnuslarsson/quotes magnuslarsson/quotes:${version}
	docker push magnuslarsson/quotes:${version}


# portal

## Build js part:
	
	cd portal

Cleanup, if required:

    rm -r bower_components
    rm -r node_modules

Install dependencies:

	npm install && bower install

Build the webapp under the src/main/resources/static - folder:

	node_modules/gulp/bin/gulp.js clean
	node_modules/gulp/bin/gulp.js build
	
**NOTE:** Ensure that the static folder is added to .gitignore so that these files not end up in th git-repo

**Potential improvements:**

1. Currently all steps above are done manually, can they be automated by extending the gradle script?

## Build Docker image:

	./gradlew clean build
	docker build -t magnuslarsson/portal .
	
Tag and push Docker image:
	
	version=17
	docker tag magnuslarsson/portal magnuslarsson/portal:${version}
	docker push magnuslarsson/portal:${version}

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
	
	curl -s  localhost:8080/api/quote | jq .
	curl -s  localhost:8081/api/quote | jq .
	
	curl -s localhost:9090/quote | jq .
	> {
	>   "ipAddress": "192.168.1.198",
	>   "quote": "Champagne should be cold, dry and free",
	>   "language": "en"
	> }	
	
	
See [EurekaInstanceConfigBean](https://github.com/spring-cloud/spring-cloud-netflix/blob/master/spring-cloud-netflix-eureka-client/src/main/java/org/springframework/cloud/netflix/eureka/EurekaInstanceConfigBean.java) and [EurekaClientConfigBean](https://github.com/spring-cloud/spring-cloud-netflix/blob/master/spring-cloud-netflix-eureka-client/src/main/java/org/springframework/cloud/netflix/eureka/EurekaClientConfigBean.java) for more details of the configurable options.

	
	

# docker-compose v1

Start up and monitor logs:

	cd docker-compose-v1
	docker-compose up -d && docker-compose logs -f

In another terminal:

	# docker-compose exec quotes-service  wget -qO- localhost:9090/quote | jq
	# docker-compose exec portal wget -qO- localhost:8080/home | jq

	docker-compose-v2$ docker-compose exec portal wget -qO- localhost:9090/quote | jq
	docker-compose exec portal wget -qO- quotes-service:8080/api/quote | jq

Web browser: 

* [http://docker.me:8080/home](http://docker.me:8080/home)

*TODO* Restart ger ingen ny IP adress???

Scale issues:

	docker-compose scale quotes-service=2
	docker-compose ps


	docker-compose exec --index=1 quotes-service  wget -qO- localhost:8080/api/quote | jq	docker-compose exec --index=2 quotes-service  wget -qO- localhost:8080/api/quote | jq	
	docker-compose exec portal wget -qO- quotes-service:9090/quote | jq


	

Wiew host names in portal:	

	docker-compose exec portal cat /etc/hosts

Wiew IP address in quotes:	

	docker-compose exec --index=1 quotes-service hostname -i	docker-compose exec --index=2 quotes-service hostname -i
	
Shudown
	
	docker-comopse down
			
# docker-compose v2

If 

	Unexpected API error for dockercomposev2_portal_1 (HTTP code 500)
	Response body:
	dial unix /Users/magnus/Library/Containers/com.docker.docker/Data/*00000003.00000948: connect: connection refused

Then

	docker-compose down
	
Or

	Restart Docker for Mac...
	
Start up and monitor logs:

	cd docker-compose-v2
	# docker-compose up -d && docker-compose logs -f
	docker-compose up

In another terminal:

	docker-compose exec quotes-service  wget -qO- localhost:8080/api/quote | jq .
	docker-compose exec portal wget -qO- quotes-service:8080/api/quote | jq .
	docker-compose exec portal wget -qO- localhost:9090/api/quote | jq 

	docker run -it --rm --network dockercomposev2_default centos curl quotes-service:8080/api/quote | jq .
	curl -s localhost:9090/quote | jq .

	docker-compose exec portal nslookup quotes-service
	> Server:    127.0.0.11
	> Address 1: 127.0.0.11
	>
	> Name:      quotes-service
	> Address 1: 172.19.0.3 dockercomposev2_quotes-service_1.dockercomposev2_default

## Scale issues:

	docker-compose up
	docker-compose scale quotes-service=3
	docker-compose exec portal nslookup quotes-service

	curl -s localhost:9090/quote | jq .

	docker run -it --rm --network dockercomposev2_default centos curl quotes-service:8080/api/quote | jq .


Verify:

	curl -s localhost:9090/quote | jq .

Scale:

	docker-compose scale quotes-service=3
	docker-compose exec portal nslookup quotes-service
	docker inspect -f '{{ .Config.Hostname }} {{ .NetworkSettings.Networks.dockercomposev2_default.IPAddress }} {{ .Config.Image }}' $(docker ps -q --filter "name=quotes")

Try out:

>>> 	see PPT!!!



Kill selected instance:

	curl -s docker.me:9090/9090/quoteWithoutRetries | jq .
	docker rm -f

Call should hang:

	docker-compose exec portal nslookup quotes-service
	curl -s docker.me:9090/quote | jq .


>**NOTE**: Varför växlar inte anropen???
>
>	  docker-compose exec portal wget -qO- quotes-service:8080/api/quote | jq
>
>Portal väljer alltid samma (ofta den första)
>
>	  docker run -it --rm --network=dockercomposev2_default centos curl quotes-service:8080/api/quote | jq
>
>"Docker run" mot centos väljer olika
>
>Gissningsvis är någon inställt på att alltid cacha DNS entries i docker imagen ofayau/ejre?

   
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

## Google Cloud Platform - Compute Engine

Source: [http://blog.kubernetes.io/2016/07/autoscaling-in-kubernetes.html?m=1](http://blog.kubernetes.io/2016/07/autoscaling-in-kubernetes.html?m=1)

Prereq: 

	brew install gnu-sed
	
Env vars:	

	export KUBE_GCE_ZONE=europe-west1-b
	export NODE_SIZE=n1-standard-1
	export NUM_NODES=1
	export KUBE_ENABLE_CLUSTER_AUTOSCALER=true
	export KUBE_AUTOSCALER_MIN_NODES=1
	export KUBE_AUTOSCALER_MAX_NODES=5

Download and install K8S locally:

	cd /Users/magnus/Documents/projects/cadec-2017/service-discovery	curl -sS https://get.k8s.io | bash

Start cluster:

	cd kubernetes
	./cluster/kube-up.sh
	
	Wrote config for k8s-labb-3_kubernetes to /Users/magnus/.kube/config
	
	Kubernetes cluster is running.  The master is running at:
	  https://130.211.139.68
	
	The user name and password to use is located in /Users/magnus/.kube/config.
	
	Your active configuration is: [default]
	
	Project: k8s-labb-3
	Zone: us-central1-b
		
	Kubernetes master is running at https://130.211.139.68
	GLBCDefaultBackend is running at https://130.211.139.68/api/v1/proxy/namespaces/kube-system/services/default-http-backend
	Heapster is running at https://130.211.139.68/api/v1/proxy/namespaces/kube-system/services/heapster
	KubeDNS is running at https://130.211.139.68/api/v1/proxy/namespaces/kube-system/services/kube-dns
	kubernetes-dashboard is running at https://130.211.139.68/api/v1/proxy/namespaces/kube-system/services/kubernetes-dashboard
	Grafana is running at https://130.211.139.68/api/v1/proxy/namespaces/kube-system/services/monitoring-grafana
	InfluxDB is running at https://130.211.139.68/api/v1/proxy/namespaces/kube-system/services/monitoring-influxdb
	
	To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.

Admin password:

	grep password /Users/magnus/.kube/config

Verify cluster nodes:
	
	kubectl get nodes

Get externa IP addresses:

	kubectl get nodes \
	  -o jsonpath='{.items[*].status.addresses[?(@.type=="ExternalIP")].address}'

Deploy:

	kubectl run quotes --image=magnuslarsson/quotes:16 --port=8080 
	kubectl expose deployment quotes --type=LoadBalancer --name quotes-service

	kubectl run portal --image=magnuslarsson/portal:17 --port=9090 
	kubectl expose deployment portal --type=LoadBalancer --name portal-service

	kubectl get deployment
	kubectl get pods
	kubectl get svc quotes-service
	kubectl get svc portal-service


	#kubectl run php-apache \
	# --image=gcr.io/google_containers/hpa-example \
	# --requests=cpu=500m,memory=500M --expose --port=80


Test deployment (using EXTERNAL-IP from get svc)

	MYHOST=130.211.85.2
	curl -s http://$MYHOST:8080/api/quote | jq .

	#kubectl run -i --tty service-test --image=busybox /bin/sh
	# # wget -q -O- http://php-apache.default.svc.cluster.local
	# # exit
	
	#kubectl attach service-test-427663219-tl83f -c service-test -i -t
	
Horizontal autoscalar:

	kubectl autoscale deployment quotes --cpu-percent=50 --min=1 --max=10

	# kubectl autoscale deployment php-apache --cpu-percent=50 --min=1 --max=10

	kubectl get hpa
		
Put some load, start two:		

	while true; do curl -s http://$MYHOST:8080/api/quote?strength=14 | jq .; done
	
	# kubectl run -i --tty load-generator --image=busybox /bin/sh
	# # while true; do wget -q -O- http://php-apache.default.svc.cluster.local; done		

Expected sample output:

	$ kubectl get hpa
	NAME      REFERENCE           TARGET    CURRENT   MINPODS   MAXPODS   AGE
	quotes    Deployment/quotes   50%       433%      1         10        3m
	
	$ kubectl get pods
	NAME                      READY     STATUS    RESTARTS   AGE
	quotes-4029858897-2nsm6   1/1       Running   0          2m
	quotes-4029858897-5xn93   1/1       Running   0          17m
	quotes-4029858897-82vdc   1/1       Running   0          6m
	quotes-4029858897-d5ctp   1/1       Running   0          6m
	quotes-4029858897-s6sl4   0/1       Pending   0          2m
	quotes-4029858897-t7sbj   1/1       Running   0          6m
	quotes-4029858897-w8crj   0/1       Pending   0          2m
	quotes-4029858897-xm68g   1/1       Running   0          2m

	$ kubectl get nodes
	NAME                           STATUS                     AGE
	kubernetes-master              Ready,SchedulingDisabled   21m
	kubernetes-minion-group-4ptj   Ready                      22m
	kubernetes-minion-group-l6kv   NotReady                   6s
	kubernetes-minion-group-xq6d   Ready                      18m

...after a while...

	$ kubectl get pods
	NAME                      READY     STATUS    RESTARTS   AGE
	quotes-4029858897-2nsm6   1/1       Running   0          4m
	quotes-4029858897-5xn93   1/1       Running   0          20m
	quotes-4029858897-82vdc   1/1       Running   0          8m
	quotes-4029858897-9h4sx   1/1       Running   0          37s
	quotes-4029858897-d5ctp   1/1       Running   0          8m
	quotes-4029858897-s6sl4   1/1       Running   0          4m
	quotes-4029858897-t7sbj   1/1       Running   0          8m
	quotes-4029858897-w8crj   1/1       Running   0          4m
	quotes-4029858897-wzhzf   1/1       Running   0          37s
	quotes-4029858897-xm68g   1/1       Running   0          4m

	$ kubectl get nodes
	NAME                           STATUS                     AGE
	kubernetes-master              Ready,SchedulingDisabled   23m
	kubernetes-minion-group-4ptj   Ready                      23m
	kubernetes-minion-group-l6kv   Ready                      1m
	kubernetes-minion-group-xq6d   Ready                      19m

...stop the load and wait to see automatic scale down...

	$ kubectl get pods
	NAME                      READY     STATUS    RESTARTS   AGE
	quotes-4029858897-5xn93   1/1       Running   0          16h
	
	$ kubectl get nodes
	NAME                           STATUS                     AGE
	kubernetes-master              Ready,SchedulingDisabled   16h
	kubernetes-minion-group-4ptj   Ready                      16h





Destroy cluster:


	export KUBE_GCE_ZONE=europe-west1-b
	./cluster/kube-down.sh
	
## Google Cloud Platform - Container Engine

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
	
Remove remaining forwarding rules	

	gcloud compute forwarding-rules list
	gcloud compute forwarding-rules delete a0f144949cc0c11e6815142010a84000 --region europe-west1

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

**Quotes:**

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


**Portal:**

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

Note on how to fix statis IP addresses for swarm nodes in VirtualBox: [https://github.com/docker/machine/issues/1709]()
Logs for a service [https://github.com/docker/docker/issues/23710]()

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

**FIRST MANUAL VERSION**

	docker $(docker-machine config swarm-manager-1) swarm init --advertise-addr $(docker-machine ip swarm-manager-1)
	docker $(docker-machine config swarm-worker-1)  swarm join \
	  --token SWMTKN-... \
	  $(docker-machine ip swarm-manager-1):2377
	docker $(docker-machine config swarm-worker-2)  swarm join \
	  --token SWMTKN-... \
	  $(docker-machine ip swarm-manager-1):2377

**SECOND IMPROVED AUTOMATED VERSION**

	ManagerIP=`docker-machine ip swarm-manager-1`
	
	docker-machine ssh swarm-manager-1 docker swarm init --advertise-addr ${ManagerIP}
	
	WorkerToken=`docker-machine ssh swarm-manager-1 docker swarm join-token worker | grep token | awk '{ print $2 }'`
	
	docker-machine ssh swarm-worker-1 "docker swarm join --token ${WorkerToken} ${ManagerIP}:2377"
	docker-machine ssh swarm-worker-2 "docker swarm join --token ${WorkerToken} ${ManagerIP}:2377"
	
Direct docker commands to a manager in the cluster:
	
	eval $(docker-machine env swarm-manager-1)    

Or a wroker:

	eval $(docker-machine env swarm-worker-1)    
	eval $(docker-machine env swarm-worker-2)    

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

Stop swarm:

	docker-machine stop swarm-manager-1 swarm-worker-1 swarm-worker-2

Start swarm:

	docker-machine start swarm-manager-1 swarm-worker-1 swarm-worker-2


Restart swarm:

	docker-machine restart swarm-manager-1 swarm-worker-1 swarm-worker-2

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

## Deploy quotes-service and portal	
network:

	# docker-compose bundle
	# docker deploy dockercomposev2

	docker network create --driver overlay my_network


quotes-service:
	
	# docker service create --replicas 1 --name quotes-service -p 8080:8080 --network my_network --update-delay 10s --update-parallelism 1 magnuslarsson/quotes:16

	docker service create --replicas 1 --name quotes-service -p 8080:8080 --network my_network magnuslarsson/quotes:16

	docker service ls
	docker service ps quotes-service
	docker service ps quotes-service --filter "desired-state=running"
	docker service inspect quotes-service	
	curl -s $(docker-machine ip swarm-manager-1):8080/api/quote | jq
	curl -s $(docker-machine ip swarm-worker-1):8080/api/quote | jq
	curl -s $(docker-machine ip swarm-worker-2):8080/api/quote | jq
	
	docker service scale quotes-service=3
	
	./swarm ls
	
	for ((i=1;i<=10;i++)); do curl -s $(docker-machine ip swarm-manager-1):8080/api/quote | jq .ipAddress; sleep 1; done	
	

portal:

	# docker service create --replicas 1 --name portal -p 9090:9090 --network my_network --update-delay 10s --update-parallelism 1 magnuslarsson/portal:17

	docker service create --replicas 1 --name portal -p 9090:9090 --network my_network magnuslarsson/portal:17

	curl -s $(docker-machine ip swarm-manager-1):9090/api/quote | jq
	curl -s $(docker-machine ip swarm-worker-1):9090/api/quote | jq
	curl -s $(docker-machine ip swarm-worker-2):9090/api/quote | jq
	
	for ((i=1;i<=10;i++)); do curl -s $(docker-machine ip swarm-manager-1):9090/api/quote | jq .ipAddress; sleep 1; done	

	# curl localhost:30000/home

Lookup IP address of quotes-service:

	docker ps | grep portal
	docker exec -it 70e1a6f9ec2b nslookup quotes-service


Web Browser URL:

	http://localhost:9080
	http://192.168.99.102:9080
	
**Note #1:** Scaling + round robin works fine using external curl command!!!

**Note #2:** Scaling + round robin now works with portal!!!
HttpCLient ConMgr TTL = 1 sec löste problemet...
Kan appache komma förbi Virtual Extensible LAN (VXLAN)???

Se [https://github.com/containous/traefik/issues/718]()
Ev oxå [https://github.com/docker/docker/issues/25325#issuecomment-245684162]()

**Note #3:** Upgrade a bundle is done with the docker deploy command

**Note #4:** When killing a quote container a few missing reqs are noted when calling the portal while calling the quotes service directly works fine!

### Teardown

	docker service rm quotes-service
	docker service rm portal
	docker network rm my_network
	
	
