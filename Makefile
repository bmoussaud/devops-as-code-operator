REGISTRY=registry.local:5000

VERSION=0.1.0-SNAPSHOT
DOCKER_IMAGE=$(REGISTRY)/digital.ai/devops-as-code-operator:$(VERSION)
DOCKER_IMAGE_DEV=$(REGISTRY)/digital.ai/devops-as-code-operator:dev

crd:
	kubectl delete -f crd/crd.yaml
	kubectl apply -f crd/crd.yaml

java-build:
	mvn clean package

build: java-build
	echo $(DOCKER_IMAGE)
	docker build --build-arg JAR_FILE=devops-as-code-operator-0.1.0-SNAPSHOT.jar -t $(DOCKER_IMAGE) .
	docker tag $(DOCKER_IMAGE) $(DOCKER_IMAGE_DEV)
	#docker push $(DOCKER_IMAGE)
	docker push $(DOCKER_IMAGE_DEV)


deploy: build
	kubectl delete -f k8s/deployment.yaml
	kubectl apply -f k8s/deployment.yaml
