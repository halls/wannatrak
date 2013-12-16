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
 * 11.01.2009 1:07:22
 */
package org.wannatrak.client;

import com.google.gwt.maps.client.MapWidget;

import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.layers.KmlLayer;
import com.google.gwt.maps.client.overlay.GeoXmlOverlay;
import com.google.gwt.maps.client.overlay.GeoXmlLoadCallback;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Random;
import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.*;

public class MapController {
    private final Mediator mediator;
    private final MapWidget mapWidget;
    private final Map<Long, KmlLayer> layers;


    public MapController(Mediator mediator, MapWidget mapWidget) {
        this.mediator = mediator;
        this.mapWidget = mapWidget;

        layers = new HashMap<Long, KmlLayer>();
    }

    public void setMapCenter(double latitude, double longitude) {
        final LatLng newCenter = LatLng.newInstance(latitude, longitude);
        mapWidget.setCenter(newCenter);
    }

    public void showSubjects(
            Set<Long> subjects,
            String fromDaysAgo,
            String fromHour,
            String fromMinute,
            String toDaysAgo,
            String toHour,
            String toMinute,
            boolean showErrors,
            boolean updateOnlyNew
    ) {
        final Set<Long> subjectsToShow;
        if (updateOnlyNew) {
            subjectsToShow = new HashSet<Long>(subjects);
            subjectsToShow.removeAll(layers.keySet());

            for (Iterator<Long> it = layers.keySet().iterator(); it.hasNext(); ) {
                Long subjectId = it.next();
                if (!subjects.contains(subjectId)) {
                    layers.get(subjectId).setMap(null);
                    it.remove();
                }
            }
        } else {
            subjectsToShow = subjects;
        }

        for (final Long subjectId : subjectsToShow) {
            mediator.showSublectLoading(subjectId);

            String url = "http://" + Window.Location.getHost()
                    + "/show?subjectId=" + subjectId
                    + "&sessionId=" + Cookies.getCookie("JSESSIONID")
                    + "&hfrom=" + fromHour
                    + "&mfrom=" + fromMinute
                    + "&dfrom=" + fromDaysAgo
                    + "&hto=" + toHour
                    + "&mto=" + toMinute
                    + "&dto=" + toDaysAgo
                    + "&valid=" + !showErrors
                    + "&format=" + DateTimeFormat.getMediumDateTimeFormat().getPattern()
                    .replaceAll(" ", "_SPACE_")
                    + "&tzoffset=" + new Date().getTimezoneOffset()
                    + "&nocache=" + Random.nextInt();
            KmlLayer route = KmlLayer.newInstance(url);
            KmlLayer prevOverlay = layers.get(subjectId);
            if (prevOverlay != null) {
                prevOverlay.setMap(null);
            }
            route.setMap(mapWidget);
            layers.put(subjectId, route);
            mediator.hideSubjectLoading(subjectId);


/*
            GeoXmlOverlay.load(
                    "http://" + Window.Location.getHost()
                            + "/show?subjectId=" + subjectId
                            + "&sessionId=" + Cookies.getCookie("JSESSIONID")
                            + "&hfrom=" + fromHour
                            + "&mfrom=" + fromMinute
                            + "&dfrom=" + fromDaysAgo
                            + "&hto=" + toHour
                            + "&mto=" + toMinute
                            + "&dto=" + toDaysAgo
                            + "&valid=" + !showErrors
                            + "&format=" + DateTimeFormat.getMediumDateTimeFormat().getPattern()
                                                .replaceAll(" ", "_SPACE_")
                            + "&tzoffset=" + new Date().getTimezoneOffset()
                            + "&nocache=" + Random.nextInt(),

                    new GeoXmlLoadCallback() {
                        @Override
                        public void onFailure(String url, Throwable caught) {
                            mediator.hideSubjectLoading(subjectId);
                        }

                        @Override
                        public synchronized void onSuccess(String url, GeoXmlOverlay overlay) {
                            KmlLayer route = KmlLayer.newInstance(url);
                            KmlLayer prevOverlay = layers.get(subjectId);
                            if (prevOverlay != null) {
                                prevOverlay.setMap(null);
                            }
                            route.setMap(mapWidget);
                            layers.put(subjectId, route);
                            mediator.hideSubjectLoading(subjectId);
                        }
                    }
            );
*/
        }
    }
}
