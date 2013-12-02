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
 * 05.08.2008 0:33:27
 */
package org.wannatrak.middleware.ejb;

import org.wannatrak.middleware.entity.Position;
import org.wannatrak.middleware.entity.User;
import org.wannatrak.middleware.util.BooleanHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collections;
import java.util.List;

@Stateless(name = PositionWorker.JNDI_NAME)
public class PositionWorkerBean extends AbstractWorkerBean implements PositionWorkerLocal, PositionWorkerRemote {

    @EJB
    private SubjectWorkerLocal subjectWorkerLocal;

    @Nullable
    public Position getLastPosition(User user, @NotNull Long subjectId, Boolean valid) {
        final String validSql = createValidWhereSql(valid);
        if (user != null && subjectWorkerLocal.getSubject(user, subjectId) != null) {
            List<Position> positions = dao.find(
                    Position.class,
                    0,
                    1,
                    "e.subject.id = ? " + validSql + "order by e.gpsTimestamp desc",
                    subjectId
            );
            if (positions.isEmpty()) {
                positions = dao.find(
                    Position.class,
                    0,
                    1,
                    "e.subject.id = ? order by e.gpsTimestamp desc",
                    subjectId
                );
            }
            if (!positions.isEmpty()) {
                return positions.get(0);
            }
        } else {
            List<Position> positions = dao.find(
                    Position.class,
                    0,
                    1,
                    "e.subject.id = ? and (e.subject.hidden is null or e.subject.hidden = false) " + validSql + "order by e.gpsTimestamp desc",
                    subjectId
            );
            if (positions.isEmpty()) {
                positions = dao.find(
                        Position.class,
                        0,
                        1,
                        "e.subject.id = ? and (e.subject.hidden is null or e.subject.hidden = false) order by e.gpsTimestamp desc",
                        subjectId
                );
            }
            if (!positions.isEmpty()) {
                return positions.get(0);
            }
        }
        return null;
    }

    @NotNull
    public List<Position> getPositions(
            @NotNull User user,
            @NotNull Long subjectId,
            @NotNull DateTime from,
            @NotNull DateTime to,
            @NotNull Boolean valid
    ) {
        final String validSql = createValidWhereSql(valid);
        if (subjectWorkerLocal.getSubject(user, subjectId) != null) {
            final List<Position> positions = dao.find(
                    Position.class,
                    0,
                    4000,
                    "e.subject.id = ? " +
                            "and e.gpsTimestamp >= ? and e.gpsTimestamp <= ? " +
                            validSql +
                            "order by e.gpsTimestamp desc",
                    subjectId,
                    from,
                    to
            );
            if (positions.isEmpty()) {
                positions.addAll(
                        dao.find(
                                Position.class,
                                0,
                                1,
                                "e.subject.id = ? and e.gpsTimestamp < ? " +
                                        validSql +
                                        "order by e.gpsTimestamp desc",
                                subjectId,
                                from
                        )
                );
                positions.addAll(
                        dao.find(
                                Position.class,
                                0,
                                1,
                                "e.subject.id = ? and e.gpsTimestamp > ? " +
                                        validSql +
                                        "order by e.gpsTimestamp asc",
                                subjectId,
                                to
                        )
                );
            }

            if (positions.isEmpty()) {
                positions.addAll(
                        dao.find(
                            Position.class,
                            0,
                            4000,
                            "e.subject.id = ? " +
                                    "and e.gpsTimestamp >= ? and e.gpsTimestamp <= ? " +
                                    "order by e.gpsTimestamp desc",
                            subjectId,
                            from,
                            to
                        )
                );
            }
            if (positions.isEmpty()) {
                positions.addAll(
                        dao.find(
                                Position.class,
                                0,
                                1,
                                "e.subject.id = ? and e.gpsTimestamp < ? " +
                                        "order by e.gpsTimestamp desc",
                                subjectId,
                                from
                        )
                );
                positions.addAll(
                        dao.find(
                                Position.class,
                                0,
                                1,
                                "e.subject.id = ? and e.gpsTimestamp > ? " +
                                        "order by e.gpsTimestamp asc",
                                subjectId,
                                to
                        )
                );
            }
            return positions;
        }
        return Collections.emptyList();
     }

    @NotNull
    public List<Position> getDemoPositions(
            @NotNull Long subjectId,
            @NotNull DateTime from,
            @NotNull DateTime to,
            @NotNull Boolean valid
    ) {
        final String validSql = createValidWhereSql(valid);
        final List<Position> positions = dao.find(
                Position.class,
                0,
                4000,
                "e.subject.id = ? and (e.subject.hidden is null or e.subject.hidden = false) " +
                        "and e.gpsTimestamp >= ? and e.gpsTimestamp <= ? " +
                        validSql +
                        "order by e.gpsTimestamp desc",
                subjectId,
                from,
                to
        );
        if (positions.isEmpty()) {
            positions.addAll(
                    dao.find(
                            Position.class,
                            0,
                            1,
                            "e.subject.id = ? and (e.subject.hidden is null or e.subject.hidden = false) and e.gpsTimestamp < ? " +
                                    validSql +
                                    "order by e.gpsTimestamp desc",
                            subjectId,
                            from
                    )
            );
            positions.addAll(
                    dao.find(
                            Position.class,
                            0,
                            1,
                            "e.subject.id = ? and (e.subject.hidden is null or e.subject.hidden = false) and e.gpsTimestamp > ? " +
                                    validSql +
                                    "order by e.gpsTimestamp asc",
                            subjectId,
                            to
                    )
            );
        }
        
        if (positions.isEmpty()) {
            positions.addAll(
                    dao.find(
                        Position.class,
                        0,
                        4000,
                        "e.subject.id = ? and (e.subject.hidden is null or e.subject.hidden = false) " +
                                "and e.gpsTimestamp >= ? and e.gpsTimestamp <= ? " +
                                "order by e.gpsTimestamp desc",
                        subjectId,
                        from,
                        to
                )
            );
        }
        if (positions.isEmpty()) {
            positions.addAll(
                    dao.find(
                            Position.class,
                            0,
                            1,
                            "e.subject.id = ? and (e.subject.hidden is null or e.subject.hidden = false) and e.gpsTimestamp < ? " +
                                    "order by e.gpsTimestamp desc",
                            subjectId,
                            from
                    )
            );
            positions.addAll(
                    dao.find(
                            Position.class,
                            0,
                            1,
                            "e.subject.id = ? and (e.subject.hidden is null or e.subject.hidden = false) and e.gpsTimestamp > ? " +
                                    "order by e.gpsTimestamp asc",
                            subjectId,
                            to
                    )
            );
        }
        return positions;
    }

    private String createValidWhereSql(Boolean valid) {
        return "and (e.longitude <> 0 or e.latitude <> 0) " + 
                (BooleanHelper.valueOf(valid) ? "and e.odometer > 0 " : "and e.valid = true ");
    }
}
