package de.jraetz.bluetooth;

import java.io.OutputStream;
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
        System.out.println("service " + serviceName.getValue() + " found " + url);
      } else {
        System.out.println("service found " + url);
      }

      if (serviceName.getValue().equals("OBEX Object Push")) {
        sendMessageToDevice(url);
      }
    }

  }

  private static void sendMessageToDevice(String serverURL) {
    try {
      System.out.println("Connecting to " + serverURL);

      ClientSession clientSession = (ClientSession) Connector.open(serverURL);
      HeaderSet hsConnectReply = clientSession.connect(null);
      if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
        System.out.println("Failed to connect");
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

}
