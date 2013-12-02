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

package org.wannatrak;

import org.wannatrak.middleware.util.ResourceHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created 01.07.2009 21:32:40
 *
 * @author Andrey Khalzov
 */
public class VersionServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String serverName = request.getServerName();
        final String jadFile = ResourceHelper.getResourceForClass(getClass(), "/download/" + serverName + ".jad");
        String version = "";
        for (String jadAttribute : jadFile.split("\n")) {
            if (jadAttribute.startsWith("MIDlet-Version:")) {
                version = jadAttribute.split(":")[1].trim();
            }
        }

        final byte[] resultBytes = version.getBytes("utf-8");
        response.setContentLength(resultBytes.length);
        response.getOutputStream().write(resultBytes);

    }
}
