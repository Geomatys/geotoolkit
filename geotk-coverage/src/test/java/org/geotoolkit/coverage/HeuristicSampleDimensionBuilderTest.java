/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.coverage;

import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.measure.Units;
import org.junit.Test;

import static org.junit.Assert.fail;


/**
 * Test {@link HeuristicSampleDimensionBuilder}.
 *
 * <p>Ported from <a href="https://github.com/apache/sis/pull/24">SIS pull request #24</a></p>
 *
 * @author Alexis Manin (Geomatys)
 */
public class HeuristicSampleDimensionBuilderTest {
    /**
     * When user does not explicitly define a background category, ensure that we promote another
     * category as background if and only if it matches <em>all</em> of the following requirements:
     * <ul>
     *   <li>Is qualitative</li>
     *   <li>Named background, fill-value or no-data</li>
     *   <li>has a valid minimum value</li>
     * </ul>
     */
    @Test
    public void testDefaultBackground() {
        SampleDimension s = new HeuristicSampleDimensionBuilder()
                .addQuantitative("background", 0f, 0.99f, Units.UNITY)
                .addQuantitative("no-data", 1f, 1.99f, Units.UNITY)
                .addQuantitative("fill-value", 2f, 3f, Units.UNITY)
                .build();
        Number n;
        s.getBackground().ifPresent(bg
                -> fail("No quantitative category should be promoted as background value, but background is defined: "+bg));

        s = new HeuristicSampleDimensionBuilder()
                .addQualitative("background", 1, 2)
                .build();
        n = s.getBackground()
                .filter(it -> it.intValue() == 1)
                .orElseThrow(() -> new AssertionError("No background value defined, but we expect it to be 2"));

        // tricky case: background should be prioritized, but it is quantitative, then it is discarded as a valid choice
        s = new HeuristicSampleDimensionBuilder()
                .addQuantitative("background", 0, 1, Units.UNITY)
                .addQualitative("fill-value", 2, 3)
                .build();
        n = s.getBackground()
                .filter(it -> it.intValue() == 2)
                .orElseThrow(() -> new AssertionError("No background value defined, but we expect it to be 2"));

        s = new HeuristicSampleDimensionBuilder()
                .addQualitative("whatever", 0, 1)
                .build();
        s.getBackground().ifPresent(bg
                -> fail("Only categories with a valid name should be promoted"));
    }
}
