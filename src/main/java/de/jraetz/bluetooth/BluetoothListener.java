package de.jraetz.bluetooth;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

public class BluetoothListener implements DiscoveryListener {

  private final Object mLock;

  public BluetoothListener(Object pLock) {
    mLock = pLock;
  }

  @Override
  public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
    String name;
    try {
      name = btDevice.getFriendlyName(false);
    } catch (Exception e) {
      name = btDevice.getBluetoothAddress();
    }

    System.out.println("device found: " + name);

  }

  @Override
  public void inquiryCompleted(int arg0) {
    synchronized(mLock){
      mLock.notify();
    }
  }

  @Override
  public void serviceSearchCompleted(int arg0, int arg1) {
  }

  @Override
  public void servicesDiscovered(int arg0, ServiceRecord[] arg1) {
  }

}
