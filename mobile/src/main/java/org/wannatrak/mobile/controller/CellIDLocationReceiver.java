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

package org.wannatrak.mobile.controller;

/**
 * Created 16.10.2009 15:50:15
 *
 * @author Andrey Khalzov
 */
public class CellIDLocationReceiver implements Runnable {

    private final Controller controller;

    public CellIDLocationReceiver(Controller controller) {
        this.controller = controller;
    }

    public void run() {
        while (controller.started) {
            controller.putCellPositionIfTimeToUpdate();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getClass().getName());
            }
        }
    }
}
