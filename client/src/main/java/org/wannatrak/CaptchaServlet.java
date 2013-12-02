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
 * 27.12.2008 23:57:39
 */
package org.wannatrak;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

public class CaptchaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        response.setHeader("Pragma", "public");
        response.setHeader("cache-control", "max-age=0,no-cache, no-store,must-revalidate, proxy-revalidate, s-maxage=0");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        final CaptchaGenerator captchaGenerator = new CaptchaGenerator(6);
        captchaGenerator.writeImageToOutputStream(247, 247, 253, 40, 150, response.getOutputStream());

        session.setAttribute("verification.code", captchaGenerator.getVerificationValue());

        response.flushBuffer();
    }
}
