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

import org.jboss.annotation.ejb.Service;
import org.wannatrak.middleware.ejb.UserWorkerLocal;
import org.wannatrak.middleware.ejb.MailService;
import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.util.StringHelper;
import org.wannatrak.middleware.util.UserSession;

import javax.ejb.EJB;
import javax.mail.MessagingException;
import java.io.*;
import java.util.logging.Logger;
import java.util.Locale;

/**
 * Created 19.08.2009 23:43:23
 *
 * @author Andrey Khalzov
 */
@Service(objectName = MailingService.OBJECT_NAME)
public class MailingServiceImpl implements MailingService {

    @EJB
    UserWorkerLocal userWorkerLocal;

    @EJB
    MailService mailService;

    private Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void doMailing(
            String forHostName, String language, String country, String htmlFileName
    ) throws IOException, MessagingException, IllegalAccessException {
        final Locale locale;
        if (StringHelper.isAlmostEmpty(language) || StringHelper.isAlmostEmpty(country)) {
            locale = Locale.ENGLISH;
        } else {
            locale = new Locale(language, country);
        }
        UserSession.setLocale(locale);

        final File file = new File(System.getProperty("jboss.server.data.dir") + "/" + htmlFileName);
        final Reader fileReader = new InputStreamReader(new FileInputStream(file), "utf-8");
        final String mailContent = StringHelper.readToString(fileReader);
        final String subject = mailContent.substring(
                mailContent.indexOf("<title>") + "<title>".length(),
                mailContent.indexOf("</title>")
        );

        for (User user : userWorkerLocal.getUsersByHostNameForMailing(forHostName)) {
            mailService.send(user.getEmail(), subject, mailContent);
            logger.info("Mailing '" + subject + "' for host '" + forHostName + "' sent to " + user.getEmail());
        }
    }
}
