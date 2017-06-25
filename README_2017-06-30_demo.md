# PREPARE

	cd ~/Documents/projects/cadec-2017/service-discovery/git/cadec-2017-service-discovery

For all terminal windows:

	export PS1="$ "
	
## PREPARE Local Dev

1. IntelliJ 2017.2 EAP
   * Open Run Dashboard
1. One browser tab with http://127.0.0.1:9090 
1. One browser tab with http://127.0.0.1:8080/api/quote 

## PREPARE Docker Compose 

Build binaries:

    cd quotes && ./gradlew build && cd -
    cd portal && ./gradlew build && cd -
    
## PREPARE Docker Machine in AWS

Create machine:

These environment variables can typically be set in the startup script `~.bash_profile`

    export AWS_ACCESS_KEY=...
    export AWS_SECRET_KEY=...
    export AWS_VPC_ID=...

    env | grep AWS

## Create a Docker host in AWS

    docker-machine -D create \
      --driver amazonec2 \
      --amazonec2-access-key $AWS_ACCESS_KEY \
      --amazonec2-secret-key $AWS_SECRET_KEY \
      --amazonec2-vpc-id $AWS_VPC_ID \
      --amazonec2-region "eu-west-1" \
      --amazonec2-zone a \
      --amazonec2-instance-type "t2.large" \
      aws-test-1
	
	eval "$(docker-machine env aws-test-1)"

	docker info | grep provider
	> provider=amazonec2

## Configure the firewall in the AWS security group

A Docker host use by default a security group named `docker-machine`. 
Use the [AWS Management Console](https://eu-west-1.console.aws.amazon.com/ec2/v2/home?region=eu-west-1#SecurityGroups) to determine its id.
Define an environment variable like:

    export AWS_SECURITY_GROUP_ID=...
    
We need to open up the TCP ports 8080 and 9090

    ec2-authorize -p 8080 -P TCP $AWS_SECURITY_GROUP_ID --region eu-west-1
    ec2-authorize -p 9090 -P TCP $AWS_SECURITY_GROUP_ID --region eu-west-1

	    
## PREPARE SWARM

1. Start swarm:

		docker-machine start swarm-manager-1 swarm-worker-1 swarm-worker-2

1. Direct docker commands to a manager in the cluster:
	
		eval $(docker-machine env swarm-manager-1)    

1. Open visualizer in a web browser: [http://192.168.99.100:8000](http://192.168.99.100:8000)

1. Open portal in web browser

## PREPARE KUBERNETES

### Setup K8S cluster

Setup cluster with auto scaling of nodes

	cd ~/Documents/projects/cadec-2017/service-discovery
	
	export KUBE_GCE_ZONE=europe-west1-b
	export NODE_SIZE=n1-standard-1
	export NUM_NODES=1
	export KUBE_ENABLE_CLUSTER_AUTOSCALER=true
	export KUBE_AUTOSCALER_MIN_NODES=1
	export KUBE_AUTOSCALER_MAX_NODES=5
	
	cd kubernetes
	./cluster/kube-up.sh	
	
	kubectl get nodes
	kubectl cluster-info	
	grep password /Users/magnus/.kube/config

Setup services with auto scaling of quotes pods:

	kubectl run quotes --image=magnuslarsson/quotes:16 --port=8080 
	kubectl expose deployment quotes --type=LoadBalancer --name quotes-service

	kubectl run portal --image=magnuslarsson/portal:17 --port=9090 
	kubectl expose deployment portal --type=LoadBalancer --name portal-service

	kubectl get deployment
	kubectl get pods
	kubectl get svc quotes-service
	kubectl get svc portal-service
	
	kubectl autoscale deployment quotes --cpu-percent=50 --min=1 --max=10
	kubectl get hpa

Verify IP addresses

	$ kubectl get svc
	NAME             CLUSTER-IP    EXTERNAL-IP      PORT(S)    AGE
	kubernetes       10.0.0.1      <none>           443/TCP    1d
	portal-service   10.0.94.30    146.148.16.15    9090/TCP   1d
	quotes-service   10.0.17.114   23.251.139.163   8080/TCP   1d

	$ kubectl get pods
	NAME                      READY     STATUS    RESTARTS   AGE
	portal-329106472-6qw13    1/1       Running   0          1d
	quotes-4111254610-x7ng5   1/1       Running   0          1d

	$ kubectl exec -it portal-329106472-6qw13 nslookup quotes-service
	Name:      quotes-service
	Address 1: 10.0.17.114 quotes-service.default.svc.cluster.local

### PREPARE K8S DEMO ENV

1. Monitor hpa

		while true; do o="$(kubectl get hpa)"; clear; echo "$o"; sleep 3; done

1. Monitor pods

		while true; do o="$(kubectl get pods)"; clear; echo "$o"; sleep 3; done

1. Monitor nodes

		while true; do o="$(kubectl get nodes)"; clear; echo "$o"; sleep 3; done

1. Command prompt för att kunna köra t ex `kubectl get svc `

1. Arrangera terminal fönster och tre web läsare fönster, sänk tid till 40 ms


# Local Dev

1. Start Q + P in Dashboard
1. (Show health endpoint)
1. Web browser - show quotes
1. Web browser - show portal
1. Stop Q + P in Dashboard

# Docker Compose

1. Build and start

        docker-compose build
        docker-compose up -d

  Kontrollera health state + logs:

        docker ps
        > health: starting       

        docker-compose logs -f

        docker ps
        > healthy       


1. Samma demo steg som ovan

1. Stoppa

        docker-compose down

# Test with docker-machine on AWS

	eval $(docker-machine env aws-test-1) 

samma som  ovan fast mot AWS IP-Adr

	docker-machine ls

# Docker Swarm - OLD

**Init**

	eval $(docker-machine env swarm-manager-1)
	docker info

**Check**

	docker network ls
	docker service ls

**Setup**
	
	docker network create --driver overlay my_network
	docker service create --replicas 3 --name quotes-service -p 8080:8080 --network my_network magnuslarsson/quotes:16
	docker service create --replicas 1 --name portal -p 9090:9090 --network my_network magnuslarsson/portal:17

	docker service logs -f portal
	 
	.

Healthcheck?

	--health-cmd ["CMD", "wget", "-sq", "http://localhost:9090/health"]
	--health-interval 15s
	--health-retries 4
	--health-timeout	5s

      test: ["CMD", "wget", "-sq", "http://localhost:9090/health"]
      interval: 15s
      timeout: 5s
      retries: 4

# Docker Swarm

Deploy Stack:

	docker stack deploy -c my-app.yml my-appList stack and services
	docker stack ls	docker stack ps my-appLog services:
	docker service logs -f my-app_portal-service	docker service logs -f my-app_quotes-service

## Demo steps
	
**Start requests in Portal**

[http://192.168.99.100:9090](http://192.168.99.100:9090)

**Kill a container**

	eval $(docker-machine env swarm-manager-1)
	eval $(docker-machine env swarm-worker-1/2)
	docker kill TAB
	
	docker $(docker-machine config swarm-worker-1/2) kill 
	
**Kill a node**

Verify that no browser use the node to be killed!!!

	docker-machine stop swarm-worker-1/2	

	docker-machine start swarm-worker-1/2


## Docker Swarm rolling upgrade

	docker service update \
	  --image magnuslarsson/quotes:go-23 \
	  --update-parallelism 1 \
	  --update-delay 5s \
	  my-app_quotes-service

	docker service scale my-app_quotes-service=10

Rolling back: 

	docker service update \
	  --image magnuslarsson/quotes:16 \
	  --replicas 3 \
	  --update-parallelism 1 \
	  --update-delay 5s \
	  my-app_quotes-service


### Teardown

	docker stack rm my-app

## Docker Swarm rolling upgrade - OLD
 
FUNKAR INTE (PGA EXTRA SCALE KOMMANDO?):

	docker service update \
	  --rollback \
	  --replicas 3 \
	  --update-delay 0s \
	  my-app_quotes-service

OLD:

	docker service scale quotes-service=3
	docker service update --image magnuslarsson/quotes:16 quotes-service


### Teardown

	docker service rm quotes-service
	docker service rm portal
	docker network rm my_network

	docker-machine start swarm-worker-1/2

Also stop the nodes:

	docker-machine stop swarm-manager-1 swarm-worker-1 swarm-worker-2
	
	
	
# KUBERNETES


Open portal in web browser using its external ip: [http://146.148.16.15:9090](http://146.148.16.15:9090)
	
	

# Shutdown

## Shut down Kubernetes

	export KUBE_GCE_ZONE=europe-west1-b
	# export NODE_SIZE=n1-standard-1
	# export NUM_NODES=1
	# export KUBE_ENABLE_CLUSTER_AUTOSCALER=true
	# export KUBE_AUTOSCALER_MIN_NODES=1
	# export KUBE_AUTOSCALER_MAX_NODES=5
	./cluster/kube-down.sh

