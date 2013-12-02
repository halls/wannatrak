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
 * 11.07.2008 2:08:38
 */
package org.wannatrak.middleware.util;

import static org.wannatrak.middleware.util.GeoConstants.*;

import static java.lang.Math.acos;
import static java.lang.Math.sin;
import static java.lang.Math.cos;

public class GeoHelper {

    public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
        longitude1 = degreesToRadians(longitude1);
        latitude1 = degreesToRadians(latitude1);

        longitude2 = degreesToRadians(longitude2);
        latitude2 = degreesToRadians(latitude2);

        return acos(
                sin(latitude1) * sin(latitude2)
                        + cos(latitude1) * cos(latitude2) * cos(longitude1 - longitude2)
        ) * EARTH_RADIUS;
    }

    public static double degreesToRadians(double degrees) {
        return degrees / DEGREES_IN_RADIANS;
    }
}
