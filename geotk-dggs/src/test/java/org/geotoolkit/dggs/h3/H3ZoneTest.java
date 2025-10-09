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
package org.geotoolkit.dggs.h3;

import org.geotoolkit.referencing.dggs.Zone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class H3ZoneTest {

    /**
     * Test hierarchical children for a hexagon cell.
     * 8009fffffffffff
     */
    @Test
    public void testHierarchicalChildrenHexagon() {
        final H3Dggrs dggrs = new H3Dggrs();
        final long zoneId = 0x85283473fffffffl;
        final H3Zone zone = new H3Zone(dggrs, zoneId);
        final String[] hierarchicalChildren = zone.getHierarchicalChildren().stream().map(H3Zone::getTextIdentifier).toArray(String[]::new);

        Assertions.assertArrayEquals(new String[]{
            "862834707ffffff",
            "86283470fffffff",
            "862834717ffffff",
            "86283471fffffff",
            "862834727ffffff",
            "86283472fffffff",
            "862834737ffffff"
        }, hierarchicalChildren);
    }

    /**
     * Test children for a hexagon cell.
     */
    @Test
    public void testChildrenHexagon() {
        final H3Dggrs dggrs = new H3Dggrs();
        final long zoneId = 0x85283473fffffffl;
        final H3Zone zone = new H3Zone(dggrs, zoneId);
        final String[] children = zone.getChildren().stream().map(H3Zone::getTextIdentifier).toArray(String[]::new);

        Assertions.assertArrayEquals(new String[]{
            //7 hierarchical children
            "862834707ffffff",
            "86283470fffffff",
            "862834717ffffff",
            "86283471fffffff",
            "862834727ffffff",
            "86283472fffffff",
            "862834737ffffff",
            //6 adjacents cell overlaping
            "862834627ffffff",
            "86283444fffffff",
            "8628347afffffff",
            "8628340d7ffffff",
            "862834777ffffff",
            "86283409fffffff"
        }, children);
    }

    /**
     * Test parents for a hexagon cell.
     */
    @Test
    public void testParentHexagon() throws TransformException {
        final H3Dggrs dggrs = new H3Dggrs();
        final String parentId = "85283473fffffff";
        final Zone parentZone = dggrs.createCoder().decode(parentId);

        //7 hierarchical children
        //only the first one must have a single parent
        final String[] hierarchicalCenter = new String[]{
            "862834707ffffff",
        };
        for (String zid : hierarchicalCenter) {
            Zone zone = dggrs.createCoder().decode(zid);
            Zone[] parents = zone.getParents().toArray(Zone[]::new);
            Assertions.assertEquals(1, parents.length);
            Assertions.assertEquals(parentId ,parents[0].getTextIdentifier());
        }
        //the others are also in another parent
        final String[] hierarchical = new String[]{
            "86283470fffffff",
            "862834717ffffff",
            "86283471fffffff",
            "862834727ffffff",
            "86283472fffffff",
            "862834737ffffff"
        };
        for (String zid : hierarchical) {
            Zone zone = dggrs.createCoder().decode(zid);
            Zone[] parents = zone.getParents().toArray(Zone[]::new);
            Assertions.assertEquals(2, parents.length);
            //the first parent must be this parent
            Assertions.assertEquals(parentId ,parents[0].getTextIdentifier());
            Assertions.assertNotEquals(parentId ,parents[1].getTextIdentifier());
        }
        //6 adjacents cell overlaping
        final String[] adjacents = new String[]{
            "862834627ffffff",
            "86283444fffffff",
            "8628347afffffff",
            "8628340d7ffffff",
            "862834777ffffff",
            "86283409fffffff"
        };
        for (String zid : adjacents) {
            Zone zone = dggrs.createCoder().decode(zid);
            Zone[] parents = zone.getParents().toArray(Zone[]::new);
            Assertions.assertEquals(2, parents.length);
            //first parent should be another parent
            Assertions.assertNotEquals(parentId ,parents[0].getTextIdentifier());
            Assertions.assertEquals(parentId ,parents[1].getTextIdentifier());
        }
    }

    /**
     * Test hierarchical children for a hexagon cell.
     *
     */
    @Test
    public void testHierarchicalChildrenPentagon() {
        final H3Dggrs dggrs = new H3Dggrs();
        final long zoneId = 0x8009fffffffffffl;
        final H3Zone zone = new H3Zone(dggrs, zoneId);
        final String[] hierarchicalChildren = zone.getHierarchicalChildren().stream().map(H3Zone::getTextIdentifier).toArray(String[]::new);
        Assertions.assertArrayEquals(new String[]{
            "81083ffffffffff",
            "8108bffffffffff",
            "8108fffffffffff",
            "81093ffffffffff",
            "81097ffffffffff",
            "8109bffffffffff"
        }, hierarchicalChildren);
    }

    /**
     * Test children for a hexagon cell.
     */
    @Test
    public void testChildrenPentagon() {
        final H3Dggrs dggrs = new H3Dggrs();
        final long zoneId = 0x8009fffffffffffl;
        final H3Zone zone = new H3Zone(dggrs, zoneId);
        final String[] children = zone.getChildren().stream().map(H3Zone::getTextIdentifier).toArray(String[]::new);
        Assertions.assertArrayEquals(new String[]{
            //6 hierarchical children
            "81083ffffffffff",
            "8108bffffffffff",
            "8108fffffffffff",
            "81093ffffffffff",
            "81097ffffffffff",
            "8109bffffffffff",
            //5 adjacents cell overlaping
            "81113ffffffffff",
            "81013ffffffffff",
            "81193ffffffffff",
            "81073ffffffffff",
            "811f3ffffffffff"
        }, children);
    }

    /**
     * Test parents for a hexagon cell.
     */
    @Test
    public void testParentPentagon() throws TransformException {
        final H3Dggrs dggrs = new H3Dggrs();
        final String parentId = "8009fffffffffff";
        final Zone parentZone = dggrs.createCoder().decode(parentId);

        //7 hierarchical children
        //only the first one must have a single parent
        final String[] hierarchicalCenter = new String[]{
            "81083ffffffffff",
        };
        for (String zid : hierarchicalCenter) {
            Zone zone = dggrs.createCoder().decode(zid);
            Zone[] parents = zone.getParents().toArray(Zone[]::new);
            Assertions.assertEquals(1, parents.length);
            Assertions.assertEquals(parentId ,parents[0].getTextIdentifier());
        }
        //the others are also in another parent
        final String[] hierarchical = new String[]{
            "8108bffffffffff",
            "8108fffffffffff",
            "81093ffffffffff",
            "81097ffffffffff",
            "8109bffffffffff"
        };
        for (String zid : hierarchical) {
            Zone zone = dggrs.createCoder().decode(zid);
            Zone[] parents = zone.getParents().toArray(Zone[]::new);
            Assertions.assertEquals(2, parents.length);
            //the first parent must be this parent
            Assertions.assertEquals(parentId ,parents[0].getTextIdentifier());
            Assertions.assertNotEquals(parentId ,parents[1].getTextIdentifier());
        }
        //6 adjacents cell overlaping
        final String[] adjacents = new String[]{
            "81113ffffffffff",
            "81013ffffffffff",
            "81193ffffffffff",
            "81073ffffffffff",
            "811f3ffffffffff"
        };
        for (String zid : adjacents) {
            Zone zone = dggrs.createCoder().decode(zid);
            Zone[] parents = zone.getParents().toArray(Zone[]::new);
            Assertions.assertEquals(2, parents.length);
            //first parent should be another parent
            Assertions.assertNotEquals(parentId ,parents[0].getTextIdentifier());
            Assertions.assertEquals(parentId ,parents[1].getTextIdentifier());
        }
    }

}
