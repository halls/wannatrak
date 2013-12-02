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
package org.wannatrak.device.api;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.Assert;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.wannatrak.middleware.ejb.MockUserWorker;
import org.wannatrak.middleware.ejb.MockSubjectWorker;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created 12.09.2009 23:49:33
 *
 * @author Andrey Khalzov
 */
@Test
public class ApiIntegrationTest {
    private static final String LOGIN = "login";

    private Api api;

    @BeforeTest
    public void beforeTest() throws URISyntaxException {
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());

        final ClientRequestFactory clientRequestFactory = new ClientRequestFactory(
                new ApacheHttpClientExecutor(
                        new HttpClient(new MultiThreadedHttpConnectionManager())
                ),
                new URI("http://localhost:9095/api")
//                new URI("http://localhost:8080/device/api")
        );
        api = clientRequestFactory.createProxy(Api.class);
    }

    public void testLogin() throws Exception {
        Assert.assertEquals(
                api.login(new Api.Login(LOGIN, MockUserWorker.PASSWORD)),
                MockSubjectWorker.DEVICE_CODE
        );

        try {
            api.login(new Api.Login("", "wrong pass"));
            Assert.fail();
        } catch (ClientResponseFailure e) {
            Assert.assertEquals(e.getMessage(), "Error status 403 Forbidden returned");
        }
    }

    public void testLogout() throws Exception {
        api.logout(MockSubjectWorker.DEVICE_CODE);

        try {
            api.logout("wrong device code");
            Assert.fail();
        } catch (ClientResponseFailure e) {
            Assert.assertEquals(e.getMessage(), "Error status 400 Bad Request returned");
        }
    }
}
