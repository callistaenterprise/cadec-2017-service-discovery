# PREPARE

	cd ~/Documents/projects/cadec-2017/service-discovery/git/cadec-2017-service-discovery

For all terminal windows:

	export PS1="$ "
	
## PREPARE Netflix

1. One terminal for Netflix Eureka
2. One browser for Eureka 
3. One browser for portal

## PREPARE K8S 

1. Monitor hpa

		while true; do o="$(kubectl get hpa)"; clear; echo "$o"; sleep 3; done

1. Monitor pods

		while true; do o="$(kubectl get pods)"; clear; echo "$o"; sleep 3; done

1. Monitor nodes

		while true; do o="$(kubectl get nodes)"; clear; echo "$o"; sleep 3; done

1. Arrangera terminal fönster och tre web läsare fönster

## PREPARE Docker-Compose

1. Two terminals for docker-compose
	
		cd docker-compose-v2

## PREPARE SWARM

1. Start swarm:

		docker-machine start swarm-manager-1 swarm-worker-1 swarm-worker-2

1. Direct docker commands to a manager in the cluster:
	
		eval $(docker-machine env swarm-manager-1)    

1. Open visualizer in a web browser: [http://192.168.99.100:8000](http://192.168.99.100:8000)

1. Open portal in web browser

## PREPARE KUBERNETES

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

# Spring Cloud and Netflix Eureka

In folder /

	termrc start  Eureka Web: [http://localhost:8761](http://localhost:8761)  Open portal web app: [http://localhost:9090](http://localhost:9090)

# docker-compose

In folder /docker-compose-v2
	

	docker-compose up	docker-compose scale quotes-service=2	docker-compose ps	docker-compose exec portal-service nslookup quotes-service
Open portal web app: [http://localhost:9090](http://localhost:9090)

# Docker Swarm

**Init**

	eval $(docker-machine env swarm-manager-1)
	docker info

**Check**

	docker network ls
	docker service ls

## more Swarm demo steps

**Start requests in Portal**

[http://192.168.99.100:9090](http://192.168.99.100:9090)

**Kill a container**

	eval $(docker-machine env swarm-manager-1)
	eval $(docker-machine env swarm-worker-1)
	eval $(docker-machine env swarm-worker-2)
	docker kill TAB
	
	docker $(docker-machine config swarm-worker-1) kill 
	docker $(docker-machine config swarm-worker-2) kill 

## Swarm - Demo steps
	
	docker network create --driver overlay my_network
	docker service create --replicas 1 --name portal -p 9090:9090 --network my_network magnuslarsson/portal:17
	docker service create --replicas 3 --name quotes-service -p 8080:8080 --network my_network magnuslarsson/quotes:16

	docker service logs -f portal
	 
	.
		
## Kill a node

Verify that no browser use the node to be killed!!!

	docker-machine stop swarm-worker-1
	docker-machine stop swarm-worker-2

	docker-machine start swarm-worker-2
	docker-machine start swarm-worker-2


## Docker Swarm rolling upgrade

	docker service update --image magnuslarsson/quotes:go-22 quotes-service
	docker service scale quotes-service=10

Rolling back: 

	docker service scale quotes-service=3
	docker service update --image magnuslarsson/quotes:16    quotes-service

## Teardown

	docker service rm quotes-service
	docker service rm portal
	docker network rm my_network

	docker-machine start swarm-worker-1
	docker-machine start swarm-worker-2

Also stop the nodes:

	docker-machine stop swarm-manager-1 swarm-worker-1 swarm-worker-2
	
	
	
# KUBERNETES


Open portal in web browser using its external ip: [http://146.148.16.15:9090](http://146.148.16.15:9090)
	
	
* Dra ner Req Interval till 400 ms...
	1. Starta 1 web läsare
		* 16.43.30 - 16.44.27
		* Vänta 60 - 90 sek...
	1. Starta 3 web läsare: ??? 
		* Vänta ??? sek...


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

## Shut down Kubernetes

	export KUBE_GCE_ZONE=europe-west1-b
	# export NODE_SIZE=n1-standard-1
	# export NUM_NODES=1
	# export KUBE_ENABLE_CLUSTER_AUTOSCALER=true
	# export KUBE_AUTOSCALER_MIN_NODES=1
	# export KUBE_AUTOSCALER_MAX_NODES=5
	./cluster/kube-down.sh

# AMAZON AWS/ALB

**portal-service:** [http://ML-ALB-1373732302.eu-west-1.elb.amazonaws.com](http://ML-ALB-1373732302.eu-west-1.elb.amazonaws.com)
	
	
	  