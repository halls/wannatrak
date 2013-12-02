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
 * 13.07.2008 0:25:39
 */
package org.wannatrak.middleware.ejb;

import org.wannatrak.middleware.util.DAO;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class AbstractWorkerBean {
    public static final String JNDI_PREFIX = "wannatrak/";

    @PersistenceContext(unitName = "wannatrak/persistence")
    protected EntityManager em;

    protected DAO dao;

    @PostConstruct
    public void initDao() {
        dao = new DAO(em);
    }
}
