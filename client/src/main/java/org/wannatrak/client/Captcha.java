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

package org.wannatrak.client;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Random;

/**
 * @author Andrey Khalzov
 * 22.05.2009 23:57:19
 */
public class Captcha {
    private static final String CAPTCHA_URL = "/verify.png?";

    public static Image createCaptcha() {
        final Image captchaImage = new Image(
                "http://"
                        + Window.Location.getHost()
                        + CAPTCHA_URL
                        + Random.nextInt()
        );
        captchaImage.setStylePrimaryName("captchaRegister");
        return captchaImage;
    }
}
