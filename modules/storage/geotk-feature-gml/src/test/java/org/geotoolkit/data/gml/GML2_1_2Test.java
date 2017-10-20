/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.gml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiLineString;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.data.FeatureSet;
import org.geotoolkit.storage.Resource;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 * GML 2.1.2 store tests.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class GML2_1_2Test {

    public static final GeometryFactory GF = new GeometryFactory();

    @Test
    public void testMultiLigne() throws Exception {

        final GMLFeatureStore store = new GMLFeatureStore(GML2_1_2Test.class.getResource("/org/geotoolkit/data/gml/2_1_2/MultiLigne.xml").toURI());
        final Set<GenericName> names = store.getNames();
        assertEquals(1,names.size());
        final GenericName name = names.iterator().next();
        assertEquals("CoursEau", name.tip().toString());
        final FeatureType type = store.getFeatureType(name.toString());
        assertEquals("CoursEau", type.getName().tip().toString());
        assertNotNull(type.getProperty("msGeometry"));
        assertNotNull(type.getProperty("CdEntiteHydrographique"));
        assertNotNull(type.getProperty("NomEntiteHydrographique"));
        assertNotNull(type.getProperty("Classe"));

        final Resource resource = store.findResource(name.toString());
        assertTrue(resource instanceof FeatureSet);
        final FeatureSet fs = (FeatureSet) resource;
        final List<Feature> features = fs.features(false).collect(Collectors.toList());
        assertEquals(2, features.size());

        final Feature f1 = features.get(0);
        final MultiLineString ml1 = (MultiLineString) f1.getPropertyValue("msGeometry");
        assertEquals(2, ml1.getNumGeometries());
        assertArrayEquals(new Coordinate[]{
            new Coordinate(10, 20),
            new Coordinate(30, 40),
            new Coordinate(50, 60),
            new Coordinate(1, 2),
            new Coordinate(3, 4),
            new Coordinate(5, 6)}, ml1.getCoordinates());
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), ml1.getUserData());
        assertEquals("I0245000",f1.getPropertyValue("CdEntiteHydrographique"));
        assertEquals("Ruisseau de la Boulaie",f1.getPropertyValue("NomEntiteHydrographique"));
        assertEquals("6",f1.getPropertyValue("Classe"));

        final Feature f2 = features.get(1);
        final MultiLineString ml2 = (MultiLineString) f2.getPropertyValue("msGeometry");
        assertEquals(1, ml2.getNumGeometries());
        assertArrayEquals(new Coordinate[]{
            new Coordinate(7, 8),
            new Coordinate(9, 10)}, ml2.getCoordinates());
        assertEquals(CommonCRS.WGS84.normalizedGeographic(), ml1.getUserData());
        assertEquals("M03-4002",f2.getPropertyValue("CdEntiteHydrographique"));
        assertEquals("canal de l'Arcisses",f2.getPropertyValue("NomEntiteHydrographique"));
        assertEquals("7",f2.getPropertyValue("Classe"));

    }

}
