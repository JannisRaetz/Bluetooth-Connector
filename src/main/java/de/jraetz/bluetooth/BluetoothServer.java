package de.jraetz.bluetooth;

import java.util.ArrayList;
import java.util.Arrays;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;

public class BluetoothServer {

  private static Object lock = new Object();

  public static void main(String[] args) {

    ArrayList<RemoteDevice> devices = new ArrayList<>();

    try {
      // 1
      LocalDevice localDevice = LocalDevice.getLocalDevice();

      // 2
      DiscoveryAgent agent = localDevice.getDiscoveryAgent();

      // 3
      agent.startInquiry(DiscoveryAgent.GIAC, new BluetoothListener(lock));

      try {
        synchronized (lock) {
          lock.wait();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("Device Inquiry Completed. ");

      devices.addAll(Arrays.asList(agent.retrieveDevices(0)));

    } catch (Exception e) {
      e.printStackTrace();
    }

    UUID[] uuidSet = new UUID[1];
    uuidSet[0] = new UUID(0x1105); //OBEX Object Push service

    int[] attrIDs = new int[]{
        0x0100 // Service name
    };

    try {
      LocalDevice localDevice = LocalDevice.getLocalDevice();
      DiscoveryAgent agent = localDevice.getDiscoveryAgent();
      for (RemoteDevice device : devices) {
        agent.searchServices(null, uuidSet, device, new BluetoothListener(lock));
      }

      synchronized (lock) {
        lock.wait();
      }
    } catch (InterruptedException | BluetoothStateException e) {
      e.printStackTrace();
      return;
    }
  }

}
