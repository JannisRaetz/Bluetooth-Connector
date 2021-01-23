package de.jraetz.bluetooth.connection;

import de.jraetz.bluetooth.connection.BluetoothConnector;
import java.io.IOException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.RemoteDevice;
import org.testng.annotations.Test;

public class TestBluetoothConnector {

  private final BluetoothConnector connector;

  public TestBluetoothConnector() throws BluetoothStateException {
    connector = new BluetoothConnector();
  }

  @Test
  public void TestDescovery() throws IOException {
    for(RemoteDevice device : connector.getDiscoveredDevices()) {
      System.out.println(device.getFriendlyName(false));
      System.out.println(device.getBluetoothAddress());
      System.out.println("_________________________________");
    }
  }

  @Test
  public void TestConnection() throws BluetoothStateException {
    connector.connectToDevice("3CDCBC4F7DF5");

  }

}
