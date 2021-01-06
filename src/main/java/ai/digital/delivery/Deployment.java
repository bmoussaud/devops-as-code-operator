package ai.digital.delivery;

import io.fabric8.kubernetes.client.CustomResource;

public class Deployment extends CustomResource {

    private DeploymentSpec spec;

    private DeploymentStatus status;

    public DeploymentSpec getSpec() {
        if (spec == null) {
            spec = new DeploymentSpec();
        }
        return spec;

    }

    public void setSpec(DeploymentSpec spec) {
        this.spec = spec;
    }

    public DeploymentStatus getStatus() {
        if (status == null ) {
            status = new DeploymentStatus();
        }
        return status;
    }

    public void setStatus(DeploymentStatus status) {
        this.status = status;
    }
}