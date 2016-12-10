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

## Setings

AWS_VPC_ID=vpc-da8d5abf

## 1. Create ECS IAM role


## 2. Create a ECS cluster

See https://console.aws.amazon.com/ecs

Lista nycklar:

	aws kms list-keys

...ger iam-user not authorized fel..

Configure and create a cluster with three nodes:

    ecs-cli configure --region eu-west-1 --access-key $AWS_ACCESS_KEY_ID --secret-key $AWS_SECRET_ACCESS_KEY --cluster ecs-ml-cluster

    ecs-cli up --keypair aws-key1 --capability-iam --size 3 --instance-type t2.small

Remove the cluster, if required:

    ecs-cli down -f

### Open ports in the clusters security group

For now we need to open the following ports

* 8761 (Discovery server, Eureka)
* 8888 (Config Server)
* 9999 (Local OAuth Server)
* 8443 (API-gateway/Edge server)

## 3. Create EC2 container instances
    
## 4. Create task

## 5. Create application load balancer

## 6. Create service 
      