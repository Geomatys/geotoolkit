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

import java.io.IOException;
import java.util.Optional;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.apache.sis.util.Utilities;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
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

    @Test
    public void testMapContextBounds() throws FactoryException, IOException {

        final CoordinateReferenceSystem wgs72 = CommonCRS.WGS72.defaultGeographic();
        MutableStyle defaultStyle = new DefaultStyleFactory().style();

        //Test layers in same CRS
        MapContext context = MapBuilder.createContext();
        GeneralEnvelope envWGS84 = new GeneralEnvelope(CommonCRS.WGS84.defaultGeographic());
        envWGS84.setRange(0, -10, 10);
        envWGS84.setRange(1, -10, 10);
        context.layers().add(createEmptyLayer(envWGS84));

        GeneralEnvelope envWGS842 = new GeneralEnvelope(CommonCRS.WGS84.defaultGeographic());
        envWGS842.setRange(0, 10, 20);
        envWGS842.setRange(1, 10, 20);
        MapLayer layer = createEmptyLayer(envWGS842);
        layer.setVisible(false);
        context.layers().add(layer);

        //all layer
        Envelope ctxBounds = context.getBounds(false);
        assertNotNull(ctxBounds);
        GeneralEnvelope expected = new GeneralEnvelope(CommonCRS.WGS84.defaultGeographic());
        expected.setRange(0, -10, 20);
        expected.setRange(1, -10, 20);
        assertTrue(expected.equals(ctxBounds, 0.00001, true));

        //only visible
        ctxBounds = context.getBounds(true);
        assertNotNull(ctxBounds);
        expected = new GeneralEnvelope(CommonCRS.WGS84.defaultGeographic());
        expected.setRange(0, -10, 10);
        expected.setRange(1, -10, 10);
        assertTrue(expected.equals(ctxBounds, 0.00001, true));


        // test layer in different CRS than Context
        context = MapBuilder.createContext();

        CoordinateReferenceSystem mercator = CRS.fromWKT(
                "PROJCS[“WGS 84 / World Mercator”,\n" +
                "  GEOGCS[“WGS 84”,\n" +
                "    DATUM[“World Geodetic System 1984”,\n" +
                "      SPHEROID[“WGS 84”, 6378137.0, 298.257223563, AUTHORITY[“EPSG”, “7030”]],\n" +
                "      AUTHORITY[“EPSG”, “6326”]],\n" +
                "    PRIMEM[“Greenwich”, 0.0, AUTHORITY[“EPSG”, “8901”]],\n" +
                "    UNIT[“degree”, 0.017453292519943295],\n" +
                "    AXIS[“Geodetic latitude”, NORTH],\n" +
                "    AXIS[“Geodetic longitude”, EAST],\n" +
                "    AUTHORITY[“EPSG”, “4326”]],\n" +
                "  PROJECTION[“Mercator (1SP)”, AUTHORITY[“EPSG”, “9804”]],\n" +
                "  PARAMETER[“latitude_of_origin”, 0.0],\n" +
                "  PARAMETER[“central_meridian”, 0.0],\n" +
                "  PARAMETER[“scale_factor”, 1.0],\n" +
                "  PARAMETER[“false_easting”, 0.0],\n" +
                "  PARAMETER[“false_northing”, 0.0],\n" +
                "  UNIT[“m”, 1.0],\n" +
                "  AXIS[“Easting”, EAST],\n" +
                "  AXIS[“Northing”, NORTH],\n" +
                "  AUTHORITY[“EPSG”, “3395”]]");
        GeneralEnvelope env1 = new GeneralEnvelope(mercator);
        env1.setRange(0, -10000, 10000);
        env1.setRange(1, -10000, 10000);
        context.layers().add(createEmptyLayer(env1));

        CoordinateReferenceSystem lambert = CRS.fromWKT(
                "PROJCS[“NAD_1983_StatePlane_Massachusetts_Mainland_FIPS_2001”,\n" +
                "  GEOGCS[“GCS_North_American_1983”,\n" +
                "    DATUM[“D_North_American_1983”,\n" +
                "      SPHEROID[“GRS_1980”, 6378137.0, 298.257222101]],\n" +
                "    PRIMEM[“Greenwich”, 0.0],\n" +
                "    UNIT[“degree”, 0.017453292519943295],\n" +
                "    AXIS[“Latitude”, NORTH],\n" +
                "    AXIS[“Longitude”, EAST]],\n" +
                "  PROJECTION[“Lambert_Conformal_Conic”],\n" +
                "  PARAMETER[“central_meridian”, -71.5],\n" +
                "  PARAMETER[“latitude_of_origin”, 41.0],\n" +
                "  PARAMETER[“standard_parallel_1”, 41.71666666666667],\n" +
                "  PARAMETER[“scale_factor”, 1.0],\n" +
                "  PARAMETER[“false_easting”, 200000.0],\n" +
                "  PARAMETER[“false_northing”, 750000.0],\n" +
                "  PARAMETER[“standard_parallel_2”, 42.68333333333334],\n" +
                "  UNIT[“m”, 1.0],\n" +
                "  AXIS[“x”, EAST],\n" +
                "  AXIS[“y”, NORTH]]");
        GeneralEnvelope env2 = new GeneralEnvelope(lambert);
        env2.setRange(0, -10, 10);
        env2.setRange(1, -10, 10);
        context.layers().add(createEmptyLayer(env2));

        ctxBounds = context.getBounds(true);
        assertNotNull(ctxBounds);
        expected = new GeneralEnvelope(CommonCRS.WGS84.defaultGeographic());
        expected.setRange(0, -73.651, -0.090);
        expected.setRange(1, -0.089, 34.244);
        assertTrue(expected.equals(ctxBounds, 0.01, true));

        // empty layer bounds equals to CRS validity domain
        context = MapBuilder.createContext();
        ctxBounds = context.getBounds(true);
        assertNotNull(ctxBounds);
        expected = new GeneralEnvelope(CRS.getDomainOfValidity(CommonCRS.WGS84.defaultGeographic()));
        assertTrue(expected.equals(ctxBounds, 0.0000001, true));

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
