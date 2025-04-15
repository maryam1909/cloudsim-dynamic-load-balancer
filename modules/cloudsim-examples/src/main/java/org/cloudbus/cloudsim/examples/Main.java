package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.*;

public class Main {

    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmList;
    private static int numCloudlets = 10;
    private static int numVms = 5;
    private static int pesNumber = 1; // Number of CPUs per cloudlet

    public static void main(String[] args) {
        Log.printLine("Starting CloudSim Example (7.0.0-alpha)...");

        try {
            // Initialize the CloudSim library
            CloudSim.init(numVms, Calendar.getInstance(), false);

            // Create Datacenter
            Datacenter datacenter = createDatacenter("Datacenter_0");

            // Create Broker
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            // Create VMs and Cloudlets
            vmList = createVms(brokerId);
            cloudletList = createCloudlets(brokerId);

            // Submit VMs and Cloudlets to broker
            broker.submitGuestList(new ArrayList<>(vmList));
            broker.submitCloudletList(new ArrayList<>(cloudletList));

            // Start the simulation
            CloudSim.startSimulation();

            // Print results when simulation is over
            List<Cloudlet> results = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();

            printCloudletList(results);
            Log.printLine("CloudSim Example finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

    private static Datacenter createDatacenter(String name) {
        // Create PEs
        List<Pe> peList = new ArrayList<>();
        int mips = 1000;
        peList.add(new Pe(0, new PeProvisionerSimple(mips)));

        // Create Hosts
        List<Host> hostList = new ArrayList<>();
        int ramPerHost = 8192; // 8 GB RAM per host
        long storagePerHost = 1_000_000; // 1 TB storage per host
        int bwPerHost = 10000;

        for (int i = 0; i < numVms; i++) {
            Host host = new Host(
                i,
                new RamProvisionerSimple(ramPerHost),
                new BwProvisionerSimple(bwPerHost),
                storagePerHost,
                peList,
                new VmSchedulerTimeShared(peList)
            );
            hostList.add(host);
        }

        // Create Datacenter characteristics
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw
        );

        // Create Datacenter
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datacenter;
    }

    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    private static List<Vm> createVms(int brokerId) {
        List<Vm> vms = new ArrayList<>();
        int mips = 1000;
        long size = 10000; // image size (MB)
        int ram = 1024; // 1 GB RAM per VM
        long bw = 1000;

        for (int i = 0; i < numVms; i++) {
            vms.add(new Vm(i, brokerId, mips, pesNumber, ram, bw, size, "Xen", new CloudletSchedulerTimeShared()));
        }
        return vms;
    }

    private static List<Cloudlet> createCloudlets(int brokerId) {
        List<Cloudlet> cloudlets = new ArrayList<>();
        long length = 4000; // cloudlet length (MI)
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < numCloudlets; i++) {
            Cloudlet cloudlet = new Cloudlet(
                i, length, pesNumber, fileSize, outputSize,
                utilizationModel, utilizationModel, utilizationModel
            );
            cloudlet.setUserId(brokerId);
            cloudlets.add(cloudlet);
        }
        return cloudlets;
    }

    private static void printCloudletList(List<Cloudlet> list) {
        String indent = "    ";
        Log.printLine("\n========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
                      "Datacenter ID" + indent + "VM ID" + indent +
                      "Time" + indent + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (Cloudlet cloudlet : list) {
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);
            if (cloudlet.getStatus().equals(Cloudlet.CloudletStatus.SUCCESS)) {
                Log.print("SUCCESS" + indent + cloudlet.getResourceId() + indent +
                          cloudlet.getVmId() + indent + dft.format(cloudlet.getActualCPUTime()) +
                          indent + dft.format(cloudlet.getExecStartTime()) +
                          indent + dft.format(cloudlet.getFinishTime()));
            }
            Log.printLine();
        }
    }
}