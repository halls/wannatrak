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

package org.wannatrak.middleware.jmx;

import org.testng.annotations.Test;
import org.wannatrak.middleware.ejb.MailServiceBean;
import org.wannatrak.middleware.util.UserSession;
import org.wannatrak.middleware.configuration.ConfigurationContainer;
import org.wannatrak.middleware.configuration.core.exceptions.StoreNotFoundException;
import org.wannatrak.middleware.configuration.core.exceptions.UnknownStoreException;
import org.wannatrak.middleware.configuration.core.exceptions.ValidationException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Locale;

/**
 * Created 20.08.2009 0:28:15
 *
 * @author Andrey Khalzov
 */
@Test(enabled = false)
public class MailingServiceTest {

    public void testDoMailing() throws
            IOException,
            MessagingException,
            IllegalAccessException,
            StoreNotFoundException,
            UnknownStoreException,
            ValidationException
    {
        final MailingServiceImpl mailingService = new MailingServiceImpl();
        mailingService.mailService = new MailServiceBean();
        mailingService.userWorkerLocal = new MockUserWorker();

        System.setProperty("jboss.server.data.dir", "src/test/resources");
        UserSession.setLocale(Locale.ENGLISH);
        ConfigurationContainer.MAIL.init();

        mailingService.doMailing("%wannatrak.com", null, null, "test-mailing.html");
    }
}
