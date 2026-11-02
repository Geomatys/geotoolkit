/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.storage.memory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.storage.tiling.WritableTileMatrixSet;
import org.geotoolkit.storage.multires.TileMatrices;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Tests {@link FeatureSetTileGenerator}.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureSetTileGeneratorTest {

    private static final CoordinateReferenceSystem CRS84 = CommonCRS.WGS84.normalizedGeographic();
    private static final GeometryFactory GF = new GeometryFactory();
    /** Area of the features, the tiles are generated over it only. */
    private static final Envelope2D AREA = new Envelope2D(CRS84, 4.9, 42.9, 0.3, 0.3);

    /**
     * Without minimum footprint all features are kept, with one the features smaller than
     * the size at the tile resolution are skipped, points are kept.
     * Level 10 has a resolution of about 0.00069° per pixel, level 5 about 0.022°.
     */
    @Test
    public void testMinimumFootprint() throws Exception {
        final FeatureSet source = createFeatures();

        Assert.assertEquals(Set.of("large", "small", "point"), generate(source, 0, 10));
        Assert.assertEquals(Set.of("large", "small", "point"), generate(source, 0, 5));

        //the small square is 0.0005°, less than 2 pixels at level 10
        Assert.assertEquals(Set.of("large", "point"), generate(source, 2, 10));
        //the large square is 0.1°, about 4.5 pixels at level 5
        Assert.assertEquals(Set.of("large", "point"), generate(source, 2, 5));
        Assert.assertEquals(Set.of("point"), generate(source, 5, 5));
    }

    @Test
    public void testInvalidMinimumFootprint() throws Exception {
        final FeatureSetTileGenerator generator = new FeatureSetTileGenerator(createFeatures());
        Assert.assertEquals(0, generator.getMinimumFootprint(), 0);
        Assert.assertThrows(IllegalArgumentException.class, () -> generator.setMinimumFootprint(-1));
        Assert.assertThrows(IllegalArgumentException.class, () -> generator.setMinimumFootprint(Double.NaN));
    }

    private static FeatureSet createFeatures() {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Geometry.class).setName("geom").setCRS(CRS84).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(String.class).setName("name");
        final FeatureType type = ftb.build();
        final List<Feature> features = new ArrayList<>();
        features.add(feature(type, "large", square(5.0, 43.0, 0.1)));
        features.add(feature(type, "small", square(5.15, 43.15, 0.0005)));
        features.add(feature(type, "point", GF.createPoint(new Coordinate(5.05, 43.15))));
        return new InMemoryFeatureSet(type, features);
    }

    private static Geometry square(double x, double y, double size) {
        return GF.createPolygon(new Coordinate[] {new Coordinate(x, y), new Coordinate(x + size, y),
            new Coordinate(x + size, y + size), new Coordinate(x, y + size), new Coordinate(x, y)});
    }

    private static Feature feature(FeatureType type, String name, Geometry geometry) {
        geometry.setUserData(CRS84);
        final Feature feature = type.newInstance();
        feature.setPropertyValue("geom", geometry);
        feature.setPropertyValue("name", name);
        return feature;
    }

    /**
     * @return names of the features in the tiles of the level
     */
    private static Set<String> generate(FeatureSet source, double minimumFootprint, int level) throws Exception {
        final InMemoryTiledFeatureSet tiled = new InMemoryTiledFeatureSet(source.getType());
        final WritableTileMatrixSet tms = tiled.createTileMatrixSet(TileMatrices.createWorldWGS84Template(level));
        final FeatureSetTileGenerator generator = new FeatureSetTileGenerator(source);
        generator.setMinimumFootprint(minimumFootprint);
        generator.generate(tms, AREA, null, null);

        TileMatrix matrix = null;
        for (TileMatrix m : tms.getTileMatrices().values()) {
            if (matrix == null || TileMatrices.getScale(m) < TileMatrices.getScale(matrix)) matrix = m;
        }
        final Set<String> names = new HashSet<>();
        try (Stream<Tile> tiles = matrix.getTiles(TileMatrices.getTilesInEnvelope(matrix, AREA), false)) {
            tiles.forEach(tile -> {
                try (Stream<Feature> features = ((FeatureSet) tile.getResource()).features(false)) {
                    features.forEach(f -> names.add((String) f.getPropertyValue("name")));
                } catch (DataStoreException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        return names;
    }
}
