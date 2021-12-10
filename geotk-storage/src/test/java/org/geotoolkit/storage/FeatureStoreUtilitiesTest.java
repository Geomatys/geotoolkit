/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage;

import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import java.util.Arrays;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureStoreUtilitiesTest {

    @Test
    public void testGetEnvelope() throws DataStoreException {
        final GeometryFactory gf = org.geotoolkit.geometry.jts.JTS.getFactory();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Candidate");
        ftb.addAttribute(Geometry.class).setName("geom").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType type = ftb.build();

        final Feature f1 = type.newInstance();
        f1.setPropertyValue("geom", gf.createPoint(new Coordinate(10, 20)));
        final Feature f2 = type.newInstance();
        f2.setPropertyValue("geom", gf.createPoint(new Coordinate(-30, -40)));

        final FeatureSet fs = new InMemoryFeatureSet(type, Arrays.asList(f1,f2));

        final Envelope envelope = FeatureStoreUtilities.getEnvelope(fs);

        final GeneralEnvelope expected = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        expected.setRange(0, -30, 10);
        expected.setRange(1, -40, 20);

        Assert.assertEquals(expected, envelope);

    }

}
