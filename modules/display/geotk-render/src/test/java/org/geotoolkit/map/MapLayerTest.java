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

package org.geotoolkit.map;

import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.storage.query.SimpleQuery;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Query;
import org.geotoolkit.storage.feature.DefiningFeatureSet;
import org.geotoolkit.storage.memory.InMemoryStore;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.util.NamesExt;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MapLayerTest {

    public MapLayerTest() {
    }

    @Test
    public void testFeatureLayer() throws DataStoreException {

        try {
            MapBuilder.createFeatureLayer(null, null);
            fail("Creating maplayer with null source should raise an error");
        } catch (Exception ex) {
            //ok
        }

        final GenericName name = NamesExt.create("test");
        FeatureTypeBuilder builder = new FeatureTypeBuilder();
        builder.setName(name);
        FeatureType type = builder.build();

        InMemoryStore ds = new InMemoryStore();
        FeatureSet fs = (FeatureSet) ds.add(new DefiningFeatureSet(type, null));

        FeatureMapLayer layer = MapBuilder.createFeatureLayer(fs, new DefaultStyleFactory().style());
        assertNotNull(layer);

        Query query = layer.getQuery();
        assertNull(query);

        try {
            layer.setQuery(null);
        } catch (Exception ex) {
            //ok
            throw new IllegalArgumentException("Can set a null query");
        }

        try {
            final SimpleQuery sq = new SimpleQuery();
            sq.setFilter(Filter.EXCLUDE);
            layer.setQuery(sq);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Should be able to set this query");
        }

    }


}
