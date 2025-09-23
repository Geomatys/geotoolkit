
package org.geotoolkit.dggs.a5.internal;

import org.geotoolkit.dggs.a5.internal.Utils;
import org.geotoolkit.dggs.a5.internal.Cell;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.geometries.GeometryFactory;
import org.apache.sis.geometries.Polygon;
import org.apache.sis.geometries.math.DataType;
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.geometries.math.TupleArrays;
import org.apache.sis.geometries.math.Vector2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import static org.geotoolkit.dggs.a5.internal.Cell.a5cellContainsPoint;
import static org.geotoolkit.dggs.a5.internal.Cell.cellToBoundary;
import static org.geotoolkit.dggs.a5.internal.Cell.lonLatToCell;
import static org.geotoolkit.dggs.a5.internal.Serialization.MAX_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.Serialization.deserialize;
import org.geotoolkit.storage.geojson.GeoJSONProvider;
import org.geotoolkit.storage.geojson.GeoJSONStore;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;

public class CellTest {

    @Test
    public void cellBoundaryTests() throws URISyntaxException, DataStoreException {
        //should contain the original point for all resolutions
        final Map<String, Vector2D.Double> testPoints = new LinkedHashMap<>();
        try (GeoJSONStore store = new GeoJSONStore(new GeoJSONProvider(), CellTest.class.getResource("ne_50m_populated_places_nameonly.json").toURI(), 7)) {
            try (Stream<Feature> features = store.features(false)) {
                final Iterator<Feature> iterator = features.iterator();
                while (iterator.hasNext()) {
                    final Feature feature = iterator.next();
                    final Point point = (Point) feature.getPropertyValue(AttributeConvention.GEOMETRY);
                    final String name = (String) feature.getPropertyValue("name");
                    testPoints.put(name, new Vector2D.Double(point.getX(), point.getY()));
                }
            }
        }

        //System.out.println("Testing with " +  testPoints.size() + " points from GeoJSON file");

        // Dictionary to store failures for each resolution and point
        final Map<String,Map<Integer,List<String>>> failures = new LinkedHashMap<>();

        //System.out.println("Skipping resolution " + MAX_RESOLUTION + " as lonLatToCell is not implemented for this resolution yet");

        // Test each random GeoJSON
        int pointIndex = 0;
        for (Entry<String,Vector2D.Double> entry : testPoints.entrySet()) {
            final String featureName = entry.getKey();
            final Vector2D.Double testLonlat = entry.getValue();
            final String pointKey = "Point " + pointIndex + " - " + featureName + " (" + testLonlat.x + "," + testLonlat.y + ")";

            // Test resolutions from 0 to MAX_RESOLUTION
            for (int resolution = 1; resolution <= MAX_RESOLUTION; resolution++) {
                if (resolution == MAX_RESOLUTION) {
                    continue;
                }

                final List<String> resolutionFailures = new ArrayList<>();

                // Get cell ID for the coordinates
                long cellId = lonLatToCell(testLonlat, resolution);

                // Get cell boundary
                final Vector2D.Double[] boundary = cellToBoundary(cellId, new Cell.CellToBoundaryOptions());
                final SampleSystem ss = SampleSystem.of(CommonCRS.WGS84.normalizedGeographic());
                final Polygon polygon = GeometryFactory.createPolygon(
                        GeometryFactory.createLinearRing(GeometryFactory.createSequence(TupleArrays.of(List.of(boundary), ss, DataType.DOUBLE))), Collections.EMPTY_LIST);

                // Verify the original point is contained within the cell
                final Utils.A5Cell cell = deserialize(cellId);
                if (! (a5cellContainsPoint(cell, testLonlat) != 0)) {
                    resolutionFailures.add("Cell " + cellId + " does not contain the original point " + testLonlat + " cell " + polygon.asText());
                }

                // Store failures for this resolution if any occurred
                if (!resolutionFailures.isEmpty()) {
                    if (!failures.containsKey(pointKey)) { failures.put(pointKey, new LinkedHashMap<>());}
                    failures.get(pointKey).put(resolution, resolutionFailures);
                }
            }
            pointIndex++;
        }

        // Report all failures
        if (!failures.isEmpty()) {
            StringBuilder failureMessage = new StringBuilder("\nFailures by point and resolution:\n");
            for (Entry<String,Map<Integer,List<String>>> entry : failures.entrySet()) {
                final String pointKey = entry.getKey();
                failureMessage.append("\n ").append(pointKey).append("\n");
                for (Entry<Integer,List<String>> subentry : entry.getValue().entrySet()) {
                    final int resolution = subentry.getKey();
                    failureMessage.append("  Resolution ").append(resolution).append(":\n");
                    for (String failure : subentry.getValue()) {
                        failureMessage.append(" -").append(failure).append("\n");
                    }
                }

            }
            throw new Error(failureMessage.toString());
        }

    }

}
