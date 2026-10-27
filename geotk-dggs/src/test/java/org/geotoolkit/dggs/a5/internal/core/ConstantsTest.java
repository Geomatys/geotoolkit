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
package org.geotoolkit.dggs.a5.internal.core;

import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseTo;
import static org.geotoolkit.dggs.a5.internal.core.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/constants.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class ConstantsTest {

    private static final JsonNode constantsFixture = Fixtures.load("fixtures/constants.json");

    private static double num(String... path) {
        JsonNode n = constantsFixture;
        for (String p : path) n = n.get(p);
        return n.asDouble();
    }

    // ---- describe: GoldenRatio ----
    @Test
    void has_correct_value() {
        assertCloseTo(num("φ", "expectedValue"), φ, 15);
        assertEquals(num("φ", "value"), φ);
    }

    @Test
    void satisfies_golden_ratio_property_φ_squared_equals_φ_plus_1() {
        assertCloseTo(num("φ", "properties", "goldenRatioPlusOne"), num("φ", "properties", "goldenRatioSquared"), 15);
    }

    @Test
    void satisfies_reciprocal_property_1_over_φ_equals_φ_minus_1() {
        assertCloseTo(num("φ", "properties", "reciprocalMinusOne"), num("φ", "properties", "reciprocal"), 15);
    }

    // ---- describe: AngularConstants ----
    @Test
    void TWO_PI_equals_2π() {
        assertEquals(num("angles", "TWO_PI", "value"), TWO_PI);
        assertCloseTo(num("angles", "TWO_PI", "expectedValue"), TWO_PI, 15);
    }

    @Test
    void TWO_PI_OVER_5_equals_2π_over_5() {
        assertEquals(num("angles", "TWO_PI_OVER_5", "value"), TWO_PI_OVER_5);
        assertCloseTo(num("angles", "TWO_PI_OVER_5", "expectedValue"), TWO_PI_OVER_5, 15);
    }

    @Test
    void PI_OVER_5_equals_π_over_5() {
        assertEquals(num("angles", "PI_OVER_5", "value"), PI_OVER_5);
        assertCloseTo(num("angles", "PI_OVER_5", "expectedValue"), PI_OVER_5, 15);
    }

    @Test
    void PI_OVER_10_equals_π_over_10() {
        assertEquals(num("angles", "PI_OVER_10", "value"), PI_OVER_10);
        assertCloseTo(num("angles", "PI_OVER_10", "expectedValue"), PI_OVER_10, 15);
    }

    @Test
    void angular_relationships() {
        assertCloseTo(2 * PI_OVER_5, TWO_PI_OVER_5, 15);
        assertCloseTo(2 * PI_OVER_10, PI_OVER_5, 15);
    }

    // ---- describe: DodecahedronAngles ----
    @Test
    void dihedral_angle_is_correct() {
        assertEquals(num("dodecahedronAngles", "dihedralAngle", "value"), dihedralAngle);
        assertCloseTo(num("dodecahedronAngles", "dihedralAngle", "expectedValue"), dihedralAngle, 15);
    }

    @Test
    void interhedral_angle_is_correct() {
        assertEquals(num("dodecahedronAngles", "interhedralAngle", "value"), interhedralAngle);
        assertCloseTo(num("dodecahedronAngles", "interhedralAngle", "expectedValue"), interhedralAngle, 15);
    }

    @Test
    void dihedral_and_interhedral_angles_sum_to_π() {
        assertCloseTo(Math.PI, num("dodecahedronAngles", "angleSum"), 15);
    }

    @Test
    void face_edge_angle_is_correct() {
        assertEquals(num("dodecahedronAngles", "faceEdgeAngle", "value"), faceEdgeAngle);
        assertCloseTo(num("dodecahedronAngles", "faceEdgeAngle", "expectedValue"), faceEdgeAngle, 15);
    }

    // ---- describe: DistanceConstants ----
    @Test
    void distance_to_edge_is_correct() {
        assertEquals(num("distances", "distanceToEdge", "value"), distanceToEdge);
        assertCloseTo(num("distances", "distanceToEdge", "expectedValue"), distanceToEdge, 15);
        assertCloseTo(num("distances", "distanceToEdge", "alternativeFormula"), distanceToEdge, 15);
    }

    @Test
    void distance_to_vertex_is_correct() {
        assertEquals(num("distances", "distanceToVertex", "value"), distanceToVertex);
        assertCloseTo(num("distances", "distanceToVertex", "expectedValue"), distanceToVertex, 15);
        assertCloseTo(num("distances", "distanceToVertex", "alternativeFormula"), distanceToVertex, 15);
    }

    // ---- describe: DodecahedronSphereRadii ----
    @Test
    void inscribed_radius_is_1() {
        assertEquals(num("sphereRadii", "Rinscribed", "value"), Rinscribed);
        assertEquals(num("sphereRadii", "Rinscribed", "expectedValue"), Rinscribed);
    }

    @Test
    void midedge_radius_is_correct() {
        assertEquals(num("sphereRadii", "Rmidedge", "value"), Rmidedge);
        assertCloseTo(num("sphereRadii", "Rmidedge", "expectedValue"), Rmidedge, 15);
    }

    @Test
    void circumscribed_radius_is_correct() {
        assertEquals(num("sphereRadii", "Rcircumscribed", "value"), Rcircumscribed);
        assertCloseTo(num("sphereRadii", "Rcircumscribed", "expectedValue"), Rcircumscribed, 15);
    }

    @Test
    void radii_relationships() {
        final JsonNode rel = constantsFixture.get("sphereRadii").get("relationships");
        assertTrue(rel.get("inscribedLessThanMidedge").asBoolean());
        assertTrue(rel.get("midedgeLessThanCircumscribed").asBoolean());
    }

    // ---- describe: MathematicalConsistency ----
    @Test
    void constants_are_finite_numbers() {
        for (JsonNode test : constantsFixture.get("validationTests").get("finiteNumbers")) {
            assertTrue(test.get("isFinite").asBoolean());
            assertFalse(test.get("isNaN").asBoolean());
        }
    }

    @Test
    void positive_constants_are_positive() {
        for (JsonNode test : constantsFixture.get("validationTests").get("positiveConstants")) {
            assertTrue(test.get("isPositive").asBoolean());
        }
    }
}
