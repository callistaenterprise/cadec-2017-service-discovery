# PREPARE

	cd ~/Documents/projects/cadec-2017/service-discovery/git/cadec-2017-service-discovery

For all terminal windows:

	export PS1="$ "
	
## PREPARE Netflix

1. One terminal for Netflix Eureka
2. One browser for Eureka 
3. One browser for portal

## PREPARE Spring Cloud, ELK, Zipkin and Prometheus

		cd performancetest
		docker compose up -d

		cd blog-microservices
		docker compose up -d

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

1. Command prompt för at tkunna köra t ex `kubectl get svc `

1. Arrangera terminal fönster och tre web läsare fönster, sänk tid till 40 ms



# Spring Cloud and Netflix Eureka, No Docker

In folder /

	termrc start  Eureka Web: [http://localhost:8761](http://localhost:8761)  Open portal web app: [http://localhost:9090](http://localhost:9090)

1. visa eureka
2. några single anrop för att påvisa round robin
3. starta kontinuerliga anrop
4. stoppa quote service 1
5. inga fel i protal, men bara anrop till service #2
6. visa eureka igen

# Spring Cloud, ELK, Zipkin and Prometheus

Eureka, test (Edge, Oauth), scale, load balance, Elastic, Zipkin, config server, circuit breaker, Prometheus1. Kolla i Eureka1. test-them-all och oauth security			. ./test-all.sh		curl -ks https://localhost:443/api/product/123 -H "Authorization: Bearer $TOKEN" | jq .1. Logstash & zipkin	Note: Kibana fields: level, corr-service, corr-id, message, host		curl -ks https://localhost:443/api/product/123123 -H "Authorization: Bearer $TOKEN" | jq .	Sök I zipkin: `http.path=/api/product/123123`1. Scale
		docker-compose scale rec=2	
	Eureka + returnerad IP adress efter curl-anrop	Sök i Kibana på:	sök på 123123 + review service1. Kolla i Hystrix		vi ../blog-microservices-config/review-service.yml	
		time curl -ks https://localhost:443/api/product/123 -H "Authorization: Bearer $TOKEN" | jq .1. Prometheus		. ./test-all.sh		while true; do clear; time curl -ks https://localhost:443/api/product/123 -H "Authorization: Bearer $TOKEN" | jq .; sleep 1; done	Increase CPU usage		vi ../blog-microservices-config/review-service.yml
# Docker Swarm

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

	docker service update --image magnuslarsson/quotes:go-22 quotes-service
	docker service scale quotes-service=10

Rolling back: 

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

