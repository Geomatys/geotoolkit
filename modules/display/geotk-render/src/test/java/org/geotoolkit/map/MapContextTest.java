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

import java.util.Optional;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.apache.sis.util.Utilities;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * Test MapContext creation and behavior.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class MapContextTest {

    @Test
    public void testContextCreation() throws FactoryException {

        try {
            MapBuilder.createContext(null);
            fail("Creating MapContext with null CRS should raise an error");
        }catch(Exception ex){
            //ok
        }

        //default MapContext
        MapContext context = MapBuilder.createContext();
        assertNotNull(context);
        assertTrue(Utilities.equalsIgnoreMetadata(CommonCRS.WGS84.defaultGeographic(), context.getCoordinateReferenceSystem()));


        // WGS72 MapContext
        final CoordinateReferenceSystem wgs72 = CommonCRS.WGS72.defaultGeographic();
        context = MapBuilder.createContext(wgs72);
        assertNotNull(context);
        assertTrue(Utilities.equalsIgnoreMetadata(wgs72, context.getCoordinateReferenceSystem()));
    }

    private static MapLayer createEmptyLayer(Envelope env) {
        final DataSet ds = new DataSet() {
            @Override
            public Optional<Envelope> getEnvelope() throws DataStoreException {
                return Optional.of(env);
            }

            @Override
            public Optional<GenericName> getIdentifier() throws DataStoreException {
                return Optional.empty();
            }

            @Override
            public Metadata getMetadata() throws DataStoreException {
                return new DefaultMetadata();
            }

            @Override
            public <T extends StoreEvent> void addListener(Class<T> type, StoreListener<? super T> sl) {
            }

            @Override
            public <T extends StoreEvent> void removeListener(Class<T> type, StoreListener<? super T> sl) {
            }
        };
        return MapBuilder.createLayer(ds);
    }
}
