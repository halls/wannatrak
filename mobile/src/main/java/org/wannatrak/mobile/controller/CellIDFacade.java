package org.wannatrak.mobile.controller;

import org.wannatrak.mobile.model.Position;
import org.wannatrak.mobile.model.Cell;
import org.wannatrak.mobile.controller.cellid.dbclient.CellDBClient;
import org.wannatrak.mobile.controller.cellid.dbclient.CellDBOrgClient;
import org.wannatrak.mobile.controller.cellid.dbclient.OpenCellIDClient;
import org.wannatrak.mobile.controller.cellid.*;

public class CellIDFacade {

    private final CellDBClient[] dbClients;
    private final CellIDInfoProvider cellIDInfoProvider;
    private final Controller controller;

    public CellIDFacade(Controller controller) {
        this.controller = controller;

        dbClients = new CellDBClient[] {
                new OpenCellIDClient(controller),
                new CellDBOrgClient(controller)
        };

        CellIDInfoProvider defaultCellIDInfoProvider = new DefaultCellIDInfoProvider();
        CellIDInfoProvider motorolaCellIDInfoProvider = new MotorolaCellIDInfoProvider();
        CellIDInfoProvider samsungCellIDInfoProvider = new SamsungCellIDInfoProvider();        
        CellIDInfoProvider nokiaS40CellIDInfoProvider = new NokiaS40CellIDInfoProvider();
        CellIDInfoProvider nokiaS60CellIDInfoProvider = new NokiaS60CellIDInfoProvider();
        CellIDInfoProvider seCellIDInfoProvider = new SonyEricssonCellIDInfoProvider();
        CellIDInfoProvider siemensCellIDInfoProvider = new SiemensCellIDInfoProvider();
        
        if (nokiaS40CellIDInfoProvider.doesWorkForPlatform()) {
            this.cellIDInfoProvider = nokiaS40CellIDInfoProvider;

//            controller.showLog("Nokia S40 Cell ID Provider");
        } else if (nokiaS60CellIDInfoProvider.doesWorkForPlatform()) {
            this.cellIDInfoProvider = nokiaS60CellIDInfoProvider;

//            controller.showLog("Nokia S60 Cell ID Provider");
        } else if (seCellIDInfoProvider.doesWorkForPlatform()) {
            this.cellIDInfoProvider = seCellIDInfoProvider;

//            controller.showLog("Sony Ericsson Cell ID Provider");
        } else if (motorolaCellIDInfoProvider.doesWorkForPlatform()) {
            this.cellIDInfoProvider = motorolaCellIDInfoProvider;

//            controller.showLog("Motorola Cell ID Provider");
        } else if (samsungCellIDInfoProvider.doesWorkForPlatform()) {
            this.cellIDInfoProvider = samsungCellIDInfoProvider;

//            controller.showLog("Samsung Cell ID Provider");
        } else if (defaultCellIDInfoProvider.doesWorkForPlatform()) {
            this.cellIDInfoProvider = defaultCellIDInfoProvider;

//            controller.showLog("Motorola (Old) Cell ID Provider");
        } else if (siemensCellIDInfoProvider.doesWorkForPlatform()) {
            this.cellIDInfoProvider = siemensCellIDInfoProvider;

//            controller.showLog("Siemens Cell ID Provider");
        } else {
            this.cellIDInfoProvider = null;

//            controller.showLog("Cell ID Provider not found");
        }
    }

    public boolean isCellIDAvailable() {
        return cellIDInfoProvider != null;
    }

    public Cell getCell() {
        if (!isCellIDAvailable()) {
            return null;
        }
        return cellIDInfoProvider.getCell();
    }

    public Position getPosition() {
        if (cellIDInfoProvider == null || dbClients == null || dbClients.length == 0) {
            return null;
        }

        final Cell cell = cellIDInfoProvider.getCell();
        if (!controller.setCellIfNew(cell)) {
            return null;
        }

        Position position = null;
        for (int i = 0; i < dbClients.length; i++) {
            position = dbClients[i].getPosition(
                    cell.cellID,
                    cell.mobileCountryCode,
                    cell.mobileNetworkCode,
                    cell.locationAreaCode
            );
            if (position != null) {
                if (position.getLatitude() == 0 && position.getLongitude() == 0) {
                    position = null;
                } else {
                    break;
                }
            }
        }

        return position;
    }

    public void postCellPosition(Cell cell, Position position) {
        for (int i = 0; i < dbClients.length; i++) {
             dbClients[i].postCellPosition(
                    cell.cellID,
                    cell.mobileCountryCode,
                    cell.mobileNetworkCode,
                    cell.locationAreaCode,
                    position.getLatitude(),
                    position.getLongitude(),
                    position.getTimestamp()
            );
        }
    }
}
