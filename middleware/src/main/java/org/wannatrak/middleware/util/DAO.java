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
 * 12.07.2008 14:25:04
 */
package org.wannatrak.middleware.util;

import org.wannatrak.middleware.entity.UndeletableEntity;
import org.wannatrak.middleware.exception.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

public class DAO {
    private static final String WHERE_NOT_DELETED = "e.deleted <> true";

    private final EntityManager em;

    @NotNull
    public static String createWhereInQueryString(@NotNull List list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("List of \"where in\" is empty");
        }

        final StringBuilder sb = new StringBuilder("(");
        for (Object object : list) {
            sb.append("?,");
        }
        sb.replace(sb.length() - 1, sb.length(), ")");
        return sb.toString();
    }

    public DAO(@NotNull EntityManager em) {
        this.em = em;
    }

    @NotNull
    public <T> T find(@NotNull Class<T> clazz, @NotNull Object o) throws EntityNotFoundException {
        final T t = _find(clazz, o);
        if (UndeletableEntity.class.isAssignableFrom(clazz)) {
            if (BooleanHelper.valueOf(((UndeletableEntity) t).isDeleted())) {
                throw new EntityNotFoundException();
            }
        }
        return t;
    }

    public <T> void persist(@NotNull T t) {
        em.persist(t);
    }

    public <T> void remove(@NotNull T t) {
        if (t instanceof UndeletableEntity) {
            ((UndeletableEntity) t).setDeleted(true);
            merge(t);
        } else {
            _remove(t);
        }
    }

    @NotNull
    public <T> T merge(@NotNull T t) {
        T mergedT = em.merge(t);
        if (mergedT == null) {
            throw new RuntimeException("EntityManager returned null on merge()");
        }
        return mergedT;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <T> T findSingleResult(@NotNull Class<T> clazz, @NotNull String where, @NotNull Object... params)
            throws EntityNotFoundException
    {
        if (UndeletableEntity.class.isAssignableFrom(clazz)) {
            final Query query = createEntityQuery((Class<UndeletableEntity>) clazz, where, params);
            return getSingleResult(clazz, query);
        } else {
            return _findSingleResult(clazz, where, params);
        }
    }

    @NotNull
    public <T> List<T> find(@NotNull Class<T> clazz, int firstResult, int maxResults) {
        if (UndeletableEntity.class.isAssignableFrom(clazz)) {
            final Query query = _createQuery(clazz, "e", WHERE_NOT_DELETED);
            return getResultList(query, firstResult, maxResults);
        } else {
            return _find(clazz, firstResult, maxResults);
        }
    }

    @NotNull
    public <T extends UndeletableEntity> List<T> findOrdered(
            @NotNull Class<T> clazz,
            int firstResult,
            int maxResults,
            @NotNull String where,
            @NotNull String order,
            @NotNull Object... params
    ) {
        final Query query = createEntityOrderQuery(clazz, where, order, params);
        return getResultList(query, firstResult, maxResults);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <T> List<T> find(
            @NotNull Class<T> clazz,
            int firstResult,
            int maxResults,
            @NotNull String where,
            @NotNull Object... params
    ) {
        if (UndeletableEntity.class.isAssignableFrom(clazz)) {
            final Query query = createEntityQuery((Class<UndeletableEntity>) clazz, where, params);
            return getResultList(query, firstResult, maxResults);
        } else {
            return _find(clazz, firstResult, maxResults, where, params);
        }
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <T extends UndeletableEntity> List<T> findOrdered(
            @NotNull Class<T> clazz,
            @NotNull String where,
            @NotNull String order,
            @NotNull Object... params
    ) {
        final Query query = createEntityOrderQuery(clazz, where, order, params);
        return (List<T>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <T> List<T> find(@NotNull Class<T> clazz, @NotNull String where, @NotNull Object... params) {
        if (UndeletableEntity.class.isAssignableFrom(clazz)) {
            final Query query = createEntityQuery((Class< UndeletableEntity>) clazz, where, params);
            return (List<T>) query.getResultList();
        } else {
            return _find(clazz, where, params);
        }
    }

    @NotNull
    public <T extends UndeletableEntity> Query createEntityOrderQuery(
            @NotNull Class<T> clazz,
            @NotNull String where,
            @NotNull String order,
            @NotNull Object... params
    ) {
        return createOrderQuery(clazz, "e", where, order, params);
    }

    @NotNull
    public <T extends UndeletableEntity> Query createEntityQuery(
            @NotNull Class<T> clazz,
            @NotNull String where,
            @NotNull Object... params
    ) {
        return createQuery(clazz, "e", where, params);
    }

    @NotNull
    public <T extends UndeletableEntity> Query createOrderQuery(
            @NotNull Class<T> clazz,
            @NotNull String what,
            @NotNull String where,
            @NotNull String order,
            @NotNull Object... params
    ) {
        final String whereQuery = createWhereQuery(where);
        return _createQuery(
                clazz,
                what,
                whereQuery + " order by " + order,
                params
        );
    }

    @NotNull
    public <T extends UndeletableEntity> Query createQuery(
            @NotNull Class<T> clazz,
            @NotNull String what,
            @NotNull String where,
            @NotNull Object... params
    ) {
        final String whereQuery = createWhereQuery(where);
        return _createQuery(clazz, what, whereQuery, params);
    }

    private String createWhereQuery(String where) {
        String whereQuery = WHERE_NOT_DELETED;
        if (!StringHelper.isAlmostEmpty(where)) {
            whereQuery = whereQuery + " and (" + where + ")";
        }
        return whereQuery;
    }

    @NotNull
    private <T> T _find(@NotNull Class<T> clazz, @NotNull Object o) throws EntityNotFoundException {
        final T t = em.find(clazz, o);
        if (t == null) {
            throw new EntityNotFoundException();
        }
        return t;
    }

    private <T> void _remove(@NotNull T t) {
        em.remove(t);
    }

    @NotNull
    private <T> T _findSingleResult(@NotNull Class<T> clazz, @NotNull String where, @NotNull Object... params)
            throws EntityNotFoundException
    {
        final Query query = _createEntityQuery(clazz, where, params);
        return getSingleResult(clazz, query);
    }

    @NotNull
    private <T> List<T> _find(@NotNull Class<T> clazz, int firstResult, int maxResults) {
        final Query query = em.createQuery("select e from " +  getEntityName(clazz) + " e");
        return getResultList(query, firstResult, maxResults);
    }

    @NotNull
    private <T> List<T> _find(
            @NotNull Class<T> clazz,
            int firstResult,
            int maxResults,
            @NotNull String where,
            @NotNull Object... params
    ) {
        final Query query = _createEntityQuery(clazz, where, params);
        return getResultList(query, firstResult, maxResults);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private <T> List<T> _find(@NotNull Class<T> clazz, @NotNull String where, @NotNull Object... params) {
        final Query query = _createEntityQuery(clazz, where, params);
        return safeGetResultList(query);
    }

    @NotNull
    private <T> Query _createEntityQuery(@NotNull Class<T> clazz, @NotNull String where, @NotNull Object... params) {
        return _createQuery(clazz, "e", where, params);
    }

    @NotNull
    private <T> Query _createQuery(
            @NotNull Class<T> clazz,
            @NotNull String what,
            @NotNull String where,
            @NotNull Object... params
    ) {
        final Query query = em.createQuery(
                "select " + what + " from " + getEntityName(clazz) + " e where " + where
        );
        int j = 1;
        for (Object param : params) {
            if (param instanceof List) {
                for (Object listParam : (List) param) {
                    query.setParameter(j++, listParam);
                }
            } else {
                query.setParameter(j++, param);
            }
        }
        return query;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private <T> T getSingleResult(@NotNull Class<T> clazz, @NotNull Query query) throws EntityNotFoundException {
        List<T> list = (List<T>) query.getResultList();
        if (list == null || list.isEmpty() || list.get(0) == null) {
            throw new EntityNotFoundException();
        }
        return list.get(0);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private <T> List<T> getResultList(@NotNull Query query, int firstResult, int maxResults) {
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return safeGetResultList(query);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private <T> List<T> safeGetResultList(@NotNull Query query) {
        List<T> list = query.getResultList();
        if (list == null) {
            list = Collections.emptyList();
        }
        return list;
    }

    private <T> String getEntityName(@NotNull Class<T> clazz) {
        Entity annotation = clazz.getAnnotation(Entity.class);
        return StringHelper.isEmpty(annotation.name()) ? clazz.getSimpleName() : annotation.name();
    }
}
