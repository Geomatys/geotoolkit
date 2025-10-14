/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.dggs.h3.internal.shared;

import com.uber.h3core.H3Core;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import org.geotoolkit.dggs.h3.H3Ext;
import org.junit.Test;

/**
 * Python code provided by Jerome Saint-Louis to ensure H3 subchild results match DGGRS requirements.
 *
 *
 * @author Johann Sorel (Geomatys)
 * @author Jerome Saint-Louis (Ecere), original code in python
 */
public class H3ExtTest {

    @Test
    public void subZoneTest() throws IOException {
        final H3Core H3 = H3Core.newInstance();

        final int[] relative_depths = new int[]{2, 3, 4, 5, 6};

        //Select one specific pentagon from the list for testing
        final List<Long> pentagon_res3 = new ArrayList<>(H3.getPentagons(3));
        final List<Long> pentagon_res2 = new ArrayList<>(H3.getPentagons(2));

        record testCase(String name, Long cellId){};

        final testCase[] test_cases = new testCase[] {
            new testCase("Hexagonal_NYC_Res5", H3.latLngToCell(40.7128, -74.0060, 5)),
            new testCase("Hexagonal_London_Res4", H3.latLngToCell(51.5074, -0.1278, 4)),
            new testCase("Pentagonal_SouthPole_Res3", pentagon_res3.get(0)),
            new testCase("Pentagonal_Atlantic_Res2", pentagon_res2.get(0)),
            new testCase("Hexagonal_HighRes_Res10", H3.latLngToCell(34.0522, -118.2437, 10)),
            new testCase("-80, 0", H3.latLngToCell(-80.0, 0.0, 3))
        };

        System.out.println("Starting comprehensive test suite...");
        for (testCase t : test_cases) {
            final int parentResolution = H3Index.getResolution(t.cellId);
            final String parentType = H3.isPentagon(t.cellId) ? "Pentagonal" : "Hexagonal";

            System.out.println("--- Testing " + t.name + " " + parentType + ", Res=" + parentResolution + ", H3=" + t.cellId + " ---");

            for (int relative_depth : relative_depths) {
                final int target_resolution = parentResolution + relative_depth;

                if (target_resolution > 15) {
                    System.out.println("  Skipping depth " + relative_depth + ": Target resolution " + target_resolution + " exceeds max (15).");
                    continue;
                }

                long before = System.currentTimeMillis();
                final LongStream geometric_subzones = H3Ext.getGeometricSubZones(t.cellId, relative_depth);
                long count = geometric_subzones.count();
                long after = System.currentTimeMillis();
                System.out.println("  Found " + count + " geometric sub-zones at resolution " + target_resolution + " (Depth " + relative_depth + "). in " + (after-before) + "ms");
            }
        }
    }
}
