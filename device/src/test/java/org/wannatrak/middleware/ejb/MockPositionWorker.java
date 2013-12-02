package org.wannatrak.middleware.ejb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.wannatrak.middleware.entity.Position;
import org.wannatrak.middleware.entity.User;

import java.util.Collections;
import java.util.List;

/**
 * @author Andrey Khalzov
 *         28.04.11 2:18
 */
public class MockPositionWorker implements PositionWorker {

    @NotNull
    public List<Position> getPositions(@NotNull User user, @NotNull Long subjectId, @NotNull DateTime from, @NotNull DateTime to, @NotNull Boolean valid) {
        return Collections.emptyList();
    }

    @Nullable
    public Position getLastPosition(User user, @NotNull Long subjectId, Boolean valid) {
        return new Position();
    }
}
