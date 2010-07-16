/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.geometry.jts.transform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ScaleDecimatorTest {

    private final GeometryFactory GF = new GeometryFactory();

    public ScaleDecimatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testPointDecimation() throws TransformException {
        final GeometryTransformer decimator = new GeometryScaleTransformer(10, 10);

        final MultiPoint geom = GF.createMultiPoint(
                new Coordinate[]{
                    new Coordinate(0, 0),
                    new Coordinate(15, 12), //dx 15 , dy 12
                    new Coordinate(8, 28), //dx 7 , dy 16
                    new Coordinate(9, 31), //dx 1 , dy 3
                    new Coordinate(-5, 11), //dx 14 , dy 20
                    new Coordinate(-1, 9) //dx 4 , dy 2
                });

        final MultiPoint decimated = (MultiPoint) decimator.transform(geom);

        assertEquals(4, decimated.getNumGeometries());
        assertEquals(geom.getGeometryN(0).getCoordinate(), decimated.getGeometryN(0).getCoordinate());
        assertEquals(geom.getGeometryN(1).getCoordinate(), decimated.getGeometryN(1).getCoordinate());
        assertEquals(geom.getGeometryN(2).getCoordinate(), decimated.getGeometryN(2).getCoordinate());
        assertEquals(geom.getGeometryN(4).getCoordinate(), decimated.getGeometryN(3).getCoordinate());

    }

    @Test
    public void testLineStringDecimation() throws TransformException {
        final GeometryTransformer decimator = new GeometryScaleTransformer(10, 10);

        final LineString geom = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(0, 0),
                    new Coordinate(15, 12), //dx 15 , dy 12
                    new Coordinate(8, 28), //dx 7 , dy 16
                    new Coordinate(9, 31), //dx 1 , dy 3
                    new Coordinate(-5, 11), //dx 14 , dy 20
                    new Coordinate(-1, 9) //dx 4 , dy 2
                });

        final LineString decimated = (LineString) decimator.transform(geom);

        assertEquals(4, decimated.getNumPoints());
        assertEquals(geom.getGeometryN(0).getCoordinate(), decimated.getGeometryN(0).getCoordinate());
        assertEquals(geom.getGeometryN(1).getCoordinate(), decimated.getGeometryN(1).getCoordinate());
        assertEquals(geom.getGeometryN(2).getCoordinate(), decimated.getGeometryN(2).getCoordinate());
        assertEquals(geom.getGeometryN(4).getCoordinate(), decimated.getGeometryN(3).getCoordinate());

    }

    @Test
    public void testTinyLineStringDecimation() throws TransformException {
        final GeometryTransformer decimator = new GeometryScaleTransformer(10, 10);

        final LineString geom = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(0, 0),
                    new Coordinate(1, 0),
                    new Coordinate(2, 1),
                    new Coordinate(1,1)
                });

        final LineString decimated = (LineString) decimator.transform(geom);

        assertEquals(2, decimated.getNumPoints());
        assertEquals(geom.getGeometryN(0).getCoordinate(), decimated.getGeometryN(0).getCoordinate());
        assertEquals(geom.getGeometryN(3).getCoordinate(), decimated.getGeometryN(1).getCoordinate());

    }

}