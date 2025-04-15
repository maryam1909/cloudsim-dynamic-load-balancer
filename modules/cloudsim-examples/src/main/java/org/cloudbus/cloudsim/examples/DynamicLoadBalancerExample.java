package org.cloudbus.cloudsim.examples;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Cloudlet.CloudletStatus;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class DynamicLoadBalancerExample {

    public static void main(String[] args) {

        Log.printLine("Starting Dynamic Load Balancing Simulation...");

        try {
            // Step 1: Initialize CloudSim
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;
            CloudSim.init(numUsers, calendar, traceFlag);

            // Step 2: Create Datacenter
            Datacenter datacenter0 = createDatacenter("Datacenter_0");

            // Step 3: Create Broker
            DatacenterBroker broker = new DatacenterBroker("Broker");

            // Step 4: Create VMs
            List<Vm> vmList = new ArrayList<>();
            int vmCount = 5;

            for (int i = 0; i < vmCount; i++) {
                Vm vm = new Vm(i, broker.getId(), 1000, 1, 1024, 1000, 10000,
                        "Xen", new CloudletSchedulerTimeShared());
                vmList.add(vm);
            }

            broker.submitGuestList(vmList);

            // Step 5: Create Cloudlets
            List<Cloudlet> cloudletList = new ArrayList<>();
            int cloudletCount = 10;

            for (int i = 0; i < cloudletCount; i++) {
                UtilizationModel utilization = new UtilizationModelFull();
                Cloudlet cloudlet = new Cloudlet(i, 4000, 1, 300, 300,
                        utilization, utilization, utilization);
                cloudlet.setUserId(broker.getId());

                // Dynamic Load Balancing: Assign to VM with least cloudlets
                int bestVmId = findLeastLoadedVm(vmList, cloudletList);
                cloudlet.setVmId(bestVmId);

                cloudletList.add(cloudlet);
            }

            broker.submitCloudletList(cloudletList);

            // Step 6: Start Simulation
            CloudSim.startSimulation();

            // Step 7: Print Results
            List<Cloudlet> results = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();
            printCloudletResults(results);

            Log.printLine("Simulation completed successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("An error occurred during simulation.");
        }
    }

    // Method to create Datacenter
    private static Datacenter createDatacenter(String name) throws Exception {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();

        int mips = 10000;
        peList.add(new Pe(0, new PeProvisionerSimple(mips)));

        int ram = 8192; // MB
        long storage = 1_000_000; // MB
        int bw = 10000;

        Host host = new Host(0, new RamProvisionerSimple(ram),
                new BwProvisionerSimple(bw), storage, peList,
                new VmSchedulerTimeShared(peList));

        hostList.add(host);

        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double timeZone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, timeZone, cost,
                costPerMem, costPerStorage, costPerBw);

        return new Datacenter(name, characteristics,
                new VmAllocationPolicySimple(hostList), new LinkedList<>(), 10);
    }

    // Method to find the VM with the least cloudlets
    private static int findLeastLoadedVm(List<Vm> vmList, List<Cloudlet> assignedCloudlets) {
        int[] vmLoads = new int[vmList.size()];

        // Count the number of cloudlets assigned to each VM
        for (Cloudlet cloudlet : assignedCloudlets) {
            if (cloudlet.getVmId() >= 0 && cloudlet.getVmId() < vmLoads.length) {
                vmLoads[cloudlet.getVmId()]++;
            }
        }

        // Find the VM with the least load (cloudlets)
        int minLoad = Integer.MAX_VALUE;
        int bestVmId = 0;

        for (int i = 0; i < vmLoads.length; i++) {
            if (vmLoads[i] < minLoad) {
                minLoad = vmLoads[i];
                bestVmId = i;
            }
        }

        return bestVmId;
    }

    // Method to print the results
    private static void printCloudletResults(List<Cloudlet> list) {
        String indent = "    ";
        Log.printLine("\n========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
                "Datacenter ID" + indent + "VM ID" + indent +
                "Time" + indent + "Start Time" + indent + "Finish Time");

        for (Cloudlet cloudlet : list) {
            Log.printLine(cloudlet.getCloudletId() + indent + indent +
                    (cloudlet.getStatus() == CloudletStatus.SUCCESS ? "SUCCESS" : "FAILED") + indent +
                    cloudlet.getResourceId() + indent + indent + cloudlet.getVmId() + indent +
                    cloudlet.getActualCPUTime() + indent + cloudlet.getExecStartTime() + indent + cloudlet.getFinishTime());
        }
    }
}
