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

package org.wannatrak.mobile.view;

import org.wannatrak.mobile.controller.Controller;
import org.wannatrak.mobile.MessagesBundle;
import org.wannatrak.mobile.Executor;
import org.wannatrak.mobile.Locale;
import org.wannatrak.mobile.model.Position;

import javax.microedition.lcdui.*;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.Connector;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.Vector;

/**
 * Created 11.07.2009 23:35:16
 *
 * @author Andrey Khalzov
 */
public class Map extends Canvas implements CommandListener {

    private static final MessagesBundle messagesBundle
            = MessagesBundle.getMessageBundle("org.wannatrak.mobile.view.Map");

    private static final String URL = "http://maps.google.com/staticmap?format=png8&sensor=true&";
    private static final String PATH_DECOR = "&path=rgb%3A0x0000ff,weight%3A3";

    private static final String HYBRID_MAP = "hybrid";
    private static final String TERRAIN_MAP = "terrain";
    private static final String ROAD_MAP = "mobile";
    
    private static final int DIGITS_AFTER_DOT = 7;

    private static final double LATITUDE_DEGREES_IN_PXL[] = new double[] {
            1.3,
            0.35,
            0.14112500,
            0.08556250,
            0.04727500,
            0.02490625,
            0.01284375,
            0.00642500,
            0.00327688,
            0.00164063,
            0.00081625,
            0.00041100,
            0.00020425,
            0.00010212,
            0.00005106,
            0.00002569,
            0.00001277,
            0.00000642,
            0.00000319
    };

    private static final double LONGITUDE_DEGREES_IN_PXL[] = new double[] {
            1.37,
            0.70333333,
            0.34866667,
            0.17425000,
            0.08715000,
            0.04358333,
            0.02160833,
            0.01089417,
            0.00540167,
            0.00272333,
            0.00136192,
            0.00068667,
            0.00034333,
            0.00017167,
            0.00008583,
            0.00004250,
            0.00002127,
            0.00001073,
            0.00000536
    };

    private final Command zoomInCommand = new Command(messagesBundle.get("zoomIn"), Command.SCREEN, 1);
    private final Command zoomOutCommand = new Command(messagesBundle.get("zoomOut"), Command.BACK, 2);
    private final Command roadmapCommand = new Command(messagesBundle.get("roadmap"), Command.SCREEN, 3);
    private final Command satelliteCommand = new Command(messagesBundle.get("satellite"), Command.SCREEN, 4);
    private final Command terrainCommand = new Command(messagesBundle.get(TERRAIN_MAP), Command.SCREEN, 5);
    private final Command updateCommand = new Command(messagesBundle.get("update"), Command.SCREEN, 6);
    private final Command closeCommand = new Command(messagesBundle.get("close"), Command.SCREEN, 7);

    private final Controller controller;

    private Image image;
    private String maptype = ROAD_MAP;
    private int zoom = 14;

    private double centerLatitude = 0;
    private double centerLongitude = 0;
    private int updating;

    public Map(Controller controller) {
        this.controller = controller;

        addCommand(zoomInCommand);
        addCommand(zoomOutCommand);
        addCommand(updateCommand);
        addCommand(closeCommand);

        addCommand(satelliteCommand);
        addCommand(terrainCommand);

        setCommandListener(this);
        controller.setMap(this);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (closeCommand.equals(command)) {
            controller.showTrakForm();
        } else if (zoomInCommand.equals(command)) {
            if (zoom < 18) {
                zoom++;
                update();
            }
        } else if (zoomOutCommand.equals(command)) {
            if (zoom > 0) {
                zoom--;
                update();
            }
        } else if (roadmapCommand.equals(command)) {
            removeCommand(roadmapCommand);

            addCommand(satelliteCommand);
            addCommand(terrainCommand);

            maptype = ROAD_MAP;

            redraw();
        } else if (satelliteCommand.equals(command)) {
            removeCommand(satelliteCommand);

            addCommand(roadmapCommand);
            addCommand(terrainCommand);

            maptype = HYBRID_MAP;

            redraw();
        } else if (terrainCommand.equals(command)) {
            removeCommand(terrainCommand);

            addCommand(roadmapCommand);
            addCommand(satelliteCommand);

            maptype = TERRAIN_MAP;

            redraw();
        } else if (updateCommand.equals(command)) {
            updatePosition();
            update();
        }
    }

    public void updateMap() {
        updatePosition();
        drawMap();
    }

    protected void paint(final Graphics graphics) {
        graphics.drawImage(image, 0, 0, 0);
        if (updating > 0) {
            if (HYBRID_MAP.equals(maptype)) {
                graphics.setGrayScale(255);
            } else {
                graphics.setGrayScale(0);
            }
            graphics.drawString(messagesBundle.get("waiting") + "...", 5, 0, Graphics.TOP | Graphics.LEFT);
        }
    }

    protected void keyPressed(int i) {
        super.keyPressed(i);
        boolean needToUpdate = true;

        switch (i) {
            case KEY_NUM9:
                right();
            case DOWN:
            case -2:
            case KEY_NUM8:
                down();
                break;

            case KEY_NUM1:
                left();
            case UP:
            case -1:
            case KEY_NUM2:
                up();
                break;

            case KEY_NUM7:
                down();
            case LEFT:
            case -3:
            case KEY_NUM4:
                left();
                break;

            case KEY_NUM3:
                up();
            case RIGHT:
            case -4:
            case KEY_NUM6:
                right();
                break;
            case KEY_NUM0:
                zoomOut();
                break;
            case KEY_NUM5:
                zoomIn();
                break;
            case KEY_STAR:
                updatePosition();
                break;
            default:
                needToUpdate = false;
        }
        if (needToUpdate) {
            update();
        }
    }

    protected void pointerPressed(int x, int y) {
        super.pointerPressed(x, y);

        double leftXBound = getWidth() / 3;
        double rightXBound = leftXBound * 2;

        double topYBound = getHeight() / 3;
        double bottomYBound = topYBound * 2;

        if (x < leftXBound) {
            left();
        } else if (x > rightXBound) {
            right();
        }

        boolean needToUpdate = false;
        if (y < topYBound) {
            up();
            needToUpdate = true;
        } else if (y < bottomYBound) {
            down();
            needToUpdate = true;
        }

        if (x >= leftXBound && x <= rightXBound && y >= topYBound && y <= bottomYBound) {
            zoomIn();
            needToUpdate = true;
        }
        if (needToUpdate) {
            update();
        }
    }

    private void redraw() {
        drawMap();
    }

    private void updatePosition() {
        final Position currentPosition = controller.getPosition();
        if (currentPosition != null) {
            centerLatitude = currentPosition.getLatitude();
            centerLongitude = currentPosition.getLongitude();
        }
    }


    private void zoomOut() {
        if (zoom > 0) {
            zoom--;
        }
    }

    private void zoomIn() {
        if (zoom < 18) {
            zoom++;
        }
    }

    private void right() {
        centerLongitude += getLongitudeStep();
    }

    private void left() {
        centerLongitude -= getLongitudeStep();
    }

    private void up() {
        centerLatitude += getLatitudeStep();
    }

    private void down() {
        centerLatitude -= getLatitudeStep();
    }

    private double getLatitudeStep() {
        return getHeight() / 3
                * LATITUDE_DEGREES_IN_PXL[
                    zoom > LATITUDE_DEGREES_IN_PXL.length ? LATITUDE_DEGREES_IN_PXL.length - 1 : zoom
                ];
    }

    private double getLongitudeStep() {
        return getWidth() / 3 
                * LONGITUDE_DEGREES_IN_PXL[
                    zoom > LONGITUDE_DEGREES_IN_PXL.length ? LONGITUDE_DEGREES_IN_PXL.length - 1 : zoom
                ];
    }

    private String getUrl() {
        final StringBuffer sb = new StringBuffer(URL);

        Position currentPosition = controller.getPosition();
        final Vector path = controller.getPath();
        if (currentPosition == null && path != null && path.size() > 0) {
            currentPosition = (Position) path.elementAt(path.size() - 1);
        }

        sb.append("center=").append(coordinateToStr(centerLatitude)).append(",").append(coordinateToStr(centerLongitude))
                .append("&zoom=").append(zoom)
                .append("&size=").append(getWidth()).append("x").append(getHeight())
                .append("&maptype=").append(maptype)
                .append("&key=").append(messagesBundle.get("key"))
                .append("&hl=").append(Locale.get());

        if (path != null && path.size() > 1 && currentPosition != null) {
            sb.append("&markers=")
                    .append(coordinateToStr(currentPosition.getLatitude()))
                    .append(",")
                    .append(coordinateToStr(currentPosition.getLongitude()))
                    .append(",green2");


            final int positionsInPath = (2048 - PATH_DECOR.length() - sb.length()) / 26 - 1;
            int i = path.size() - positionsInPath;
            if (i < 0) {
                i = 0;
            }

            final Position firstPosition = (Position) path.elementAt(i);
            sb.append("%7C")
                    .append(coordinateToStr(firstPosition.getLatitude()))
                    .append(",")
                    .append(coordinateToStr(firstPosition.getLongitude()))
                    .append(",blue1");

            sb.append(PATH_DECOR);
            for (; i < path.size(); i++) {
                final Position position = (Position) path.elementAt(i);
                sb.append("%7C")
                        .append(coordinateToStr(position.getLatitude())).append(",")
                        .append(coordinateToStr(position.getLongitude()));
            }
        } else if (currentPosition != null) {
            sb.append("&markers=")
                    .append(coordinateToStr(currentPosition.getLatitude()))
                    .append(",")
                    .append(coordinateToStr(currentPosition.getLongitude()))
                    .append(",green1");
        }

        return sb.toString();
    }

    private String coordinateToStr(double coordinate) {
        String coordinateStr = String.valueOf(coordinate);
        int dotIndex = coordinateStr.indexOf(".");
        if (dotIndex > 0 && coordinateStr.length() - dotIndex + 1 > DIGITS_AFTER_DOT) {
            coordinateStr = coordinateStr.substring(0, dotIndex + DIGITS_AFTER_DOT);
        }

        return coordinateStr;
    }

    private void update() {
        updating++;
        repaint();

        controller.executeAsynchInSingleThread(new Executor() {
            public void execute() {
                image = getImage();
                updating--;
                if (image != null) {
                    repaint();
                }
            }

            public void stop() {
            }
        });
    }

    private void drawMap() {
        final Executor executor = new Executor() {
            private boolean stopped;

            public void execute() {
                image = getImage();

                if (!stopped) {
                    if (image != null) {
                        repaint();
                    }
                    controller.showMap();
                }
            }

            public void stop() {
                stopped = true;
                controller.showTrakForm();
            }
        };

        controller.showLoading(messagesBundle.get("waiting"), messagesBundle.get("close"), executor);
        controller.executeAsynchInSingleThread(executor);
    }

    private Image getImage() {
        HttpConnection conn = null;
        DataInputStream inputStream = null;
        try {
            final String url = getUrl();
            
            conn = (HttpConnection) Connector.open(url);
            conn.setRequestMethod("GET");

            final int responseCode = conn.getResponseCode();
            if (responseCode == HttpConnection.HTTP_OK) {
                int length = (int) conn.getLength();
                if (length > 0) {
                    inputStream = conn.openDataInputStream();
                    byte[] data = new byte[length];
                    inputStream.readFully(data);
                    return Image.createImage(data, 0, length);
                } else {
                    return null;
                }
            } else {
                throw new RuntimeException("HTTP error: " + responseCode);
            }
        } catch (IOException e) {
            controller.showErrorAlert(e);
            return null;
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            try {
                if (conn!=null) {
                    conn.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
