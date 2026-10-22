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
import static org.geotoolkit.dggs.a5.internal.geometry.SphericalPolygon.pointInSphericalPolygon;
import static org.geotoolkit.dggs.a5.internal.geometry.SphericalPolygon.ringWindingSign;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/geometry/spherical-polygon-primitives.test.ts}.
 * Upstream generates one test per fixture entry; here each describe loops over its
 * fixtures, with the fixture name in assertion messages.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class SphericalPolygonTest {

    private static final JsonNode fixtures = Fixtures.load("fixtures/geometry/spherical-polygon-primitives.json");

    private static final double DEG_TO_RAD = Math.PI / 180;

    private static double[] llToVec(JsonNode ll) {
        final double lat = ll.get(1).asDouble() * DEG_TO_RAD;
        final double lon = ll.get(0).asDouble() * DEG_TO_RAD;
        final double cosLat = Math.cos(lat);
        return new double[] {cosLat * Math.cos(lon), cosLat * Math.sin(lon), Math.sin(lat)};
    }

    private static double[][] ringToVecs(JsonNode ring) {
        final double[][] ringVecs = new double[ring.size()][];
        for (int i = 0; i < ringVecs.length; i++) {
            ringVecs[i] = llToVec(ring.get(i));
        }
        return ringVecs;
    }

    // ---- describe: pointInSphericalPolygon ----
    @Test
    void pointInSphericalPolygon_fixtures() {
        int count = 0;
        for (JsonNode f : fixtures.get("pointInSphericalPolygon")) {
            final String name = f.get("name").asText();
            final double[][] ringVecs = ringToVecs(f.get("ring"));
            for (JsonNode p : f.get("points")) {
                final boolean result = pointInSphericalPolygon(doubles(p.get("vec")), ringVecs);
                assertEquals(p.get("inside").asBoolean(), result, () -> name + " " + p.get("lonLat"));
            }
            count++;
        }
        assertTrue(count > 0);
    }

    // ---- describe: ringWindingSign ----
    @Test
    void ringWindingSign_fixtures() {
        int count = 0;
        for (JsonNode f : fixtures.get("ringWindingSign")) {
            final String name = f.get("name").asText();
            final double[][] ringVecs = ringToVecs(f.get("ring"));
            assertEquals(f.get("sign").asInt(), ringWindingSign(ringVecs), name);
            count++;
        }
        assertTrue(count > 0);
    }
}
