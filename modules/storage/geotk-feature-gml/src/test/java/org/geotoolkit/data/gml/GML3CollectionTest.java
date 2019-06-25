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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.sis.storage.DataStoreException;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GML3CollectionTest {

    /**
     * Base type is a Feature collection type because it has a FeatureMember property.
     * FeatureSet type must be the member type.
     *
     * @throws URISyntaxException
     * @throws MalformedURLException
     * @throws DataStoreException
     * @throws IOException
     */
    @Test
    public void collectionTest() throws URISyntaxException, MalformedURLException, DataStoreException, IOException {

        Path file = Files.createTempFile("gml3", "gml");
        final GMLFeatureStore store = new GMLFeatureStore(file, GML2_1_2Test.class.getResource("/org/geotoolkit/data/gml/2_1_2/Road.xsd").toURI().toString(), "Road", null);

        FeatureType type = store.getType();

        Assert.assertEquals("RoadMemberType", type.getName().tip().toString());

    }

}
