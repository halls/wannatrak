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
import org.wannatrak.middleware.exception.EntityNotFoundException;
import org.wannatrak.middleware.exception.LoginFailedException;
import org.wannatrak.middleware.exception.SubjectAlreadyExistsException;
import org.wannatrak.middleware.exception.SendPeriodNotElapsedException;

import java.net.URISyntaxException;
import java.net.URI;

/**
 * Created 13.09.2009 22:34:39
 *
 * @author Andrey Khalzov
 */
@Test
public class TrakApiIntegrationTest {

    private TrakApi trakApi;
    private Api api;

    @BeforeTest
    public void beforeTest() throws URISyntaxException {
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());

        final ClientRequestFactory clientRequestFactory = new ClientRequestFactory(
                new ApacheHttpClientExecutor(
                        new HttpClient(new MultiThreadedHttpConnectionManager())
                ),
//                new URI("http://localhost:9095/api")
                new URI("http://localhost:8080/device/api")
//                new URI("http://www.wannatrak.com/device/api")
        );
        api = clientRequestFactory.createProxy(Api.class);
        trakApi = clientRequestFactory.createProxy(TrakApi.class);
    }

    public void testPost() throws EntityNotFoundException, SendPeriodNotElapsedException {
        trakApi.post(
                "deviceCode",
                new Double[][]{
                        {1d, 2d, 0d, 4d, 5d, 6d},
                        {2d, 2d, 0d, 4d, 5d, 6d},
                        {3d, 2d, 0d, 4d, 5d, 6d}
                }
        );
    }

    public void testList() throws EntityNotFoundException {
        trakApi.list("deviceCode");
    }

    public void testGetInfo() throws EntityNotFoundException {
        trakApi.get("deviceCode");
    }

    public void testGet() throws EntityNotFoundException, LoginFailedException, SubjectAlreadyExistsException {
        String key = api.login(new Api.Login("andrey.khalzov", "1234"));
        TrakApi.Track track = trakApi.list(key)[0];
        Double[][] positions = trakApi.get(key, track.getId(), true);
        System.out.println(track.getName() + ": " + positions.length);

        key = trakApi.continueTrak(key, track.getId());
        trakApi.saveSettings(key, new TrakApi.Settings(track.getName(), 5, 3));
    }

    public void testCreate() throws EntityNotFoundException {
        trakApi.create("deviceCode", "new");
    }

    public void testRemove() throws EntityNotFoundException, LoginFailedException {
        String key = api.login(new Api.Login("andrey.khalzov", "1234"));

        TrakApi.Track track = trakApi.create(key, "new");
        trakApi.remove(key, track.getId());
    }

    public void testContinueTrak() throws EntityNotFoundException {
        try {
            trakApi.continueTrak("deviceCode", 2L);
            Assert.fail();
        } catch (ClientResponseFailure e) {
            Assert.assertEquals(e.getMessage(), "Error status 400 Bad Request returned");
        }
    }

    public void testGetSettings() throws EntityNotFoundException {
        trakApi.getSettings("deviceCode");
    }

    public void testSaveSettings() throws EntityNotFoundException, SubjectAlreadyExistsException {
        trakApi.saveSettings("deviceCode", new TrakApi.Settings("new", 1, 2));
    }
}