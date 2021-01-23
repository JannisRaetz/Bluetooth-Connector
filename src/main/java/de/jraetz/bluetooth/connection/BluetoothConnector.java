package de.jraetz.bluetooth.connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;

public class BluetoothConnector {

  private final Object lock;
  private final List<RemoteDevice> discoveredDevices;
  private final Logger LOGGER = Logger.getLogger(getClass().getName());
  private DiscoveryAgent discoveryAgent;

  public BluetoothConnector() {
    this.lock = new Object();
    this.discoveredDevices = new ArrayList<>();

    try {
      LocalDevice localDevice = LocalDevice.getLocalDevice();
      discoveryAgent = localDevice.getDiscoveryAgent();
      this.init();
    } catch (BluetoothStateException exception) {
      LOGGER.log(Level.SEVERE, exception.getMessage());
    }
  }

  private void init() throws BluetoothStateException {
    //Collect all reachable devices
    this.discoveredDevices.addAll(discoverDevices());
  }

  public List<RemoteDevice> discoverDevices() throws BluetoothStateException {
    ArrayList<RemoteDevice> discoveredDevices = new ArrayList<>();
    discoveryAgent.startInquiry(DiscoveryAgent.GIAC, new BluetoothListener(lock, LOGGER));
    try {
      synchronized (lock) {
        lock.wait();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    LOGGER.log(Level.INFO, "Device Inquiry Completed. ");
    discoveredDevices.addAll(Arrays.asList(discoveryAgent.retrieveDevices(0)));
    return discoveredDevices;
  }

  public void connectToDevice(String deviceBluetoothAddress) throws BluetoothStateException {
    RemoteDevice remoteDevice = discoveredDevices.stream()
        .filter(device -> device.getBluetoothAddress().equals(deviceBluetoothAddress))
        .collect(Collectors.toList()).get(0);

    UUID[] uuidSet = new UUID[1];
    uuidSet[0] = new UUID(0x1105); //OBEX Object Push service

    int[] attrIDs = new int[]{
        0x0100 // Service name
    };
    discoveryAgent.searchServices(null, uuidSet, remoteDevice, new BluetoothListener(lock, LOGGER));

    try {
      synchronized (lock) {
        lock.wait();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }
  }

  public List<RemoteDevice> getDiscoveredDevices() {
    return discoveredDevices;
  }

  public Object getLock() {
    return lock;
  }

}
