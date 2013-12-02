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

package org.wannatrak.mobile.controller.cellid.dbclient;

import org.wannatrak.mobile.model.Position;
import org.wannatrak.mobile.controller.Controller;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import java.io.InputStream;
import java.io.IOException;

/**
 * Created 14.10.2009 22:58:44
 *
 * @author Andrey Khalzov
 */
public class OpenCellIDClient implements CellDBClient {

    private static final String URL = "http://www.opencellid.org/";
    private static final String KEY = "?key=c00efbb0d9536a956e048c6fc1129ec3";

    private final Controller controller;

    public OpenCellIDClient(Controller controller) {
        this.controller = controller;
    }

    public Position getPosition(String cellID, String mcc, String mnc, String lac) {
        try {
            final String url = URL + "cell/get" + KEY +
                            "&mcc=" + mcc +
                            "&mnc=" + mnc +
                            "&cellid=" + cellID +
                            "&lac=" + lac +
                            "&fmt=txt";
            controller.log(url);

            HttpConnection cnx = (HttpConnection) Connector.open(url);
            InputStream is = cnx.openInputStream();

            StringBuffer b = new StringBuffer();
            int car;
            while ((car = is.read()) != -1) {
                b.append((char) car);
            }
            is.close();
            cnx.close();

            String res = b.toString();
            if (res.startsWith("err")) {
                return null;
            } else {
                int pos = res.indexOf(',');
                String latitude = res.substring(0, pos);

                int pos2 = res.indexOf(',', pos + 1);
                String longitude = res.substring(pos + 1, pos2);

                String range = res.substring(pos2 + 1, res.length());

                return new Position(
                        Double.parseDouble(longitude),
                        Double.parseDouble(latitude),
                        0d,
                        (short) 0,
                        0d,
                        System.currentTimeMillis(),
                        Integer.parseInt(range)
                );
            }
        } catch (IOException e) {
            controller.log(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }

    public void postCellPosition(String cellID, String mcc, String mnc, String lac, double latitude, double longitude, long ts) {
        try {
            final String url = URL + "measure/add" + KEY +
                            "&mcc=" + mcc +
                            "&mnc=" + mnc +
                            "&cellid=" + cellID +
                            "&lac=" + lac +
                            "&lat=" + latitude +
                            "&lon=" + longitude;
            controller.log(url);

            HttpConnection cnx = (HttpConnection) Connector.open(url);
            InputStream is = cnx.openInputStream();

            StringBuffer b = new StringBuffer();
            int car;
            while ((car = is.read()) != -1) {
                b.append((char) car);
            }
            is.close();
            cnx.close();
        } catch (IOException e) {
            controller.log(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
