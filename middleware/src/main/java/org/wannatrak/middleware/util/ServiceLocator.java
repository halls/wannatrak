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
 * 14.07.2008 23:51:17
 */
package org.wannatrak.middleware.util;

import org.wannatrak.middleware.ejb.AbstractWorkerBean;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class ServiceLocator {
    private static InitialContext localInitialContext;
    private static InitialContext remoteInitialContext;

    @SuppressWarnings("unchecked")
    public static <T> T lookup(Class<T> clazz) {
        final String name = AbstractWorkerBean.JNDI_PREFIX + clazz.getSimpleName();
        try {
            return (T) lookupLocalChecked(name);
        } catch (NamingException e) {
            try {
                return (T) lookupRemoteChecked(name);
            } catch (NamingException e1) {
                throw new RuntimeException("Can't lookup local and remote " + name, e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T lookup(String name) {
        try {
            return (T) lookupLocalChecked(name);
        } catch (NamingException e) {
            try {
                return (T) lookupRemoteChecked(name);
            } catch (NamingException e1) {
                throw new RuntimeException("Can't lookup local and remote " + name, e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T lookupLocal(String name) {
        try {
            return (T) lookupLocalChecked(name);
        } catch (NamingException e) {
            throw new RuntimeException("Can't lookup local " + name, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T lookupRemote(String name) {
        try {
            return (T) lookupRemoteChecked(name);
        } catch (NamingException e) {
            throw new RuntimeException("Can't lookup remote " + name, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T lookupLocalChecked(String name) throws NamingException {
        final String jndiName = name + "/local";
        if (localInitialContext == null) {
            localInitialContext = new InitialContext();
        }
        return (T) localInitialContext.lookup(jndiName);
    }

    @SuppressWarnings("unchecked")
    private static <T> T lookupRemoteChecked(String name) throws NamingException {
        final String jndiName = name + "/remote";
        if (remoteInitialContext == null) {
            final Properties properties = new Properties();
            properties.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
            properties.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
            properties.put("java.naming.provider.url", "localhost:1099");
            properties.put("workdir.suffix", "");
            
            remoteInitialContext = new InitialContext(properties);
        }
        return (T) remoteInitialContext.lookup(jndiName);
    }
}
