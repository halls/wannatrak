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
 * Created 30.05.2009 0:00:34
 *
 * @author Andrey Khalzov
 */
package org.wannatrak.client;

import com.google.gwt.user.client.ui.HTML;

public class CopyrightLabel extends HTML {
    private static CopyrightLabel ourInstance = new CopyrightLabel();

    public static CopyrightLabel getInstance() {
        return ourInstance;
    }

    private CopyrightLabel() {
        super(StringConstants.StringConstantsSingleton.getInstance().copyrightLabel(), false);
        setStylePrimaryName("tm");
    }
}
