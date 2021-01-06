package=$1
environment=$2

echo "
apiVersion: xl-deploy/v1
kind: Deployment
spec:
  package: ${package}
  environment: ${environment}
  onSuccessPolicy: ARCHIVE
  orchestrators:
    - sequential-by-deployed
" > /tmp/deploy.yaml

cat /tmp/deploy.yaml
xl preview --xl-deploy-url http://d9c082f8bb54.ngrok.io -f /tmp/deploy.yaml

xl apply --xl-deploy-url http://d9c082f8bb54.ngrok.io -f /tmp/deploy.yaml
