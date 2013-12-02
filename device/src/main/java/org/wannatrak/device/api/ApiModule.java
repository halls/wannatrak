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

import com.google.inject.Module;
import com.google.inject.Binder;
import org.wannatrak.middleware.ejb.PositionWorker;
import org.wannatrak.middleware.ejb.UserWorker;
import org.wannatrak.middleware.ejb.SubjectWorker;
import org.wannatrak.middleware.ejb.TrackingHandler;

/**
 * Created 12.09.2009 22:46:18
 *
 * @author Andrey Khalzov
 */
public class ApiModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(LoginFailedExceptionMapper.class);
        binder.bind(EntityNotFoundExceptionMapper.class);
        binder.bind(SubjectAlreadyExistsExceptionMapper.class);
        binder.bind(SendPeriodNotElapsedExceptionMapper.class);
        binder.bind(Api.class).to(ApiImpl.class);
        binder.bind(TrakApi.class).to(TrakApiImpl.class);
        binder.bind(UserWorker.class).toProvider(new JndiProvider<UserWorker>(UserWorker.class));
        binder.bind(SubjectWorker.class).toProvider(new JndiProvider<SubjectWorker>(SubjectWorker.class));
        binder.bind(TrackingHandler.class).toProvider(new JndiProvider<TrackingHandler>(TrackingHandler.class));
        binder.bind(PositionWorker.class).toProvider(new JndiProvider<PositionWorker>(PositionWorker.class));
    }
}
