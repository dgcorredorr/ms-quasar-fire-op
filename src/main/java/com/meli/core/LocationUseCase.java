package com.meli.core;

import com.meli.core.entity.Point;
import com.meli.core.entity.Satellite;

public interface LocationUseCase {
    Point getLocation(Satellite[] satellites);
}
