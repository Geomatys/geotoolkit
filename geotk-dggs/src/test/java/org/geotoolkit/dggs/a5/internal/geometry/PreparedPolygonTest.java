/**
 * Copyright (C) 2026 Geomatys and A5 contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.dggs.a5.internal.geometry;

import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import static org.geotoolkit.dggs.a5.internal.Fixtures.doubles;
import static org.geotoolkit.dggs.a5.internal.geometry.PreparedPolygon.pointInPreparedPolygon;
import static org.geotoolkit.dggs.a5.internal.geometry.PreparedPolygon.preparePolygon;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Java-only sanity test of {@link PreparedPolygon} (no upstream test file targets
 * {@code geometry/prepared-polygon.ts} directly; it is covered upstream by the regions tests).
 * Reuses the point-in-polygon fixtures of {@code spherical-polygon-primitives.json}.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PreparedPolygonTest {

    private static final JsonNode fixtures = Fixtures.load("fixtures/geometry/spherical-polygon-primitives.json");

    private static double[] llToVec(JsonNode ll) {
        final double lat = Math.toRadians(ll.get(1).asDouble());
        final double lon = Math.toRadians(ll.get(0).asDouble());
        final double cosLat = Math.cos(lat);
        return new double[] {cosLat * Math.cos(lon), cosLat * Math.sin(lon), Math.sin(lat)};
    }

    @Test
    void matches_point_in_spherical_polygon_fixtures() {
        for (JsonNode f : fixtures.get("pointInSphericalPolygon")) {
            final String name = f.get("name").asText();
            final JsonNode ring = f.get("ring");
            final double[][] ringVecs = new double[ring.size()][];
            for (int i = 0; i < ringVecs.length; i++) ringVecs[i] = llToVec(ring.get(i));
            final PreparedPolygon prep = preparePolygon(new double[][][] {ringVecs});
            for (JsonNode p : f.get("points")) {
                assertEquals(p.get("inside").asBoolean(), pointInPreparedPolygon(doubles(p.get("vec")), prep),
                        () -> name + " " + p.get("lonLat"));
            }
        }
    }
}
