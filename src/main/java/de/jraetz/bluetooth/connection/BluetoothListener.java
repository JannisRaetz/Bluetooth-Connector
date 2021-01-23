package de.jraetz.bluetooth.connection;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;

public class BluetoothListener implements DiscoveryListener {

  private final Object mLock;
  private List<RemoteDevice> mDiscoveredDevices;
  private final Logger LOGGER;

  public BluetoothListener(Object pLock) {
    mLock = pLock;
    mDiscoveredDevices = new ArrayList<>();
    LOGGER = Logger.getLogger(getClass().getName());
  }

  public BluetoothListener(Object pLock, Logger logger) {
    mLock = pLock;
    mDiscoveredDevices = new ArrayList<>();
    LOGGER = logger;
  }

  @Override
  public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
    String name;
    try {
      name = btDevice.getFriendlyName(false);
    } catch (Exception e) {
      name = btDevice.getBluetoothAddress();
    }
    mDiscoveredDevices.add(btDevice);
    //remove duplicates
    mDiscoveredDevices = new ArrayList<>(new HashSet<>(mDiscoveredDevices));
  }

  @Override
  public void inquiryCompleted(int arg0) {
    synchronized(mLock){
      mLock.notify();
    }
  }

  @Override
  public void serviceSearchCompleted(int arg0, int arg1) {
    synchronized (mLock) {
      mLock.notify();
    }
  }

  @Override
  public void servicesDiscovered(int arg0, ServiceRecord[] services) {
    for (int i = 0; i < services.length; i++) {
      String url = services[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
      if (url == null) {
        continue;
      }

      DataElement serviceName = services[i].getAttributeValue(0x0100);
      if (serviceName != null) {
        LOGGER.log(Level.INFO, "service " + serviceName.getValue() + " found " + url);
      } else {
        LOGGER.log(Level.INFO, "service found " + url);
      }

      sendMessageToDevice(url);

    }

  }

  private void sendMessageToDevice(String serverURL) {
    try {
      LOGGER.log(Level.INFO, "Connecting to " + serverURL);

      ClientSession clientSession = (ClientSession) Connector.open(serverURL);
      HeaderSet hsConnectReply = clientSession.connect(null);
      if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
        LOGGER.log(Level.INFO, "Failed to connect");
        return;
      }

      HeaderSet hsOperation = clientSession.createHeaderSet();
      hsOperation.setHeader(HeaderSet.NAME, "Hello.txt");
      hsOperation.setHeader(HeaderSet.TYPE, "text");

      //Create PUT Operation
      Operation putOperation = clientSession.put(hsOperation);

      // Sending the message
      byte data[] = "Hello World !!!".getBytes("iso-8859-1");
      OutputStream os = putOperation.openOutputStream();
      os.write(data);
      os.close();

      putOperation.close();
      clientSession.disconnect(null);
      clientSession.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public List<RemoteDevice> getDiscoveredDevices() {
    return mDiscoveredDevices;
  }
}
