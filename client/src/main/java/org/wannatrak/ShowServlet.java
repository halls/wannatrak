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
 * 05.08.2008 15:05:29
 */
package org.wannatrak;

import org.wannatrak.middleware.ejb.PositionWorker;
import org.wannatrak.middleware.ejb.SessionWorker;
import org.wannatrak.middleware.ejb.PositionWorkerLocal;
import org.wannatrak.middleware.entity.Position;
import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.util.ServiceLocator;
import org.wannatrak.middleware.util.UserSession;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.ResourceBundle;

public class ShowServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final PositionWorkerLocal positionWorker = ServiceLocator.lookupLocal(PositionWorker.JNDI_NAME);
        final SessionWorker sessionWorker = ServiceLocator.lookupLocal(SessionWorker.JNDI_NAME);

        try {
            final Long subjectId = Long.parseLong(request.getParameter("subjectId"));
            final String sessionId = request.getParameter("sessionId");
            final Integer hfrom = Integer.parseInt(request.getParameter("hfrom"));
            final Integer mfrom = Integer.parseInt(request.getParameter("mfrom"));
            Integer dfrom = Integer.parseInt(request.getParameter("dfrom"));
            final Integer hto = Integer.parseInt(request.getParameter("hto"));
            final Integer mto = Integer.parseInt(request.getParameter("mto"));
            Integer dto = Integer.parseInt(request.getParameter("dto"));
            final Boolean valid = Boolean.parseBoolean(request.getParameter("valid"));
            final String datetimeFormat = request.getParameter("format").replaceAll("_SPACE_", " ");
            Integer tzOffset = Integer.parseInt(request.getParameter("tzoffset"));
            if (tzOffset == null) {
                tzOffset = 0;
            }

            DateTime currentDateTime = new DateTime().withSecondOfMinute(0).withMillisOfSecond(0);
            final int minutesOfDayWithOffset = currentDateTime.getMinuteOfDay() - tzOffset;
            if (minutesOfDayWithOffset >= DateTimeConstants.MINUTES_PER_DAY) {
                currentDateTime = currentDateTime.plusDays(1);
            } else if (minutesOfDayWithOffset < 0) {
                currentDateTime = currentDateTime.minusDays(1);
            }

            final DateTime dateTimeFrom = currentDateTime
                    .minusDays(dfrom)
                    .withHourOfDay(hfrom)
                    .withMinuteOfHour(mfrom)
                    .plusMinutes(tzOffset);

            Logger.getLogger(getClass()).debug(subjectId);
            Logger.getLogger(getClass()).debug(dateTimeFrom.toString());

            final DateTime dateTimeTo = currentDateTime
                    .minusDays(dto)
                    .withHourOfDay(hto)
                    .withMinuteOfHour(mto)
                    .plusMinutes(tzOffset);

            Logger.getLogger(getClass()).debug(dateTimeTo.toString());

            final StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<kml xmlns=\"http://earth.google.com/kml/2.0\">\n" +
                    "<Document>\n" +
                    "<name>Path</name>\n" +
                    "<Style id=\"style\">\n" +
                    " <LineStyle>\n" +
                    "  <color>ff0000ff</color>\n" +
                    "  <width>1.5</width>\n" +
                    " </LineStyle>\n" +
                    " <PolyStyle>\n" +
                    "  <fill>0</fill>\n" +
                    " </PolyStyle>\n" +
                    "</Style>\n" +
                    "<open>1</open>\n" +
                    "<Placemark>\n" +
                    "<styleUrl>#style</styleUrl>\n" +
                    "<LineString>\n" +
                    "<extrude>0</extrude>\n" +
                    "<altitudeMode>clampToGround</altitudeMode>\n" +
                    "<coordinates>");

            final User user = sessionWorker.getUser(sessionId);
            final List<Position> positions;
            if (user == null) {
                positions = positionWorker.getDemoPositions(subjectId, dateTimeFrom, dateTimeTo, valid);
            } else {
                positions = positionWorker.getPositions(user, subjectId, dateTimeFrom, dateTimeTo, valid);
            }

            Position prevPosition = null;
            for (Iterator<Position> it = positions.iterator(); it.hasNext(); ) {
                final Position position = it.next();
                if (prevPosition != null
                        && position.getLatitude().equals(prevPosition.getLatitude())
                        && position.getLongitude().equals(prevPosition.getLongitude())
                ) {
                    it.remove();
                } else {
                    prevPosition = position;
                }
            }
            for (Position position : positions) {
                sb.append(position.getLongitude()).append(",")
                        .append(position.getLatitude()).append("\n");
            }
            sb.append("</coordinates>\n" +
                    "</LineString>\n" +
                    "</Placemark>\n");

            if (!positions.isEmpty()) {
                addFlag(sb, positions.get(0), tzOffset, datetimeFormat);
                int i = 1;
                for (; i < positions.size() - 1; i++) {
                    if (i % 12 == 0) {
                        addFlag(sb, positions.get(i), tzOffset, datetimeFormat);
                    }
                }
                if (positions.size() > 1) {
                    addFlag(sb, positions.get(i), tzOffset, datetimeFormat);
                }
            }
            sb.append("</Document></kml>");
            final byte[] resultBytes = sb.toString().getBytes("utf-8");
            response.setContentLength(resultBytes.length);
            response.getOutputStream().write(resultBytes);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void addFlag(StringBuilder sb, Position position, int timezoneOffset, String format) {
        final DateTime timestamp = new DateTime(
                position.getGpsTimestamp().getMillis() - timezoneOffset * DateTimeConstants.MILLIS_PER_MINUTE
        );

        final ResourceBundle resourceBundle = ResourceBundle.getBundle(
                "WEB-INF/classes/org/wannatrak/messages",
                UserSession.getLocale()
        );
        sb.append("<Placemark><name>")
                .append(position.getSubject().getName()).append(" ")
                .append(timestamp.toString(format));

        final Double speed = position.getSpeed();
        if (speed != null) {
            sb.append(" ")
                    .append(resourceBundle.getString("details"))
                    .append(" ")
                    .append(String.format("%3.1f", speed))
                    .append(" ")
                    .append(resourceBundle.getString("kmh"));
        }

        final Double altitude = position.getAltitude();
        if (altitude != null) {
            sb.append(", ").append(altitude.intValue()).append(" ").append(resourceBundle.getString("masl"));
        }

        sb.append("</name>\n" +
                        "<IconStyle>\n" +
                        "<Icon>\n" +
                        "<href>http://maps.google.com/mapfiles/kml/pal3/icon61.png</href>\n" +
                        "</Icon>\n" +
                        "</IconStyle>\n" +
                        "<Point><coordinates>\n")
                .append(position.getLongitude()).append(",")
                .append(position.getLatitude()).append("\n")
                .append("</coordinates></Point></Placemark>");
    }
}
