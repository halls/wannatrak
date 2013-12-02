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
 * 18.09.2008 23:34:57
 */
package org.wannatrak.mobile.controller;

import org.wannatrak.mobile.WannatrakMidlet;
import org.wannatrak.mobile.model.Position;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;
import java.io.*;

public class BluetoothGPSReceiver implements Runnable {

    private final Controller controller;
    private String deviceAddress;

    public BluetoothGPSReceiver(Controller controller, String deviceAddress) {
        this.controller = controller;
        this.deviceAddress = deviceAddress;
    }

    public void run() {
        while (controller.started) {
            StreamConnection connection = null;
            Reader reader = null;
            try {
                connection = (StreamConnection) Connector.open("btspp://" + deviceAddress + ":1", Connector.READ);
                reader = new InputStreamReader(connection.openInputStream());
                while (controller.started) {
                    final StringBuffer output = readNmeaString(reader);
                    controller.setGPSReceiverFound(true);

                    btLog("readNmeaString");
                    if (!Parser.trimNmeaString(output)) {
                        continue;
                    }
                    btLog("trimNmeaString");
                    controller.parser.handleNmeaString(output.toString());
                    btLog("handleNmeaString");
               }
            } catch (IOException e) {
                btLog("BluetoothGPSReceiver.run: " + e.getClass().getName());
                controller.setGPSReceiverFound(false);
                pause(1);
            } catch (Exception e) {
                btLog("BluetoothGPSReceiver.run: " + e.getClass().getName());
                controller.setGPSReceiverFound(false);
                pause(1);
            } finally {
                btLog("bt disconnect");
                disconnectBluetooth(connection, reader);
            }
        }
    }

    public void btLog(String message) {
        controller.btLog(message);
    }

    private void signal(Position position) {
        HttpConnection conn = null;
        InputStream dis = null;
        try {
            conn = (HttpConnection) Connector.open(
                    WannatrakMidlet.URL + "sms/" + controller.deviceId
                            + "?event=out"
                            + "&speed=" + position.getSpeed()
                            + "&course=" + position.getCourse()
            );
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "text/plain");

            dis = conn.openInputStream();
            StringBuffer b = new StringBuffer();
            int ch;
            while ((ch = dis.read()) != -1 ) {
                b = b.append((char) ch );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                }
            }
            if (conn!=null) {
                try {
                    conn.close();
                } catch (IOException e) {
                }
            }
        }

    }

    private StringBuffer readNmeaString(Reader reader) throws IOException {
        final StringBuffer output = new StringBuffer();
        int input;
        while ((input = reader.read()) != 13) {
            output.append((char) input);
        }
        return output;
    }

    private void disconnectBluetooth(StreamConnection connection, Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
        btLog("bt reader closed");
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
            }
        }
        btLog("bt conn closed");
    }

    private void pause(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getClass().getName());
        }
    }
}
