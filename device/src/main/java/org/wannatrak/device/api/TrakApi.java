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

import org.wannatrak.middleware.exception.EntityNotFoundException;
import org.wannatrak.middleware.exception.SubjectAlreadyExistsException;
import org.wannatrak.middleware.exception.SendPeriodNotElapsedException;

import javax.ws.rs.*;

/**
 * Created 13.09.2009 21:55:17
 *
 * @author Andrey Khalzov
 */
@Path("/trak")
public interface TrakApi {

    @GET
    @Path("/{deviceKey}/{trakId}/{includeNoise}")
    @Produces("text/json")
    Double[][] get(
            @PathParam("deviceKey") String deviceKey,
            @PathParam("trakId") Long id,
            @PathParam("includeNoise") Boolean includeNoise
    ) throws EntityNotFoundException;

    @GET
    @Path("/{deviceKey}/{trakId}/{from}/{to}/{includeNoise}")
    @Produces("text/json")
    Double[][] get(
            @PathParam("deviceKey") String deviceKey,
            @PathParam("trakId") Long id,
            @PathParam("from") Long from,
            @PathParam("to") Long to,
            @PathParam("includeNoise") Boolean includeNoise
    ) throws EntityNotFoundException;

    @POST
    @Path("/{deviceKey}")
    @Consumes("text/json")
    void post(@PathParam("deviceKey") String deviceKey, Double[][] trak)
            throws EntityNotFoundException, SendPeriodNotElapsedException;

    @GET
    @Path("/get/{deviceKey}")
    @Produces("text/json")
    Track get(@PathParam("deviceKey") String deviceKey) throws EntityNotFoundException;

    @GET
    @Path("/list/{deviceKey}")
    @Produces("text/json")
    Track[] list(@PathParam("deviceKey") String deviceKey) throws EntityNotFoundException;

    @GET
    @Path("/create/{deviceKey}/{trakName}")
    @Produces("text/json")
    Track create(@PathParam("deviceKey") String deviceKey, @PathParam("trakName") String name) throws EntityNotFoundException;

    @GET
    @Path("/continue/{deviceKey}/{trakId}")
    String continueTrak(@PathParam("deviceKey") String deviceKey, @PathParam("trakId") Long id) throws EntityNotFoundException;

    @GET
    @Path("/settings/{deviceKey}")
    @Produces("text/json")
    Settings getSettings(@PathParam("deviceKey") String deviceKey) throws EntityNotFoundException;

    @POST
    @Path("/settings/{deviceKey}")
    @Consumes("text/json")
    void saveSettings(@PathParam("deviceKey") String deviceKey, Settings settings)
            throws SubjectAlreadyExistsException, EntityNotFoundException;

    @DELETE
    @Path("/remove/{deviceKey}/{trakId}")
    void remove(@PathParam("deviceKey") String deviceKey, @PathParam("trakId") Long id) throws EntityNotFoundException;

    static class Track {
        public Long id;
        public String name;

        public Track() {
        }

        public Track(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class Settings {
        public String name;
        public Integer savePeriod;
        public Integer sendPeriod;

        public Settings() {
        }

        public Settings(String name, Integer savePeriod, Integer sendPeriod) {
            this.name = name;
            this.savePeriod = savePeriod;
            this.sendPeriod = sendPeriod;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getSavePeriod() {
            return savePeriod;
        }

        public void setSavePeriod(Integer savePeriod) {
            this.savePeriod = savePeriod;
        }

        public Integer getSendPeriod() {
            return sendPeriod;
        }

        public void setSendPeriod(Integer sendPeriod) {
            this.sendPeriod = sendPeriod;
        }
    }
}
