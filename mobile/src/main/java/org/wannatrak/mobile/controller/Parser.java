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
 * 28.08.2008 0:05:57
 */
package org.wannatrak.mobile.controller;

import org.wannatrak.mobile.model.Position;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;

public class Parser {

    private Controller controller;
    private double lastAltitude;

    public Parser(Controller controller) {
        this.controller = controller;
    }

    public static boolean isValidNMEASentence(String n) {
		boolean result = false;

        if (n != null && n.length() > 0 && n.charAt(0) == '$'
				&& n.charAt(n.length() - 3) == '*') {
            String checksum = n.substring(n.indexOf('*') + 1, n.length());
            byte cb = Byte.parseByte(checksum, 16);
            byte c = 0;
            byte[] bs = n.getBytes();
            for (int i = 1; i < bs.length - 3; i++) {
				c ^= bs[i];
			}
			if (c == cb) {
				result = true;
			}
		}
		return result;
	}

    public static Vector splitToNMEAVector(String original) {

        Vector nodes = new Vector();
        String separator = "$";

        // Parse nodes into vector
        int index = original.indexOf(separator);
        original =  original.substring(index + separator.length());
        index = original.indexOf(separator);
        while (index >= 0) {

            nodes.addElement(separator + original.substring(0, index));

            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(separator + original);

        return nodes;
    }

    public static boolean trimNmeaString(StringBuffer output) {
        try {
            while (output.charAt(0) < '!' || output.charAt(0) > '~') {
                output.deleteCharAt(0);
            }
            while (output.charAt(output.length() - 1) < '!'
                    || output.charAt(output.length() - 1) > '~') {
                output.deleteCharAt(output.length() - 1);
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    public void handleNmeaString(String nmeaString) {
        if (isValidNMEASentence(nmeaString)) {
            controller.btLog("valid nmea sentence");
            final Position position = parse(nmeaString);
            controller.btLog("nmea parsed");
            if (position != null) {
                controller.btLog("position valid");

                controller.putGPSPosition(position);
            }
        }
    }

    private Position parse(String record){
        record = record.substring(0,record.indexOf('*'));
        if (record.startsWith("$GPRMC")) {
            controller.btLog("GPRMC");
            try{
                return parseGPRMC(record);
            } catch (IndexOutOfBoundsException e){
            }
        } else if (record.startsWith("$GPGGA")) {
            controller.btLog("GPGGA");
            try {
                parseGPGGA(record);
            } catch (IndexOutOfBoundsException e){
            }
        }
        return null;
    }

    private Position parseGPRMC(String record) {

        String[] values = split(record, ",");

        // Date time of fix (eg. 041107.000)
        final String timeOfFix = values[1];
        final String dateOfFix = values[9];

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2000 + Integer.parseInt(dateOfFix.substring(4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateOfFix.substring(2, 4)) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateOfFix.substring(0, 2)));
        calendar.set(Calendar.MILLISECOND, Integer.parseInt(timeOfFix.substring(7, 10)));
        calendar.set(Calendar.SECOND, Integer.parseInt(timeOfFix.substring(4, 6)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeOfFix.substring(2, 4)));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeOfFix.substring(0, 2)));
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        final String warning = values[2];

        final String latitude = values[3];

        final String latitudeDirection = values[4];

        final String longitude = values[5];

        final String longitudeDirection = values[6];

        final double groundSpeed = parseDouble(values[7], 0d);

        final String courseString = values[8];

        double longitudeDouble = 0.0;
        double latitudeDouble = 0.0;
        double speed = -2.0;
        if (longitude.length() > 0 && latitude.length() > 0) {
            longitudeDouble = parseLatLongValue(longitude, true);
            if (longitudeDirection.equals("E") == false) {
                longitudeDouble = -longitudeDouble;
            }

            latitudeDouble = parseLatLongValue(latitude, false);
            if (latitudeDirection.equals("N") == false) {
                latitudeDouble = -latitudeDouble;
            }
        } else {
            controller.btLog("latitude=" + latitude + " longitude=" + longitude);
            return null;
        }

        int course = 0;
        if (courseString.length() > 0) {
            try {
                course = (int) parseDouble(courseString, 180d);
            } catch (Exception e) {
                course = 180;
            }
        }

        if (groundSpeed > 0) {
            speed = groundSpeed * 1.852;

        }
        if (speed < 0) {
            speed = 0;
        }

        return new Position(
                longitudeDouble,
                latitudeDouble,
                speed,
                (short) course,
                lastAltitude,
                calendar.getTime().getTime()
        );
    }

    private void parseGPGGA(String record) {
        String[] values = split(record, ",");
        short isFixed = parseShort(values[6], (short) 0);
//        satelliteCount = parseShort(values[7], (short) 0);

        if (isFixed > 0) {
            lastAltitude = parseDouble(values[9], 0d);
        }

    }

    private String[] split(String original, String separator) {
        Vector nodes = new Vector();

        // Parse nodes into vector
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(original);

        // Create splitted string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++) {
                result[loop] = (String) nodes.elementAt(loop);
            }
        }
        return result;
    }

    private double parseLatLongValue(String valueString, boolean isLongitude) {
        int degreeInteger = 0;
        double minutes = 0.0;
        if (isLongitude) {
            degreeInteger = Integer.parseInt(valueString.substring(0, 3));
            minutes = Double.parseDouble(valueString.substring(3));
        } else {
            degreeInteger = Integer.parseInt(valueString.substring(0, 2));
            minutes = Double.parseDouble(valueString.substring(2));
        }
        double degreeDecimals = minutes / 60.0;
        return degreeInteger + degreeDecimals;
    }

    private double parseDouble(String value, double defaultValue) {
        double parsed = defaultValue;
        if (value != null) {
            try {
                parsed = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                parsed = defaultValue;
            }
        }
        return parsed;
    }

    private short parseShort(String value, short defaultValue) {
        short parsed = defaultValue;
        if (value != null) {
            try {
                parsed = Short.parseShort(value);
            } catch (NumberFormatException e) {
                parsed = defaultValue;
            }
        }
        return parsed;
    }
}
