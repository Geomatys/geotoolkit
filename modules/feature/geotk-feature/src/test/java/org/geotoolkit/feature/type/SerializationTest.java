/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010 Geomatys
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
 *
 *    Created on July 21, 2003, 4:00 PM
 */
package org.geotoolkit.feature.type;

import java.util.Collections;
import java.util.Collection;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.sis.util.iso.SimpleInternationalString;

import org.apache.sis.referencing.CommonCRS;
import static org.geotoolkit.test.Assert.*;

import org.junit.Test;

/**
 * Test the different feature type and attribute classes serialization.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class SerializationTest {

    public SerializationTest() {
    }

    @Test
    public void testSerialize() {

        //test attribut type
        AttributeType attType = new DefaultAttributeType(DefaultName.valueOf("attType"),
                String.class, true, true, null, null, new SimpleInternationalString("i18n"));
        assertSerializedEquals(attType);

        //test association type
        AssociationType assoType = new DefaultAssociationType(
                DefaultName.valueOf("asso"), attType, true,
                null, null, new SimpleInternationalString("i18n"));
        assertSerializedEquals(assoType);

        //test association type
        AssociationDescriptor assoDesc = new DefaultAssociationDescriptor(
                assoType, DefaultName.valueOf("assoDesc"), 0, 1, false);
        assertSerializedEquals(assoDesc);

        //test attribute descriptor
        AttributeDescriptor attDesc = new DefaultAttributeDescriptor(
                attType, DefaultName.valueOf("attDesc"), 0, 1, true, null);
        assertSerializedEquals(attDesc);

        //test geometry type
        GeometryType geomtype = new DefaultGeometryType(DefaultName.valueOf("geomType"),
                Geometry.class, CommonCRS.WGS84.normalizedGeographic(), true, true, null,
                attType, new SimpleInternationalString("i18n"));
        assertSerializedEquals(geomtype);

        //test property descriptor
        GeometryDescriptor geomDesc = new DefaultGeometryDescriptor(geomtype,
                DefaultName.valueOf("geomdesc"), 0, 1, true, null);
        assertSerializedEquals(geomDesc);

        //test complexe type
        ComplexType comType = new DefaultComplexType(DefaultName.valueOf("comType"),
                (Collection)Collections.singleton(geomDesc), true, true, null, attType, null);
        assertSerializedEquals(comType);

        assertSerializedEquals(BasicFeatureTypes.FEATURE);

    }
}
