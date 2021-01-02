package de.jraetz.bluetooth;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;

public class BluetoothServer {

  private static Object lock = new Object();

  public static void main(String[] args) {

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

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
