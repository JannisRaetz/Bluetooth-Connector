package de.jraetz.bluetooth.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;

public class BluetoothServer {

  private static Object lock = new Object();
  private final static ArrayList<RemoteDevice> knownDevices = new ArrayList<>();

  public static void main(String[] args) {
    BluetoothServer server = new BluetoothServer();
    server.initialStart();

    boolean something = true;
    while (something) {
      RemoteDevice newDevice = server.searchForNewDevice();
      server.connectWithNewDevice(newDevice);
    }
  }

  private void connectWithNewDevice(RemoteDevice newDevice) {
  }

  private RemoteDevice searchForNewDevice() {
    return null;
  }

  private void initialStart() {

    ArrayList<RemoteDevice> discoveredDevices = new ArrayList<>();
    try {
      discoveredDevices.addAll(discoverDevices());
    } catch (BluetoothStateException e) {
      e.printStackTrace();
    }

    RemoteDevice selectedDevice = null;
    try {
      selectedDevice = selectDevice(discoveredDevices);
    } catch (IOException e) {
      e.printStackTrace();
    }

    connectToDevice(selectedDevice);

  }

  private void connectToDevice(final RemoteDevice selectedDevice) {
  }

  private RemoteDevice selectDevice(final ArrayList<RemoteDevice> discoveredDevices)
      throws IOException {
    ArrayList<RemoteDevice> trustedDevices = (ArrayList<RemoteDevice>) discoveredDevices.clone();
    trustedDevices.removeIf(remoteDevice -> !remoteDevice.isTrustedDevice());
    return null;
  }

  private List<RemoteDevice> discoverDevices() throws BluetoothStateException {

    ArrayList<RemoteDevice> discoveredDevices = new ArrayList<>();

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

    discoveredDevices.addAll(Arrays.asList(agent.retrieveDevices(0)));

    return discoveredDevices;
  }

  private DiscoveryAgent startInquiry() throws BluetoothStateException {
    LocalDevice localDevice = LocalDevice.getLocalDevice();
    DiscoveryAgent agent = localDevice.getDiscoveryAgent();
    agent.startInquiry(DiscoveryAgent.GIAC, new BluetoothListener(lock));
    try {
      synchronized (lock) {
        lock.wait();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return agent;
  }
}
