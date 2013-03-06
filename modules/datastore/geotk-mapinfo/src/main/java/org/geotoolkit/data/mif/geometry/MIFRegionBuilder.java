package org.geotoolkit.data.mif.geometry;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import org.geotoolkit.data.mif.style.Brush;
import org.geotoolkit.data.mif.style.Pen;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.geotoolkit.feature.type.DefaultGeometryDescriptor;
import org.geotoolkit.feature.type.DefaultGeometryType;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 26/02/13
 */
public class MIFRegionBuilder extends MIFGeometryBuilder {

    public static final Name NAME = new DefaultName("REGION");
    public static final Name PEN_NAME = new DefaultName("PEN");
    public static final Name BRUSH_NAME = new DefaultName("BRUSH");

    private static final AttributeDescriptor BRUSH;
    private static final AttributeDescriptor PEN;

    static {
        final AttributeType penType =
                new DefaultAttributeType(PEN_NAME, Pen.class, false, false, null, null, null);
        PEN = new DefaultAttributeDescriptor(penType, PEN_NAME, 0, 1, true, null);

        final AttributeType smoothType =
                new DefaultAttributeType(BRUSH_NAME, Brush.class, false, false, null, null, null);
        BRUSH = new DefaultAttributeDescriptor(smoothType, BRUSH_NAME, 1, 1, false, Boolean.FALSE);
    }

    public SimpleFeatureType featureType;

    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {

        final int numPolygons = scanner.nextInt();
        final Polygon[] polygons = new Polygon[numPolygons];
        for (int polygonCount = 0; polygonCount < numPolygons; polygonCount++) {

            final int numCoords = scanner.nextInt();
            final double[] polygonPts = new double[numCoords];
            for (int coordCount = 0; coordCount < numCoords; coordCount++) {
                polygonPts[coordCount] = scanner.nextDouble();
            }

            final CoordinateSequence seq;
            if (toApply != null) {
                try {
                    double[] afterT = new double[numCoords];
                    toApply.transform(polygonPts, 0, afterT, 0, numCoords / 2);
                    seq = new PackedCoordinateSequence.Double(afterT, 2);
                } catch (Exception e) {
                    throw new DataStoreException("Unable to transform geometry", e);
                }
            } else {
                seq = new PackedCoordinateSequence.Double(polygonPts, 2);
            }
            final LinearRing ring = GEOMETRY_FACTORY.createLinearRing(seq);
            GEOMETRY_FACTORY.createPolygon(ring, null);
        }

        toFill.getProperty(NAME).setValue(GEOMETRY_FACTORY.createMultiPolygon(polygons));
    }

    @Override
    public Class getGeometryBinding() {
        return MultiPolygon.class;
    }

    @Override
    public Name getName() {
        return NAME;
    }
}
