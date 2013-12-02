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

package org.wannatrak.mobile;

import javax.microedition.lcdui.Image;
import java.io.IOException;

/**
 * Created 27.06.2009 0:33:22
 *
 * @author Andrey Khalzov
 */
public class ImageBundle {

    public static Image getImage(String name, String extension) {
        try {
            return Image.createImage("/img/" + name + Locale.getCurrent() + "." + extension);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
