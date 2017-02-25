# AWS ECS

[https://rossfairbanks.com/2015/03/31/hello-world-in-ec2-container-service.html]()

[https://aws.amazon.com/blogs/aws/new-aws-application-load-balancer/]()

[https://sreeninet.wordpress.com/2016/09/03/aws-ecs-docker-container-service/]()

[https://aws.amazon.com/blogs/compute/microservice-delivery-with-amazon-ecs-and-application-load-balancers/]()

## Overview

Following are the steps:

1. Create ECS IAM role that allows for EC2 instance to register to Container instance.
1. Create cluster from ECS menu. Multiple EC2 hosts can be part of the same cluster.
1. Create EC2 container instances and tie them to the cluster. We need to choose Container instances with ECS ami-id. In this customized EC2 instances, Amazon will install Docker and also install EC2 agent container that will do container health check and other house keeping.  I chose amzn-ami-2016.03.h-amazon-ecs-optimized(ecs optimized) AMI. As part of instance creation, we need to use the correct IAM role for the instance so that the instance can add itself to the ecs cluster.
1. Create task. A task can have 1 or more containers. A task is equivalent to docker-compose YML file.
1. Create application load balancer
1. Create service and tie it to either classic load balancer or application load balancer. Load balancer needs to be created before. Service can expose the application endpoint either internally or externally.

## CLI install

See [install aws and ecs cli](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/get-set-up-for-amazon-ecs.html#install_ecs_cli).

## Settings

AWS_VPC_ID=vpc-da8d5abf

## 1. Create ECS IAM role


## 2. Create a ECS cluster

See https://console.aws.amazon.com/ecs

Lista nycklar:

	aws kms list-keys

...ger iam-user "not authorized" fel..

Configure (stored in `~/.ecs/config`) and create a cluster with three nodes:

    AWS_ACCESS_KEY_ID=...
    AWS_SECRET_ACCESS_KEY=...
    ecs-cli configure --region eu-west-1 --access-key $AWS_ACCESS_KEY_ID --secret-key $AWS_SECRET_ACCESS_KEY --cluster ecs-ml-cluster

    ecs-cli up --keypair aws-key1 --capability-iam --size 1 --instance-type t2.micro

Remove the cluster, if required:

    ecs-cli down -f

### Open ports in the clusters security group

For now we need to open the following ports

* 22 - SSH
* 9090 - Portal

### Update ECS Container Agent

See [http://docs.aws.amazon.com/AmazonECS/latest/developerguide/agent-update-ecs-ami.html](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/agent-update-ecs-ami.html)

Inside each instance:

	ssh ...
	[ec2-user]$ sudo yum update -y ecs-init
	[ec2-user]$ sudo service docker restart 
	[ec2-user]$ sudo start ecs
	
Using ´aws ecs´ CLI:

	aws ecs update-container-agent --cluster ecs-ml-cluster --container-instance container_instance_id	
### SSH access

ssh -i "${private-key}" ec2-user@${external-ip}

## 3. Create EC2 container instances

### Instance health check

...simply use /health and traffic port, deadeasy!

### Tmp instance health check (before I found out the simple real solution :-)

Install Apache to perform health checks

	sudo yum -y install httpd    
	sudo service httpd start
	sudo vi /var/www/html/index.html
	OK!
	curl -i localhost
	
Ensure Apache starts on reboot
	
	sudo chkconfig httpd on
	
## 4. Create task

Create task definitions from docker-compose files:

    cd aws-ecs/quotes
    ecs-cli compose create

    cd aws-ecs/portal
    ecs-cli compose create



## 5. Create application load balancer

Manuellt enligt ..., sedan testa med:

**quote-service:**

	time curl -s ML-ALB-1373732302.eu-west-1.elb.amazonaws.com/api/quote?strength=4
	curl -s ML-ALB-1373732302.eu-west-1.elb.amazonaws.com/api/quote?strength=4 | jq .ipAddress

hostname = container id skall skilja vid round robin, ip adress kan vara samma...
 	
**portal-service:** [http://ML-ALB-1373732302.eu-west-1.elb.amazonaws.com](http://ML-ALB-1373732302.eu-west-1.elb.amazonaws.com)	

## 6. Create service 

    cd docker-compose-v2
    ecs-cli compose --file docker-compose.yml service up
      
## 7. Load tests

16.48: 500 ms, strength: 12, queue: 64
I instance 100% CPU...

16.51: increaed to 600ms, queue increased...

## 8. Putting ECS cluster to sleep

### Set service min count to 0

Update each ESC service and set number of tasks to 0 (was 2 for portal and 3 for quotes)
 
### Set node instance min count to 0

Update the auto scaling group and edit details and set Min = 0 (was 2, one for each AZ)
