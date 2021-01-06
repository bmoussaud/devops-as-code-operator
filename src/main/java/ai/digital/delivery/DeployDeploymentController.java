package ai.digital.delivery;

import io.javaoperatorsdk.operator.api.Context;
import io.javaoperatorsdk.operator.api.Controller;
import io.javaoperatorsdk.operator.api.ResourceController;
import io.javaoperatorsdk.operator.api.UpdateControl;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.javaoperatorsdk.operator.api.DeleteControl;

@Controller(crdName = "deployments.deploy.digital.ai")
public class DeployDeploymentController implements ResourceController<Deployment> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final KubernetesClient kubernetesClient;

    public DeployDeploymentController(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @Override
    public UpdateControl<Deployment> createOrUpdateResource(Deployment deployment, Context<Deployment> context) {
        log.info("createOrUpdateResource() called with: customResource = [" + deployment + "], context = [" + context + "]");
        log.info(deployment.getSpec().getVersion() +" -> "+ deployment.getSpec().getEnvironment());
        return UpdateControl.updateCustomResource(deployment);
    }

    @Override
    public DeleteControl deleteResource(Deployment customResource, Context<Deployment> context) {
        log.info("deleteResource() called with: customResource = [" + customResource + "], context = [" + context + "]");
        return DeleteControl.DEFAULT_DELETE;
    }

}
