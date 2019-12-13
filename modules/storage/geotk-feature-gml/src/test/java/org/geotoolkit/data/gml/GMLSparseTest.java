/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

import java.io.File;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GMLSparseTest {

    @Test
    public void openTest() throws Exception {

        URL xsdUrl = GMLSparseTest.class.getResource("/org/geotoolkit/data/gml/sparse/cite-gmlsf0.xsd");

        GMLSparseStore store = new GMLSparseStore(
                new File(xsdUrl.toURI()).getParentFile(),
                xsdUrl.toString(),
                "PrimitiveGeoFeature"
        );

        FeatureType type = store.getType();
        Assert.assertNotNull(type);
        Assert.assertEquals("PrimitiveGeoFeature", type.getName().tip().toString());
        Assert.assertEquals(2l, store.features(false).count());
    }

}
