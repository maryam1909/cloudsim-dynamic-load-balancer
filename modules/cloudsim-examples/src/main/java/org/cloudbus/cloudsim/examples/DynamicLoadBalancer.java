package org.cloudbus.cloudsim.examples;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class DynamicLoadBalancer {

    private List<Vm> vmList;

    public DynamicLoadBalancer(List<Vm> vmList) {
        this.vmList = vmList;
    }

    // Assign cloudlets to the least-loaded VM
   
    public Vm getLeastLoadedVm(List<Cloudlet> assignedCloudlets) {
        int[] vmLoads = new int[vmList.size()];
        for (Cloudlet c : assignedCloudlets) {
            if (c.getVmId() >= 0 && c.getVmId() < vmLoads.length) {
                vmLoads[c.getVmId()]++;
            }
        }

        int minLoad = Integer.MAX_VALUE;
        Vm bestVm = vmList.get(0);

        for (Vm vm : vmList) {
            int load = vmLoads[vm.getId()];
            if (load < minLoad) {
                minLoad = load;
                bestVm = vm;
            }
        }

        return bestVm;
    }

    public void assignCloudlets(List<Cloudlet> cloudletList) {
        List<Cloudlet> assignedCloudlets = new ArrayList<>();

        for (Cloudlet cloudlet : cloudletList) {
            Vm selectedVm = getLeastLoadedVm(assignedCloudlets);
            cloudlet.setVmId(selectedVm.getId());
            assignedCloudlets.add(cloudlet); // update assigned cloudlets
            System.out.println("Assigned Cloudlet #" + cloudlet.getCloudletId() + " to VM #" + selectedVm.getId());
        }
    }

    }
