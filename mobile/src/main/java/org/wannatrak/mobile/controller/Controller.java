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
 * 16.08.2008 17:54:38
 */
package org.wannatrak.mobile.controller;

import org.wannatrak.mobile.*;
import org.wannatrak.mobile.controller.exception.LoginException;
import org.wannatrak.mobile.controller.exception.ServerException;
import org.wannatrak.mobile.controller.exception.BluetoothException;
import org.wannatrak.mobile.model.Position;
import org.wannatrak.mobile.model.Cell;
import org.wannatrak.mobile.view.*;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import java.io.*;
import java.util.Vector;

public class Controller {
    public static final String INTERNAL_DEVICE_ADDRESS = "internal";

    public static final int MAX_SEND_PAUSE = 1439;
    public static final int MAX_SAVE_PAUSE = 86399;
    public static final int MAX_POINTS_IN_PATH = 100;

    private static final MessagesBundle messagesBundle
            = MessagesBundle.getMessageBundle("org.wannatrak.mobile.controller.Controller");

    private static final int LOGS_LENGTH = 200;

    final MIDlet midlet;

    private final RecordStore recordStore;

    private final Vector logs = new Vector(LOGS_LENGTH);
    private final Vector btLogs = new Vector(LOGS_LENGTH);

    private final Vector executors = new Vector();
    private final Thread daemonThread;

    boolean started = false;
    private long startedTs;

    boolean positionsUpdated = false;
    Parser parser;
    Position position;
    long devicePositionTimestamp;

    int savePause = 5;
    int sendPause = 1;
    int numOfPointsInPath = 50;

    String deviceId;
    String subjectName;
    String deviceKey;

    String login;
    String password;

    private Cell cell;

    private Vector path;

    private String deviceName;
    private String deviceAddress;

    private LoginForm loginForm;
    private CreateSubjectForm createSubjectForm;
    private UnlinkedSubjectsList unlinkedSubjectsList;
    private TrakForm trakForm;
    private PositionInfoForm positionInfoForm;
    private SettingsForm settingsForm;
    private Map map;
    private CellIDFacade cellIDFacade;

    public Controller(MIDlet midlet) {
        this.midlet = midlet;

        this.parser = new Parser(this);
        try {
            recordStore = RecordStore.openRecordStore("Gdebox", true);
            final int n = PropertyKey.getNumOfPropeties() - recordStore.getNumRecords();
            for (int i = 0; i < n; i++) {
                recordStore.addRecord(new byte[0], 0, 0);
            }
        } catch (RecordStoreException e) {
            throw new RuntimeException(e.getMessage());
        }
        deviceId = getProperty(PropertyKey.ID);
        deviceKey = getProperty(PropertyKey.KEY);
        subjectName = getProperty(PropertyKey.NAME);
        deviceName = getProperty(PropertyKey.DEVICE_NAME);
        deviceAddress = getProperty(PropertyKey.DEVICE_ADDRESS);

        login = getProperty(PropertyKey.LOGIN).trim();
        password = getProperty(PropertyKey.PASSWORD).trim();

        Integer sendPause = getIntProperty(PropertyKey.SEND_PAUSE);
        if (sendPause != null) {
            setSendPause(sendPause.intValue());
        }

        Integer savePause = getIntProperty(PropertyKey.SAVE_PAUSE);
        if (savePause != null) {
            setSavePause(savePause.intValue());
        }

        Integer numOfPointsInPathI = getIntProperty(PropertyKey.POINTS_IN_PATH);
        numOfPointsInPath = numOfPointsInPathI == null ? 100 : numOfPointsInPathI.intValue();
        path = new Vector(numOfPointsInPath);

        daemonThread = new Thread(new Runnable() {
            public void run() {
                while (true) {

                    if (!executors.isEmpty()) {
                        final Executor executor = (Executor) executors.firstElement();
                        executor.execute();
                        executors.removeElement(executor);
                    }

                    if (executors.isEmpty()) {
                        synchronized (Controller.this) {
                            try {
                                Controller.this.wait();
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }
            }
        });
    }

    public Position getPosition() {
        return position;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public boolean hasDeviceId() {
        return deviceId != null && deviceId.length() != 0;
    }

    public boolean hasDeviceAddress() {
        return deviceAddress != null && deviceAddress.length() != 0;
    }

    public boolean isCellIDAvailable() {
        return getCellIDFacade().isCellIDAvailable();
    }

    public boolean isStarted() {
        return started;
    }

    public void stop() {
        started = false;
    }

    public void start() {
        started = true;
        startedTs = System.currentTimeMillis();
    }

    public void setLoginForm(LoginForm loginForm) {
        this.loginForm = loginForm;
    }

    public void setCreateSubjectForm(CreateSubjectForm createSubjectForm) {
        this.createSubjectForm = createSubjectForm;
    }

    public void setUnlinkedSubjectsList(UnlinkedSubjectsList unlinkedSubjectsList) {
        this.unlinkedSubjectsList = unlinkedSubjectsList;
    }

    public void setTrakForm(TrakForm trakForm) {
        this.trakForm = trakForm;
    }

    public void setPositionInfoForm(PositionInfoForm positionInfoForm) {
        this.positionInfoForm = positionInfoForm;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void setSettingsForm(SettingsForm settingsForm) {
        this.settingsForm = settingsForm;
    }

    public boolean setCellIfNew(Cell cell) {
        if (cell == null) {
            return false;
        }

        if (this.cell == null) {
            this.cell = cell;
            return true;
        }

        if (!this.cell.equals(cell)) {
            this.cell = cell;
            return true;
        }

        return false;
    }

    public void logoutAndExit() {
        stop();
        showLoading(MessagesBundle.getGlobal("exit"));

        executeAsynch(new Executor() {
            public void execute() {
                logout();
                midlet.notifyDestroyed();
            }

            public void stop() {
            }
        });
    }

    public void exit() {
        stop();
        midlet.notifyDestroyed();
    }

    public int getSizeAvailable() {
        try {
            return recordStore.getNumRecords();
        } catch (RecordStoreException e) {
            throw new RuntimeException(e.getClass().getName());
        }
    }

    public Integer getIntProperty(PropertyKey propertyKey) {
        DataInputStream dataInputStream = null;
        try {
            final byte[] record = recordStore.getRecord(propertyKey.getNumber());
            if (record == null) {
                return null;
            }
            dataInputStream = new DataInputStream(new ByteArrayInputStream(record));
            return new Integer(dataInputStream.readInt());
        } catch (RecordStoreException e) {
            throw new RuntimeException(e.getClass().getName());
        } catch (IOException e) {
            throw new RuntimeException(e.getClass().getName());
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void setIntProperty(PropertyKey propertyKey, int value) {
        DataOutputStream dataOutputStream = null;
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeInt(value);
            recordStore.setRecord(
                    propertyKey.getNumber(),
                    byteArrayOutputStream.toByteArray(),
                    0,
                    byteArrayOutputStream.size()
            );
        } catch (RecordStoreException e) {
            throw new RuntimeException(e.getClass().getName());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getClass().getName());
        } catch (IOException e) {
            throw new RuntimeException(e.getClass().getName());
        } finally {
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public String getProperty(PropertyKey propertyKey) {
        try {
            final byte[] record = recordStore.getRecord(propertyKey.getNumber());
            if (record == null) {
                return "";
            }
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(record);
            char[] buf = new char[recordStore.getRecordSize(propertyKey.getNumber())];
            new InputStreamReader(byteArrayInputStream, "utf-8").read(buf);
            return String.valueOf(buf);
        } catch (RecordStoreException e) {
            throw new RuntimeException(e.getClass().getName());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getClass().getName());
        } catch (IOException e) {
            throw new RuntimeException(e.getClass().getName());
        }
    }

    public void setProperty(PropertyKey propertyKey, String value) {
        try {
            recordStore.setRecord(propertyKey.getNumber(), value.getBytes("utf-8"), 0, value.getBytes("utf-8").length);
        } catch (RecordStoreException e) {
            throw new RuntimeException(e.getClass().getName());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getClass().getName());
        }
    }


    public Vector getGpsDevices() throws BluetoothException {
        try {
            return BluetoothHelper.getBluetoothDevices();
        } catch (Exception e) {
            btLog("getGpsDevices: " + e.getClass().getName());
            throw new BluetoothException(e.getMessage());
        }
    }

    public long getStartedTs() {
        return startedTs;
    }

    public boolean hasInternalGPS() {
        return System.getProperty("microedition.location.version") != null;
    }

    public void showConnectExternalDeviceForm() {
        Display.getDisplay(midlet).setCurrent(new ConnectExternalDeviceForm(this));
    }

    public void showExternalDevicesList() {
        Display.getDisplay(midlet).setCurrent(new GpsDevicesList(this));
    }

    public void selectDevice(String deviceName, String deviceAddress) {
        setDeviceName(deviceName);
        setDeviceAddress(deviceAddress);

        if (!hasDeviceId()) {
            showCreateSubjectForm();
        } else {
            showTrakForm();
        }
    }

    public void showErrorAlert(IOException e, CommandListener commandListener) {
        showErrorAlert(messagesBundle.get("serverError") + " " + e.getMessage(), commandListener);
    }

    public void showErrorAlert(IOException e) {
        showErrorAlert(messagesBundle.get("serverError") + " " + e.getMessage());
    }

    public void showErrorAlert(String msg) {
        showErrorAlert(msg, null);
    }

    public void showErrorAlert(String msg, CommandListener commandListener) {
        final Alert alert = new Alert(messagesBundle.get("error"), msg, null, AlertType.ERROR);
        if (commandListener != null) {
            alert.setCommandListener(commandListener);
        }
        showAlert(alert);
    }

    public void showCreateSubjectForm() {
        if (createSubjectForm != null) {
            createSubjectForm.reinit();
        }
        Display.getDisplay(midlet).setCurrent(createSubjectForm == null ? new CreateSubjectForm(this) : createSubjectForm);
    }

    public void showUnlinkedSubjectsList() {
        if (unlinkedSubjectsList != null) {
            unlinkedSubjectsList.reinit();
        }
        Display.getDisplay(midlet).setCurrent(
                unlinkedSubjectsList == null ? new UnlinkedSubjectsList(this) : unlinkedSubjectsList
        );
    }

    public void showTrakForm() {
        Display.getDisplay(midlet).setCurrent(trakForm == null ? new TrakForm(this) : trakForm);
        trakForm.show();
        if (hasDeviceId()) {
            setGPSReceiverFound(true);
        }
    }

    public void showLoginForm() {
        if (loginForm != null) {
            loginForm.reinit();
        }
        Display.getDisplay(midlet).setCurrent(loginForm == null ? new LoginForm(this) : loginForm);
    }

    public void showSettingsForm() {
        if (settingsForm != null) {
            settingsForm.init();
        }
        Display.getDisplay(midlet).setCurrent(settingsForm == null ? new SettingsForm(this) : settingsForm);
    }

    public void showAlert(Alert alert) {
        Display.getDisplay(midlet).setCurrent(alert);
    }

    public void showPositionInfoForm() {
        Display.getDisplay(midlet).setCurrent(positionInfoForm == null ? new PositionInfoForm(this) : positionInfoForm);
    }

    public void showLoading(String name, String stopName, Executor executor) {
        Display.getDisplay(midlet).setCurrent(new LoadingForm(name, stopName, executor));
    }

    public void showLoading(String name) {
        Display.getDisplay(midlet).setCurrent(new LoadingForm(name));
    }

    public void hide() {
        Display.getDisplay(midlet).setCurrent(null);
    }

    public void loadAndShowMap() {
        if (map != null) {
            map.updateMap();
        } else {
            new Map(this);
            map.updateMap();
        }
    }

    public void showMap() {
        Display.getDisplay(midlet).setCurrent(map);
    }

    public Display getDisplay() {
        return Display.getDisplay(midlet);
    }

    public void startTrak() {
        start();

        if (trakForm != null) {
            trakForm.updateStatus();
        }

        final Runnable locationReceiver = createLocationReceiver();
        if (locationReceiver != null) {
            new Thread(createLocationReceiver()).start();
            log("GPS Receiving started");
        }

        new Thread(new Runnable() {
            public void run() {
                try {
                    boolean positionNotUpdatedLogged = false;
                    while (started) {
                        try {
                            final Position[] positions;
                            if (positionsUpdated) {
                                log("positions updated");
                                positionNotUpdatedLogged = false;
                                synchronized (Controller.this) {
                                    log("positions mutex");
                                    positions = getPositions(false);
                                    log("got " + positions.length + " positions");
                                    sendPositions(positions);
                                    setPositions(new Position[0]);
                                    log("positions cleaned");
                                    positionsUpdated = false;
                                    log("positions mutex end");
                                }
                            } else {
                                if (isTimeToUpdate() && System.currentTimeMillis() - startedTs > 5000) {
                                    setGPSReceiverFound(false);
                                }
                                if (!positionNotUpdatedLogged) {
                                    log("positions not updated");
                                    positionNotUpdatedLogged = true;
                                }
                                sendPositions(new Position[0]);
                            }
                            saveLogs();
                            sendLogs();
                            initLog();

                            pauseSend();
                        }  catch (IOException e) {
                            log("trak: " + e.getClass().getName() + ": " + e.getMessage());
                            pause(1);
                        } catch (Exception e) {
                            log("trak: " + e.getClass().getName() + ": " + e.getMessage());
                            pause(1);
                        }
                    }
                    if (trakForm != null) {
                        trakForm.updateStatus();
                    }
                } catch (Exception e) {
                    log("trak stopped: " + e.getClass().getName());
                }
            }
        }).start();
        log("GPS Sending started");
    }

    public boolean isTimeToUpdate() {
        return System.currentTimeMillis() - devicePositionTimestamp >= (10 * this.savePause * 1000);
    }

    public CellIDFacade getCellIDFacade() {
        if (cellIDFacade == null) {
            cellIDFacade = new CellIDFacade(this);
        }
        return cellIDFacade;
    }

    public Position getPositionByCellID() {
        return getCellIDFacade().getPosition();
    }

    public void attractAttention() {
        AlertType.ALARM.playSound(getDisplay());
        AlertType.ERROR.playSound(getDisplay());
        AlertType.CONFIRMATION.playSound(getDisplay());
        AlertType.INFO.playSound(getDisplay());
        AlertType.WARNING.playSound(getDisplay());

        vibrate(500);
        flashBackLight(5000);
    }

    public void vibrate(int millis) {
        Display.getDisplay(midlet).vibrate(millis);
    }

    public void flashBackLight(int millis) {
        Display.getDisplay(midlet).flashBacklight(millis);
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        setProperty(PropertyKey.ID, deviceId);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
        setProperty(PropertyKey.KEY, deviceKey);
    }

    public String getDeviceKey() {
        return deviceKey;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
        setProperty(PropertyKey.NAME, subjectName);
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        setProperty(PropertyKey.DEVICE_NAME, deviceName);
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
        setProperty(PropertyKey.DEVICE_ADDRESS, deviceAddress);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
        setProperty(PropertyKey.LOGIN, login);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        setProperty(PropertyKey.PASSWORD, password);
    }

    public void log(String message) {
        /*try {
            message = (position != null ? new Date(position.getTimestamp()).toString() : "null") + ": " + message + "\n";
            synchronized (logs) {
                if (logs.size() >= LOGS_LENGTH) {
                    logs.removeElementAt(0);
                }
                logs.addElement(message);
            }
        } catch (IllegalArgumentException e) {
        }*/
    }

    public void btLog(String message) {
        /*try {
            message = (position != null ? new Date(position.getTimestamp()).toString() : "null") + ": " + message + "\n";
            synchronized (btLogs) {
                if (btLogs.size() >= Controller.LOGS_LENGTH) {
                    btLogs.removeElementAt(0);
                }
                btLogs.addElement(message);
            }
        } catch (IllegalArgumentException e) {
        }*/
    }

    public void saveLogs() {
        /*final StringBuffer logs = new StringBuffer();
        synchronized (this.logs) {
            for (int i = 0; i < this.logs.size(); i++) {
                logs.append(this.logs.elementAt(i));
            }
        }

        final StringBuffer btLogs = new StringBuffer();
        synchronized (this.btLogs) {
            for (int i = 0; i < this.btLogs.size(); i++) {
                btLogs.append(this.btLogs.elementAt(i));
            }
        }
        setProperty(PropertyKey.LOG, logs.toString());
        setProperty(PropertyKey.BT_LOG, btLogs.toString());*/
    }

    public void sendLogs() {
        final String logs = getProperty(PropertyKey.LOG);
        final String btLogs = getProperty(PropertyKey.BT_LOG);

        if (logs != null && btLogs != null) {
            try {
                sendLog(logs + "\nBT:\n" + btLogs);
            } catch (IOException e) {
                throw new RuntimeException(e.getClass().getName());
            }
        } else {
            initLog();
        }
    }

    public void initLog() {
        setProperty(PropertyKey.LOG, "");
        setProperty(PropertyKey.BT_LOG, "");
    }

    public void sendLog(String msg) throws IOException {
        HttpConnection conn = null;
        InputStream dis = null;
        DataOutputStream dos = null;
        try {
            conn = (HttpConnection) Connector.open(WannatrakMidlet.URL + "debug/" + deviceId);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/plain");
            dos = conn.openDataOutputStream();
            dos.writeUTF(msg);
            conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dos!=null) {
                try {
                    dos.close();
                } catch (IOException e) {
                }
            }
            if(dis!=null) {
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

    public void putGPSPosition(Position position) {
        if (isSavingPosition(position)) {
            btLog("saving position");

            putPosition(position);

            final Cell cell = getCellIDFacade().getCell();
            if (setCellIfNew(cell)) {
                getCellIDFacade().postCellPosition(cell, position);
            }
        } else {
            btLog("position is not saving: " + (position.getTimestamp() - this.position.getTimestamp()));
            putCellPositionIfTimeToUpdate();
        }
    }

    public void putCellPositionIfTimeToUpdate() {
        if (isTimeToUpdate()) {
            final Position cellPosition = getPositionByCellID();
            if (cellPosition != null) {
                putPosition(cellPosition);
            }
        }
    }

    public void putPosition(Position position) {
        _putPosition(position);
        addToPath(position);
        devicePositionTimestamp = System.currentTimeMillis();
    }

    public void logout() {
        if (deviceKey == null) {
            return;
        }
        HttpConnection conn = null;
        try {
            conn = (HttpConnection) Connector.open(WannatrakMidlet.URL + "logout?deviceId=" + deviceKey);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "text/plain");

            conn.openInputStream().close();
            setDeviceKey("");
            setDeviceId("");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn!=null) {
                    conn.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String login(String login, String password) throws LoginException, IOException, ServerException {
        HttpConnection conn = null;
        DataOutputStream dos = null;
        InputStream dis = null;
        try {
            conn = (HttpConnection) Connector.open(WannatrakMidlet.URL + "login");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/plain");

            dos = conn.openDataOutputStream();
            dos.writeUTF(login);
            dos.writeUTF(password);

            final int responseCode = conn.getResponseCode();
            if (responseCode == HttpConnection.HTTP_FORBIDDEN) {
                throw new LoginException();
            } else if (responseCode != HttpConnection.HTTP_OK) {
                throw new ServerException(responseCode);
            }

            dis = conn.openInputStream();
            StringBuffer b = new StringBuffer();
            int ch;
            while ( ( ch = dis.read() ) != -1 ) {
                b = b.append( ( char ) ch );
            }
            return b.toString();
        } finally {
            try {
                if(dos != null) {
                    dos.close();
                }
                if(dis != null) {
                    dis.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String unlink() throws IOException, LoginException, ServerException {
        HttpConnection conn = null;
        InputStream dis = null;
        DataOutputStream dos = null;
        try {
            conn = (HttpConnection) Connector.open(
                    WannatrakMidlet.URL
                            + "subjects/unlink?deviceId="
                            + deviceKey
            );
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/plain");

            dos = conn.openDataOutputStream();
            dos.writeUTF(login);
            dos.writeUTF(password);

            final int responseCode = conn.getResponseCode();
            if (responseCode == HttpConnection.HTTP_FORBIDDEN) {
                throw new LoginException();
            } else if (responseCode != HttpConnection.HTTP_OK) {
                throw new ServerException(responseCode);
            }

            dis = conn.openInputStream();
            StringBuffer b = new StringBuffer();
            int ch;
            while ( ( ch = dis.read() ) != -1 ) {
                b = b.append( ( char ) ch );
            }
            return b.toString();
        } finally {
            try {
                if(dos != null) {
                    dos.close();
                }
                if(dis != null) {
                    dis.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getSendPause() {
        return sendPause;
    }

    public void setSendPause(int sendPause) {
        if (sendPause > 0 && sendPause <= MAX_SEND_PAUSE) {
            this.sendPause = sendPause;
        }
    }

    public int getSavePause() {
        return savePause;
    }

    public void setSavePause(int savePause) {
        if (savePause > 0 && savePause <= MAX_SAVE_PAUSE) {
            this.savePause = savePause;
        }
    }

    public void setGPSReceiverFound(boolean gpsReceiverFound) {
        if (trakForm != null) {
            trakForm.setLocationProviderDefined(gpsReceiverFound);
        }
    }

    public void setNumOfPointsInPath(int numOfPointsInPath) {
        final Vector newPath = new Vector(numOfPointsInPath);
        int i = path.size() - numOfPointsInPath;
        if (i < 0) {
            i = 0;
        }
        for (; i < path.size(); i++) {
            newPath.addElement(path.elementAt(i));
        }
        path = newPath;

        this.numOfPointsInPath = numOfPointsInPath;
        setIntProperty(PropertyKey.POINTS_IN_PATH, numOfPointsInPath);
    }

    public int getNumOfPointsInPath() {
        return numOfPointsInPath;
    }

    public void checkNewVersion() {
        String currentVersion = getCurrentVersion();

        if (currentVersion != null && currentVersion.trim().length() > 0
                && !currentVersion.equals(midlet.getAppProperty("MIDlet-Version"))
        ) {
            final Alert alert = new Alert(
                    messagesBundle.get("update"),
                    messagesBundle.get("newVersion") + " " + currentVersion + " " + messagesBundle.get("sureToUpdate"),
                    null,
                    AlertType.CONFIRMATION
            );
            alert.addCommand(new Command(messagesBundle.get("ok"), Command.OK, 1));
            alert.addCommand(new Command(messagesBundle.get("no"), Command.CANCEL, 2));
            alert.setCommandListener(new CommandListener() {
                public void commandAction(Command command, Displayable displayable) {
                    if (command.getCommandType() == Command.OK) {
                        downloadNewVersion();
                        exit();
                    } else {
                        showLoading(
                                MessagesBundle.getGlobal("loading"),
                                MessagesBundle.getGlobal("exit"),
                                new Executor() {
                                    public void execute() {
                                    }

                                    public void stop() {
                                        exit();
                                    }
                                }
                        );
                        synchronized (midlet) {
                            midlet.notify();
                        }
                    }
                }
            });
            showAlert(alert);
            try {
                synchronized (midlet) {
                    midlet.wait();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public Position[] getPositions() {
        return getPositions(false);
    }

    public Vector getPath() {
        return path;
    }

    public synchronized void executeAsynch(Executor executor) {
        executors.addElement(executor);

        if (!daemonThread.isAlive()) {
            daemonThread.start();
        } else {
            notify();
        }
    }

    public void executeAsynchInSingleThread(final Executor executor) {
        new Thread(new Runnable() {
            public void run() {
                executor.execute();
            }
        }).start();
    }

    public void showLog(String log) {
    	if (trakForm != null) {
    		trakForm.showLog(log);
    	}
    }

    Position[] getPositions(boolean withEmpty) {
        final Integer number = getIntProperty(PropertyKey.POSITIONS_NUM);
        if (number == null) {
            return new Position[withEmpty ? 1 : 0];
        }
        return getPositions(getIntProperty(PropertyKey.POSITIONS_NUM).intValue(), withEmpty);
    }

    void setPositions(Position[]  positions) {
        DataOutputStream dataOutputStream = null;
        try {
            int number = positions.length;
            int sizeShortage = Position.SIZE * positions.length - recordStore.getSizeAvailable();
            if (sizeShortage > 0) {
                number -= sizeShortage / Position.SIZE + 1;
            }
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            for (int i = positions.length - number; i < positions.length; i++) {
                positions[i].serialize(dataOutputStream);
            }
            recordStore.setRecord(
                    PropertyKey.POSITIONS.getNumber(),
                    byteArrayOutputStream.toByteArray(),
                    0,
                    byteArrayOutputStream.size()
            );
            setIntProperty(PropertyKey.POSITIONS_NUM, number);
        } catch (RecordStoreException e) {
            throw new RuntimeException(e.getClass().getName());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getClass().getName());
        } catch (IOException e) {
            throw new RuntimeException(e.getClass().getName());
        } finally {
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private synchronized void addToPath(Position position) {
        try {
            if (path.size() >= numOfPointsInPath) {
                path.removeElementAt(0);
            }
            path.addElement(position);
        } catch (IllegalArgumentException e) {
        }
    }

    private String getCurrentVersion() {
        String currentVersion = "";

        HttpConnection conn = null;
        InputStream inputStream = null;

        try {
            conn = (HttpConnection) Connector.open(MessagesBundle.getGlobal("versionUrl"));
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "text/plain");

            final int responseCode = conn.getResponseCode();
            if (responseCode == HttpConnection.HTTP_OK) {
                inputStream = conn.openInputStream();

                StringBuffer version = new StringBuffer();
                int ch;
                while ( ( ch = inputStream.read() ) != -1 ) {
                    version = version.append( ( char ) ch );
                }
                currentVersion = version.toString();
            }
        } catch (IOException e) {
            showErrorAlert(e);
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
        return currentVersion;
    }

    private void downloadNewVersion() {
        try {
            midlet.platformRequest(MessagesBundle.getGlobal("updateUrl"));
        } catch (ConnectionNotFoundException e) {
            showErrorAlert(e);
        }
    }

    private synchronized void _putPosition(Position position) {
        final Position[] positions = getPositions(true);
        positions[positions.length - 1] = position;
        setPositions(positions);
        positionsUpdated = true;
        this.position = position;
        if (positionInfoForm != null) {
            positionInfoForm.setNewPosition(position);
        }
        btLog("position " + positions.length + " added");
    }

    private boolean isSavingPosition(Position position) {
        return this.position == null
                || position.getTimestamp() == 0
                || (position.getTimestamp() - this.position.getTimestamp()) >= (this.savePause * 1000);
    }

    private boolean isDeviceInternal() {
    	return INTERNAL_DEVICE_ADDRESS.equals(deviceAddress);
    }

    private Runnable createLocationReceiver() {
        if (deviceAddress == null) {
            return  getCellIDFacade().isCellIDAvailable() ? new CellIDLocationReceiver(this) : null;
        } else if (isDeviceInternal()) {
            return new InternalLocationReceiver(this);
        } else {
            return new BluetoothGPSReceiver(this, deviceAddress);
        }
    }

    private void sendPositions(final Position[] positions) throws IOException {
        HttpConnection conn = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            conn = (HttpConnection) Connector.open(WannatrakMidlet.URL + "post/" + deviceId);
            log("http con opened");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/plain");
            dos = conn.openDataOutputStream();
            log("http dos opened");
            dos.writeInt(positions.length);
            for (int i = 0; i < positions.length; i++) {
                positions[i].serialize(dos);
            }

            if (conn.getResponseCode() == HttpConnection.HTTP_BAD_REQUEST) {
                relogin();
                return;
            }

            dis = conn.openDataInputStream();

            log("http dis opened");
            final int sendPause = dis.readInt();
            setSendPause(sendPause);

            final int savePause = dis.readInt();
            setSavePause(savePause);
            log("http dis read");

            final String subjectName = dis.readUTF();
            setSubjectName(subjectName);
            if (trakForm != null) {
                trakForm.setName(subjectName);
                trakForm.setServerFound(true);
            }

            log(sendPause + " :sendPause: " + this.sendPause);
            log(savePause + " :savePause: " + this.savePause);
        } catch (IOException e) {
            log(e.getClass().getName());
            if (trakForm != null) {
                trakForm.setServerFound(false);
            }
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                    log("http dos closed");
                } catch (IOException e) {
                }
            }
            if(dis != null) {
                try {
                    dis.close();
                    log("http dis closed");
                } catch (IOException e) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                    log("conn closed");
                } catch (IOException e) {
                }
            }
        }
    }

    private void relogin() {
        stop();
        try {
            final String deviceKey = login(login, password);
            setDeviceKey(deviceKey);
            setDeviceId(deviceId);
            showCreateSubjectForm();
        } catch (Exception e) {
            showLoginForm();
        }
    }

    private Position[] getPositions(int number, boolean withEmpty) {
        DataInputStream dataInputStream = null;
        try {
            final byte[] record;

            record = recordStore.getRecord(PropertyKey.POSITIONS.getNumber());
            if (record == null) {
                return new Position[withEmpty ? 1 : 0];
            }
            final Position[] positions = new Position[number + (withEmpty ? 1 : 0)];
            dataInputStream = new DataInputStream(new ByteArrayInputStream(record));
            for (int i = 0; i < number; i++) {
                final Position position = new Position(dataInputStream);
                positions[i] = position;
            }
            return positions;
        } catch (RecordStoreException e) {
            throw new RuntimeException(e.getClass().getName());
        } catch (IOException e) {
            throw new RuntimeException(e.getClass().getName());
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void pauseSend() {
        pause(sendPause * 60);
    }

    private void pause(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getClass().getName());
        }
    }
}