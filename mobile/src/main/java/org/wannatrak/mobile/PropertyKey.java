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
 * 16.08.2008 18:33:55
 */
package org.wannatrak.mobile;

public class PropertyKey {
    public static PropertyKey LOGIN;
    public static PropertyKey PASSWORD;
    public static PropertyKey KEY;
    public static PropertyKey ID;
    public static PropertyKey NAME;
    public static PropertyKey DEVICE_NAME;
    public static PropertyKey DEVICE_ADDRESS;
    public static PropertyKey GPS_RAW_DATA;
    public static PropertyKey POSITIONS;
    public static PropertyKey POSITIONS_NUM;
    public static PropertyKey LOG;
    public static PropertyKey BT_LOG;
    public static PropertyKey SEND_PAUSE;
    public static PropertyKey SAVE_PAUSE;
    public static PropertyKey POINTS_IN_PATH;

    private static int numOfPropeties = 0;

    private int number;

    public static void init() {
        LOGIN = new PropertyKey();
        PASSWORD = new PropertyKey();
        KEY = new PropertyKey();
        ID = new PropertyKey();
        NAME = new PropertyKey();
        DEVICE_NAME = new PropertyKey();
        DEVICE_ADDRESS = new PropertyKey();
        GPS_RAW_DATA = new PropertyKey();
        POSITIONS = new PropertyKey();
        POSITIONS_NUM = new PropertyKey();
        LOG = new PropertyKey();
        BT_LOG = new PropertyKey();
        SEND_PAUSE = new PropertyKey();
        SAVE_PAUSE = new PropertyKey();
        POINTS_IN_PATH = new PropertyKey();
    }

    public static int getNumOfPropeties() {
        if (numOfPropeties == 0) {
            init();
        }
        return numOfPropeties;
    }

    public int getNumber() {
        return number;
    }

    private PropertyKey() {
        numOfPropeties++;
        number = numOfPropeties;
    }
}
