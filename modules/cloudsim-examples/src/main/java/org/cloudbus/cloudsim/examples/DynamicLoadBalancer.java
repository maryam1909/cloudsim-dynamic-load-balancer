package org.cloudbus.cloudsim.examples;

import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class DynamicLoadBalancer {

    private List<Vm> vmList;

    public DynamicLoadBalancer(List<Vm> vmList) {
        this.vmList = vmList;
    }

    // Assign cloudlets to the least-loaded VM
    public Vm getLeastLoadedVm() {
        return vmList.get(new Random().nextInt(vmList.size())); // Simple random assignment
    }

    public void assignCloudlets(List<Cloudlet> cloudletList) {
        for (Cloudlet cloudlet : cloudletList) {
            Vm selectedVm = getLeastLoadedVm();
            cloudlet.setVmId(selectedVm.getId());
            System.out.println("Assigned Cloudlet #" + cloudlet.getCloudletId() + " to VM #" + selectedVm.getId());
        }
    }
}
