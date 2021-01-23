package de.jraetz.bluetooth.ui;

import de.jraetz.bluetooth.connection.BluetoothConnector;
import java.awt.Dimension;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.RemoteDevice;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class ClientSelectionWindow {

  private final JFrame frame;
  private final JButton reloadButton;
  private final JButton connectButton;
  private final JList<RemoteDevice> devicesList;
  private final BluetoothConnector connector;
  private final JScrollPane listScroller;

  public ClientSelectionWindow() {
    frame = new JFrame();
    reloadButton = new JButton("Reload");
    connectButton = new JButton("Connect");
    devicesList = new JList<>();
    listScroller = new JScrollPane(devicesList);

    connector = new BluetoothConnector();

    reloadButton.setBounds(150, 50, 100, 30);
    reloadButton.addActionListener(e -> {
      try {
        devicesList.setListData(connector.discoverDevices().toArray(new RemoteDevice[0]));
      } catch (BluetoothStateException exception) {
        exception.printStackTrace();
      }
    });

    devicesList.setVisibleRowCount(-1);
    devicesList.setCellRenderer(
        new DefaultListCellRenderer()
    );
    devicesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    devicesList.setLayoutOrientation(JList.VERTICAL);
    devicesList.setListData(connector.getDiscoveredDevices().toArray(new RemoteDevice[0]));
    listScroller.setBounds(25,120, 300, 120);

    connectButton.addActionListener(e -> {
      try {
        connector.connectToDevice(devicesList.getSelectedValue().getBluetoothAddress());
      } catch (BluetoothStateException exception) {
        exception.printStackTrace();
      }
    });
    connectButton.setBounds(150, 400, 100, 30);

    frame.add(reloadButton);
    frame.add(listScroller);
    frame.add(connectButton);
    frame.setLayout(null);
    frame.setSize(400, 500);
    frame.setVisible(true);
  }

//  private class ListReloader {
//
//    private final Object reloaderLock;
//    private final ArrayList<RemoteDevice> remoteDevices;
//    private final JList<RemoteDevice> displayedDevices;
//    private boolean run;
//
//    public ListReloader(Object lock, ArrayList<RemoteDevice> remoteDevices,
//        JList<RemoteDevice> displayedDevices) {
//      this.reloaderLock = lock;
//      this.remoteDevices = remoteDevices;
//      this.displayedDevices = displayedDevices;
//      this.
//          run = true;
//    }
//
//    public void reloadList() throws InterruptedException {
//      while (run) {
//        synchronized (reloaderLock) {
//          reloaderLock.wait();
//        }
//        displayedDevices.setListData(remoteDevices.toArray(new RemoteDevice[0]));
//      }
//    }
//
//    public void setRun(Boolean run) {
//      this.run = run;
//    }
//
//  }
}
