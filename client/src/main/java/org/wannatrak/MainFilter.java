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
import org.wannatrak.middleware.util.UserSession;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * @author Andrey Khalzov
 */
public class MainFilter implements Filter {
    public static final String GDEBOX = "gdebox";
    public static final String WANNATRAK = "wannatrak";

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException
    {
        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        final HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        httpRequest.setCharacterEncoding("utf-8");

        final String serverName = httpRequest.getServerName();
        final String query = httpRequest.getQueryString();
        final String request = httpRequest.getRequestURI() + (query == null ? "" : "?" + query);

        if (serverName.contains(WANNATRAK) && "/".equals(request)) {
            final byte[] resultBytes = ResourceHelper.getResourceForClass(getClass(), "/index_en.html")
                    .getBytes("utf-8");
            httpResponse.setCharacterEncoding("utf-8");
            httpResponse.setContentLength(resultBytes.length);
            httpResponse.setContentType("text/html");
            httpResponse.getOutputStream().write(resultBytes);
            return;
        }

        if (serverName.contains(GDEBOX)) {
            UserSession.setLocale(new Locale("ru", "RU"));
        } else {
            UserSession.setLocale(Locale.ENGLISH);
        }
        filterChain.doFilter(servletRequest, servletResponse);
        UserSession.reset();
    }

    public void destroy() {
    }
}
