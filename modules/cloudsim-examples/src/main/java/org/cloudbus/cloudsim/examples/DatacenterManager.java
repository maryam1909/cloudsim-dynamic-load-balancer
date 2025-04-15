package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.provisioners.*;

import java.util.*;

public class DatacenterManager {
    
    public static Datacenter createDatacenter(String name) throws Exception {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();

        int mips = 1000;
        peList.add(new Pe(0, new PeProvisionerSimple(mips)));

        int ram = 2048; 
        long storage = 1_000_000; 
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

        boolean vmCreationSuccessful = false;
        int retries = 0;
        while (!vmCreationSuccessful && retries < 3) {
            try {
                return new Datacenter(name, characteristics,
                        new VmAllocationPolicySimple(hostList), new LinkedList<>(), 10);
            } catch (Exception e) {
                retries++;
                if (retries >= 3) {
                    throw new Exception("Failed to create datacenter after multiple attempts.");
                }
            }
        }
        return null;
    }
}
