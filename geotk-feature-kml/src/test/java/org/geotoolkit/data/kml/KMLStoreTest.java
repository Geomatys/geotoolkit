/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017-2023, Geomatys
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
package org.geotoolkit.data.kml;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import static org.junit.Assert.*;

import org.apache.sis.storage.IllegalNameException;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class KMLStoreTest {

    @Test
    public void testReadPlacemark() throws Exception {

        final URL path = KMLStoreTest.class.getResource("/org/geotoolkit/data/kml/placemark.kml");
        try (final KMLStore store = new KMLStore(path.toURI())) {
            assertEquals("placemark.kml", store.getIdentifier().get().toString());
            final FeatureSet featureSet = (FeatureSet) store.findResource("Placemark");
            final FeatureType type = featureSet.getType();
            assertEquals(KMLStore.PLACEMARK_TYPE, type);

            try (Stream<Feature> stream = featureSet.features(false)) {
                final Iterator<Feature> ite = stream.iterator();
                assertTrue(ite.hasNext());
                final Feature feature = ite.next();
                assertEquals("Google Earth - New Placemark", feature.getPropertyValue("name"));
                assertEquals("Some Descriptive text.", feature.getPropertyValue("description"));
                final Geometry geom = (Geometry) feature.getPropertyValue("geometry");
                assertTrue(geom instanceof Point);
                final Point pt = (Point) geom;
                assertEquals(CommonCRS.WGS84.normalizedGeographic(), pt.getUserData());
                assertEquals(-90.86948943473118, pt.getX(), 0.0);
                assertEquals(48.25450093195546, pt.getY(), 0.0);
                assertEquals(0.0, pt.getCoordinate().getZ(), 0.0);

                assertFalse(ite.hasNext());
            }
        }
    }

    @Test
    public void testReadGroundOverlay() throws Exception {

        final URL path = KMLStoreTest.class.getResource("/org/geotoolkit/data/kml/groundOverlay.kml");
        try (final KMLStore store = new KMLStore(path.toURI())) {
            assertEquals("groundOverlay.kml", store.getIdentifier().get().toString());
            final FeatureSet featureSet = (FeatureSet) store.findResource("GroundOverlay");
            final FeatureType type = featureSet.getType();
            assertEquals(KMLStore.GROUNDOVERLAY_TYPE, type);

            try (Stream<Feature> stream = featureSet.features(false)) {
                final Iterator<Feature> ite = stream.iterator();
                assertTrue(ite.hasNext());
                final Feature feature = ite.next();
                assertEquals("GroundOverlay.kml", feature.getPropertyValue("name"));

                final GridCoverage icon = (GridCoverage) feature.getPropertyValue("icon");
                assertNotNull(icon);
                final GridGeometry gridGeometry = icon.getGridGeometry();
                final GridExtent extent = gridGeometry.getExtent();
                assertEquals(36, extent.getSize(0));
                assertEquals(18,extent.getSize(1));

                assertFalse(ite.hasNext());
            }
        }
    }

    @Test
    public void testReadGroundOverlayKmz() throws Exception {

        final URL path = KMLStoreTest.class.getResource("/org/geotoolkit/data/kmz/groundOverlay.kmz");
        try (final KMLStore store = new KMLStore(path.toURI())) {
            assertEquals("groundOverlay.kmz", store.getIdentifier().get().toString());
            final FeatureSet featureSet = (FeatureSet) store.findResource("GroundOverlay");
            final FeatureType type = featureSet.getType();
            assertEquals(KMLStore.GROUNDOVERLAY_TYPE, type);

            try (Stream<Feature> stream = featureSet.features(false)) {
                final Iterator<Feature> ite = stream.iterator();
                assertTrue(ite.hasNext());
                final Feature feature = ite.next();
                assertEquals("GroundOverlay.kml", feature.getPropertyValue("name"));

                final GridCoverage icon = (GridCoverage) feature.getPropertyValue("icon");
                assertNotNull(icon);
                final GridGeometry gridGeometry = icon.getGridGeometry();
                final GridExtent extent = gridGeometry.getExtent();
                assertEquals(36, extent.getSize(0));
                assertEquals(18,extent.getSize(1));

                assertFalse(ite.hasNext());
            }
        }
    }

    @Test
    public void testOldNamespace() throws Exception {
        try (final KMLStore store = new KMLStore(resource("/org/geotoolkit/data/kml/old-ns.kml"))) {
            var dataset = getDataset(store, "Placemark");

            assertEquals(KMLStore.PLACEMARK_TYPE, dataset.getType());

            try (var datastream = dataset.features(false)) {
                assertEquals(2, datastream.count());
            }
        }
    }

    private URI resource(final String classpathLocation) throws URISyntaxException {
        final var resourceUrl = KMLStoreTest.class.getResource(classpathLocation);
        assertNotNull("No resource found in classpath at location: "+classpathLocation, resourceUrl);
        return resourceUrl.toURI();
    }

    private FeatureSet getDataset(final KMLStore store, String datasetName) throws DataStoreException {
        try {
            var dataset = store.findResource(datasetName);
            if (dataset instanceof FeatureSet fs) return fs;
            else throw new AssertionError("Dataset "+datasetName+" exists but is not a Feature set. Resource: "+dataset);
        } catch (IllegalNameException e) {
            var names = store.components().stream().map(r -> {
                try {
                    return r.getIdentifier().map(it -> it.toString()).orElse("null");
                } catch (DataStoreException ex) {
                    throw new RuntimeException(ex);
                }
            }).collect(Collectors.joining(", "));
            throw new AssertionError("No resource "+datasetName+" found in datastore. Available names: "+names, e);
        }
    }
}
