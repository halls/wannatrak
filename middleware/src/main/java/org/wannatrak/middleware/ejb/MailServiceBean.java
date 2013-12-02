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

package org.wannatrak.middleware.ejb;

import org.jetbrains.annotations.NotNull;
import org.wannatrak.middleware.configuration.configurations.MailConfiguration;
import org.wannatrak.middleware.configuration.ConfigurationContainer;

import javax.ejb.Stateless;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;
import java.util.Properties;

/**
 * Created 09.04.2009 19:28:36
 *
 * @author Sergey Grachev
 */
@Stateless(name = MailService.JNDI_NAME)
public class MailServiceBean implements MailService {

    public void send(
            @NotNull final String to, @NotNull final String subject, @NotNull final String body
    ) throws MessagingException, IllegalAccessException {
        final MailConfiguration config = (MailConfiguration) ConfigurationContainer.MAIL.get();
        final Session session;
        if (config.useJBossService()) {
            final String serviceName = config.getJBossJNDIName();
            final InitialContext initialContext;
            try {
                initialContext = new InitialContext();
                session = (Session) initialContext.lookup(serviceName);
            } catch (NamingException e) {
                throw new IllegalAccessException("Mail service not found");
            }
        } else {
            final Properties props = config.getMailProperties();
            session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getUserName(), config.getPassword());
                }
            });
        }

        final MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(config.getFromAddress()));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSentDate(new Date());
        msg.setSubject(subject, "utf-8");
        msg.setText(body, "utf-8", "html");
        msg.setHeader("Content-Transfer-Encoding", "8bit");
        msg.saveChanges();

        final Transport transport = session.getTransport();
        transport.connect();
        transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
        transport.close();
    }
}
