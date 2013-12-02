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
 * 20.04.2009
 */
package org.wannatrak.mobile.view;

import org.wannatrak.mobile.controller.Controller;
import org.wannatrak.mobile.model.Position;
import org.wannatrak.mobile.MessagesBundle;

import javax.microedition.lcdui.*;
import java.util.Date;

public class PositionInfoForm extends Form implements CommandListener {
    private static final MessagesBundle messagesBundle
            = MessagesBundle.getMessageBundle("org.wannatrak.mobile.view.PositionInfoForm");

    private static final String[] CARDINAL_POINTS_ALIASES = {
            messagesBundle.get("N"),
            messagesBundle.get("NE"),
            messagesBundle.get("E"),
            messagesBundle.get("SE"),
            messagesBundle.get("S"),
            messagesBundle.get("SW"),
            messagesBundle.get("W"),
            messagesBundle.get("NW")
    };
    private static final int DEGREES_IN_OCTANT = 360 / 8;

    private final StringItem datetime;
    private final StringItem speed;
    private final StringItem altitude;
    private final StringItem latitude;
    private final StringItem longitude;
    private final StringItem course;
    private final StringItem range;

    private final Command backCommand = new Command(messagesBundle.get("back"), Command.BACK, 1);

    private final Controller controller;

    public PositionInfoForm(Controller controller) {
        super(messagesBundle.get("title"));

        this.controller = controller;

        datetime = new StringItem(messagesBundle.get("datetime"), messagesBundle.get("noData"));
        speed = new StringItem(messagesBundle.get("speed"), messagesBundle.get("noData"));
        altitude = new StringItem(messagesBundle.get("altitude"), messagesBundle.get("noData"));
        latitude = new StringItem(messagesBundle.get("latitude"), messagesBundle.get("noData"));
        longitude = new StringItem(messagesBundle.get("longitude"), messagesBundle.get("noData"));
        course = new StringItem(messagesBundle.get("course"), messagesBundle.get("noData"));
        range = new StringItem(messagesBundle.get("range"), messagesBundle.get("noData"));

        append(datetime);
        append(speed);
        append(altitude);
        append(latitude);
        append(longitude);
        append(course);
        append(range);

        addCommand(backCommand);

        setCommandListener(this);
        controller.setPositionInfoForm(this);

        final Position position = controller.getPosition();
        if (position != null) {
            setNewPosition(position);
        }
    }

    public void commandAction(Command command, Displayable displayable) {
        if (backCommand.equals(command)) {
            controller.showTrakForm();
        }
    }

    public void setNewPosition(Position position) {
        datetime.setText(new Date(position.getTimestamp()).toString());

        final String speedS = Double.toString(position.getSpeed());
        speed.setText(speedS.substring(0, Math.min(5, speedS.length())));

        final String altitudeS = Double.toString(position.getAltitude());
        altitude.setText(altitudeS.substring(0, Math.min(7, altitudeS.length())));

        latitude.setText(Double.toString(position.getLatitude()) + '\u00B0');
        longitude.setText(Double.toString(position.getLongitude()) + '\u00B0');
        course.setText(degreesToTraverse(position.getCourse()));
        range.setText(Integer.toString(position.getRange()));
    }

    private String degreesToTraverse(int degrees) {
        if (degrees >= 360) {
            degrees -= 360;
        }
        final int numberOfOctant = degrees / DEGREES_IN_OCTANT;
        String cardinalPoint = CARDINAL_POINTS_ALIASES[numberOfOctant];
        degrees -= numberOfOctant * DEGREES_IN_OCTANT;
        return cardinalPoint + (degrees > 0 ? " " + degrees + "\u00B0" : "");
    }
}
