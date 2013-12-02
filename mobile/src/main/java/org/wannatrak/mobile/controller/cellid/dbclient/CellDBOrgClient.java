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

import javax.microedition.io.HttpConnection;
import javax.microedition.io.Connector;
import java.io.InputStream;
import java.io.IOException;

/**
 * Created 14.10.2009 23:17:59
 *
 * @author Andrey Khalzov
 */
public class CellDBOrgClient implements CellDBClient {

    private static final String URL = "http://celldb.org/api/";
    private static final String ACCOUNT = "username=halls&hash=0ad9eec682c3d5f145db3de655235599";

    private final Controller controller;

    public CellDBOrgClient(Controller controller) {
        this.controller = controller;
    }

    public Position getPosition(String cellID, String mcc, String mnc, String lac) {
        try {
            final String url
                    = URL + "?method=celldb.getcell&" + ACCOUNT +
                            "&mcc=" + mcc +
                            "&mnc=" + mnc +
                            "&cellid=" + cellID +
                            "&lac=" + lac +
                            "&format=csv";
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
            if (res.endsWith("not valid") || res.endsWith("not found")) {
                return null;
            } else {
                int pos = res.indexOf(',');
                pos = res.indexOf(',', pos + 1);
                pos = res.indexOf(',', pos + 1);
                pos = res.indexOf(',', pos + 1);

                //mcc,mnc,lac,cellid,latitude,longitude

                int pos2 = res.indexOf(',', pos + 1);

                String latitude = res.substring(pos + 1, pos2);
                int pos3 = res.indexOf(',', pos2 + 1);
                String longitude = res.substring(pos2 + 1, pos3);

                return new Position(
                        Double.parseDouble(longitude),
                        Double.parseDouble(latitude),
                        0d,
                        (short) 0,
                        0d,
                        System.currentTimeMillis(),
                        1000
                );
            }
        } catch (IOException e) {
            controller.log(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }

    public void postCellPosition(String cellID, String mcc, String mnc, String lac, double latitude, double longitude, long ts) {
        try {
            final String url
                    = URL + "?method=celldb.addcell &" + ACCOUNT +
                            "&mcc=" + mcc +
                            "&mnc=" + mnc +
                            "&cellid=" + cellID +
                            "&lac=" + lac +
                            "&latitude=" + latitude +
                            "&longitude=" + longitude +
                            "&enduserid=" + controller.getLogin();
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
