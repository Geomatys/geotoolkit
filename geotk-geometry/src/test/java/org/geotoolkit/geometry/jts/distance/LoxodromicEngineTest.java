package org.geotoolkit.geometry.jts.distance;

import java.util.function.ToDoubleBiFunction;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class LoxodromicEngineTest extends EngineTest {

    @Override
    ToDoubleBiFunction<Coordinate, Coordinate> getEngine(CoordinateReferenceSystem target) {
        return new LoxodromicEngine(target);
    }

    @Override
    boolean isOrthodromic() {
        return false;
    }
}
