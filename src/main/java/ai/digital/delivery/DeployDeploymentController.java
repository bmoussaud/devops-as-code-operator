package ai.digital.delivery;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.batch.Job;
import io.fabric8.kubernetes.api.model.batch.JobBuilder;
import io.javaoperatorsdk.operator.api.Context;
import io.javaoperatorsdk.operator.api.Controller;
import io.javaoperatorsdk.operator.api.ResourceController;
import io.javaoperatorsdk.operator.api.UpdateControl;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.javaoperatorsdk.operator.api.DeleteControl;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

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
        log.info(deployment.getSpec().getVersion() + " -> " + deployment.getSpec().getEnvironment());
        triggerApplyJob();
        return UpdateControl.updateCustomResource(deployment);
    }

    @Override
    public DeleteControl deleteResource(Deployment customResource, Context<Deployment> context) {
        log.info("deleteResource() called with: customResource = [" + customResource + "], context = [" + context + "]");
        return DeleteControl.DEFAULT_DELETE;
    }

    private void triggerApplyJob() {

        final String namespace = "default";
        final Job job = new JobBuilder()
                .withApiVersion("batch/v1")
                .withNewMetadata()
                .withName("xl-cli-apply")
                .withNamespace(namespace)
                //.withLabels(Collections.singletonMap("xldeploy", "maximum-length-of-63-characters"))
                //.withAnnotations(Collections.singletonMap("annotation1", "some-very-long-annotation"))
                .endMetadata()
                .withNewSpec()
                .withNewTemplate()
                .withNewSpec()
                .addNewVolume().withName("xl-data-volume").withNewConfigMap().withName("xl-data-configmap").endConfigMap().endVolume()
                .addNewContainer().withName("xl-cli").withImage("bmoussaud/xl-cli:9.8.0")
                .withArgs("--xl-deploy-url", "http://d9c082f8bb54.ngrok.io", "--xl-deploy-username", "admin", "--xl-deploy-password", "admin", "apply", "-f", "/tmp/config/xl-deploy.yaml")
                .addNewVolumeMount().withName("xl-data-volume").withMountPath("/tmp/config").endVolumeMount()
                .withImagePullPolicy("Always")
                .endContainer()
                .withRestartPolicy("Never")
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();
        log.info("Creating job " + job.getMetadata().getName());
        kubernetesClient.batch().jobs().inNamespace(namespace).createOrReplace(job);


        // Get All pods created by the job
        PodList podList = kubernetesClient.pods().inNamespace(namespace).withLabel("job-name", job.getMetadata().getName()).list();
        // Wait for pod to complete
        try {
            kubernetesClient.pods().inNamespace(namespace).withName(podList.getItems().get(0).getMetadata().getName())
                    .waitUntilCondition(pod -> pod.getStatus().getPhase().equals("Succeeded"), 1, TimeUnit.MINUTES);

            // Print Job's log
            String joblog = kubernetesClient.batch().jobs().inNamespace(namespace).withName(job.getMetadata().getName()).getLog();
            log.info(joblog);

        } catch (InterruptedException e) {
            log.warn("Thread interrupted!",e);
            Thread.currentThread().interrupt();
        }

    }

}
