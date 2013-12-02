/*
 * Copyright 2009 Andrey Khalzov, and individual contributors as indicated by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

/**
 * Created by Andrey Khalzov
 * 20.08.2008 23:46:13
 */
package org.wannatrak.mobile.controller;

import javax.bluetooth.*;
import java.util.Vector;
import java.io.IOException;

public class BluetoothHelper {

    public static Vector getBluetoothDevices() throws BluetoothStateException, InterruptedException {
        final LocalDevice localDevice = LocalDevice.getLocalDevice();
        localDevice.setDiscoverable(DiscoveryAgent.GIAC);
        final DiscoveryAgent discoveryAgent = localDevice.getDiscoveryAgent();
        final BluetoothDiscoveryListener discoveryListener = new BluetoothDiscoveryListener();
        discoveryAgent.startInquiry(DiscoveryAgent.GIAC, discoveryListener);
        synchronized (discoveryListener) {
            if (! discoveryListener.isCompleted()) {
                discoveryListener.wait(15000);
            }
        }
        return discoveryListener.getDevices();
    }

    static class BluetoothDiscoveryListener implements DiscoveryListener {

        private boolean completed = false;
        private Vector devices = new Vector();

        public boolean isCompleted() {
            return completed;
        }

        public Vector getDevices() {
            return devices;
        }

        public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
            String address = remoteDevice.getBluetoothAddress();
            String name = null;
            try {
                name = remoteDevice.getFriendlyName(false);
            } catch (IOException e) {}

            if (name == null || name.trim().length() == 0) {
                try {
                    name = remoteDevice.getFriendlyName(true);
                } catch (IOException e) {}
                if (name == null || name.trim().length() == 0) {
                    name = address;
                }
            }

            BluetoothDevice device = new BluetoothDevice(name, address);
            devices.addElement(device);
        }

        public void servicesDiscovered(int i, ServiceRecord[] serviceRecords) {
        }

        public void serviceSearchCompleted(int i, int i1) {
        }

        public synchronized void inquiryCompleted(int i) {
            notify();
            completed = true;
        }
    }

}
