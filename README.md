A sample project 
=======
A sample project

## How to run the project locally

run the following command
```
mvn spring-boot:run
``` 

## How to deploy the operator

```
docker build -t mycontroller .
docker push mycontroller
kubectl apply -f deployment/
```

Your operator will be up and running on K8s cluster you are connected to very soon! 


