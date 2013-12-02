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
 * 29.09.2008 0:37:46
 */
package org.wannatrak.mobile.controller;

public class GeoHelper {
    public static final double DEGREES_IN_RADIANS = 180 / Math.PI;
    public static final double EARTH_RADIUS = 6371 * 1000;
    public static final double PI_2 = Math.PI / 2;
    public static final double PI_6 = Math.PI / 6;
    public static final double SQRT_3 = Math.sqrt(3);

    public static double degreesToRadians(double degrees) {
        return degrees / DEGREES_IN_RADIANS;
    }

    public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
        if (latitude1 == latitude2 && longitude1 == longitude2) {
            return 0;
        }
        longitude1 = degreesToRadians(longitude1);
        latitude1 = degreesToRadians(latitude1);

        longitude2 = degreesToRadians(longitude2);
        latitude2 = degreesToRadians(latitude2);       

        return acos(
                Math.sin(latitude1) *  Math.sin(latitude2)
                        +  Math.cos(latitude1) *  Math.cos(latitude2) *  Math.cos(longitude1 - longitude2)
        ) * EARTH_RADIUS;
    }

    public static double acos(double x) {
        return PI_2 - arcsin(x);
    }

    public static double arctan(double x) {
        int sta = 0;
        int sp = 0;
        double x2;
        double a;

        /* check up the sign change */
        if(x < 0) {
            x = - x;
            sta |= 1;
        }

        /* check up the invertation */
        if(x > 1) {
            x = 1 / x;
            sta |= 2;
        }

        /* process shrinking the domain until x<PI/12 */
        while (x > PI_2) {
            sp++;
            a = x + SQRT_3;
            a = 1 / a;
            x *= SQRT_3;
            x -= 1;
            x *= a;
        }

        /* calculation core */
        x2 = x * x;
        a = x2 + 1.4087812;
        a = 0.55913709 / a;
        a += 0.60310579;
        a -= 0.05160454 * x2;
        a *= x;

        /* process until sp=0 */
        while(sp > 0) {
            a += PI_6;
            sp--;
        }
        /* invertation took place */
        if((sta & 2) > 0) {
            a = PI_2 - a;
        }

        /* sign change took place */
        if((sta & 1) > 0) {
            a = -a;
        }
        return a;
    }

    public static double arcsin(double x) {
        /* check for exceptions */
        if (x <= -1) {
            return -PI_2;
        }
        if (x >= 1) {
            return PI_2;
        }
        /* transform the argument */
        x /= Math.sqrt(1 - x * x);
        return arctan(x);
    }
}
