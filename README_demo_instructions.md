# Docker Swarm

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

	ManagerIP=`docker-machine ip swarm-manager-1`
	
	docker-machine ssh swarm-manager-1 docker swarm init --advertise-addr ${ManagerIP}
	
	WorkerToken=`docker-machine ssh swarm-manager-1 docker swarm join-token worker | grep token | awk '{ print $2 }'`
	
	docker-machine ssh swarm-worker-1 "docker swarm join --token ${WorkerToken} ${ManagerIP}:2377"
	docker-machine ssh swarm-worker-2 "docker swarm join --token ${WorkerToken} ${ManagerIP}:2377"
	
Direct docker commands to a manager in the cluster:
	
	eval $(docker-machine env swarm-manager-1)    

Or a worker:

	eval $(docker-machine env swarm-worker-1)    
	eval $(docker-machine env swarm-worker-2)    

Inspect the cluster;
	
	docker info
	docker node ls

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

First find the IP address for one of the nodes in the cluster:

	docker-machine ip swarm-worker-1
	> 192.168.99.102

Open a web browser using the ip address:

	http://192.168.99.102:8000

## Deploy quotes-service and portal

Show empty cluster using visualizer: [http://192.168.99.102:8000](http://192.168.99.102:8000)

**network**

	docker network create --driver overlay my_network

**quotes-service**
	
	docker service create --replicas 1 --name quotes-service -p 8080:8080 --network my_network magnuslarsson/quotes:14

**portal**

	docker service create --replicas 1 --name portal -p 9090:9090 --network my_network magnuslarsson/portal:14

Show deployed cluster using visualizer: [http://192.168.99.102:8000](http://192.168.99.102:8000)

Goto [http://localhost:9090](http://localhost:9090) and start the portal

**scale quotesservice

	docker service scale quotes-service=3

Show deployed cluster using visualizer: [http://192.168.99.102:8000](http://192.168.99.102:8000)

Note load balancing working in portal!

**kill a quote-service:**

Show cluster using visualizer: [http://192.168.99.102:8000](http://192.168.99.102:8000)

	./swarm ls
	./swarm kill ${ip-address}

verify that no call to the portal fails while one of the quote services is restarted by Swarm!	

**kill a node:**

Show cluster using visualizer: [http://192.168.99.102:8000](http://192.168.99.102:8000)

1. Look up a worker node where only quote services run:

		./swarm ls

1. kill the worker node:

		docker-machine stop swarm-worker-1/2
		
1. verify that no call to the portal fails while the quote services on the failed node are restarted by Swarm on the other nodes!

1. verify 

		docker service ps quotes-service --filter "desired-state=running"

### Teardown

	docker service rm quotes-service
	docker service rm portal
	docker network rm my_network
	
	
