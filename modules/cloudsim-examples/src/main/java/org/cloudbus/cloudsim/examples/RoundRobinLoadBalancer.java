package org.cloudbus.cloudsim.examples;

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;

public class RoundRobinLoadBalancer {

    private int vmIndex = 0;

    public void assignCloudletsToVms(List<Cloudlet> cloudletList, List<Vm> vmList, DatacenterBroker broker) {
        for (Cloudlet cloudlet : cloudletList) {
            Vm vm = vmList.get(vmIndex);
            System.out.println("Assigning Cloudlet #" + cloudlet.getCloudletId() + " to VM #" + vm.getId());
            broker.bindCloudletToVm(cloudlet.getCloudletId(), vm.getId());
            vmIndex = (vmIndex + 1) % vmList.size();
        }
    }
}