package org.cloudbus.cloudsim.examples;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.junit.Test;

public class LoadBalancerTest {

    @Test
    public void testDynamicLoadBalancer() {
        List<Vm> vmList = new ArrayList<>();
        List<Cloudlet> cloudletList = new ArrayList<>();

        // Create dummy VMs
        for (int i = 0; i < 5; i++) {
            Vm vm = new Vm(i, 1, 1000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerTimeShared());
            vmList.add(vm);
        }

        // Create dummy Cloudlets
        for (int i = 0; i < 10; i++) {
            Cloudlet cloudlet = new Cloudlet(i, 4000, 1, 300, 300,
                    new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
            cloudlet.setUserId(1);
            cloudletList.add(cloudlet);
        }

        // Test the Dynamic Load Balancer
        DynamicLoadBalancer loadBalancer = new DynamicLoadBalancer(vmList);
        loadBalancer.assignCloudlets(cloudletList);

        // Check if all Cloudlets have been assigned to a VM
        assertTrue(cloudletList.stream().allMatch(cloudlet -> cloudlet.getVmId() >= 0));
    }
}
