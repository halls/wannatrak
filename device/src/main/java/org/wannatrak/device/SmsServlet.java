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
 * 02.10.2008 1:44:00
 */
package org.wannatrak.device;

import org.wannatrak.middleware.ejb.DeviceSettingsWorker;
import org.wannatrak.middleware.entity.DeviceSettings;
import org.wannatrak.middleware.util.ServiceLocator;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

public class SmsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final DeviceSettingsWorker deviceSettingsWorker = ServiceLocator.lookupLocal(DeviceSettingsWorker.JNDI_NAME);

        final String url = request.getRequestURI();
        int deviceIdIndex = url.lastIndexOf("/") + 1;
        if (deviceIdIndex < 0) {
            response.sendError(404);
        } else if (deviceIdIndex == url.length()) {
            deviceIdIndex = url.lastIndexOf("/", deviceIdIndex - 2) + 1;
        }

        final String deviceId = url.substring(deviceIdIndex, url.length());
        final DeviceSettings deviceSettings = deviceSettingsWorker.getDeviceSettings(deviceId);
        if (deviceSettings != null) {

            Properties props = new Properties();
            props.setProperty("mail.from", "hallzz@yandex.ru");
            props.setProperty("mail.to", deviceSettings.getSmsRecipient() + "@sms.mgsm.ru");
            props.setProperty("mail.debug", "false");
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.subject", request.getParameter("event"));

            javax.mail.Session emailSession = javax.mail.Session.getInstance(props);
            MimeMessage msg = new MimeMessage(emailSession);
            InternetAddress from = null;
            try {
                from = new InternetAddress(props.getProperty("mail.from"));
                InternetAddress to = new InternetAddress(props.getProperty("mail.to"));
                msg.setFrom(from);
                msg.setRecipient(Message.RecipientType.TO, to);
                msg.setSubject("");
                msg.setText(
                        "Speed: " + request.getParameter("speed")
                                + "; Course: " + request.getParameter("course")
                );
                msg.saveChanges();

                Transport tr = emailSession.getTransport("smtp");
                tr.connect("smtp.yandex.ru", "hallzz", "7777755");
                tr.sendMessage(msg, msg.getAllRecipients());
                tr.close();

            } catch (AddressException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        final byte[] resultBytes = "OK".getBytes("utf-8");
        response.setContentLength(resultBytes.length);
        response.getOutputStream().write(resultBytes);

    }
}
