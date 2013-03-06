package org.geotoolkit.data.mif.geometry;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
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

import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Util class to build a feature from Line object of a MIF file.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 26/02/13
 */
public class MIFLineBuilder extends MIFGeometryBuilder {

    public static final Name NAME = new DefaultName("LINE");
    public static final Name PEN_NAME = new DefaultName("PEN");
    private static final AttributeDescriptor PEN;

    static {
        final AttributeType penType =
                new DefaultAttributeType(PEN_NAME, Pen.class, false, false, null, null, null);
        PEN = new DefaultAttributeDescriptor(penType, PEN_NAME, 0, 1, true, null);
    }

    public SimpleFeatureType featureType;

    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {

        final double[] linePts = new double[4];
        try {
            for (int i = 0; i < linePts.length; i++) {
                linePts[i] = scanner.nextDouble();
            }
        } catch (InputMismatchException ex) {
            throw new DataStoreException("Line is not properly defined : not enough points found.", ex);
        }

        final CoordinateSequence seq = new PackedCoordinateSequence.Double(linePts, 2);
        final LineString line = GEOMETRY_FACTORY.createLineString(seq);
        toFill.getProperty(NAME).setValue(line);

        if(scanner.hasNext(PEN_NAME.getLocalPart())) {
            /** todo : parse PEN tag. */
        }
    }

    @Override
    public FeatureType buildType(CoordinateReferenceSystem crs, FeatureType parent) {
        GeometryType geomType = new DefaultGeometryType(NAME, LineString.class, crs, true, false, null, null, null);
        final GeometryDescriptor geomDesc = new DefaultGeometryDescriptor(geomType, NAME, 1, 1, true, null);

        featureType = new DefaultSimpleFeatureType(NAME, Collections.singletonList(PEN), geomDesc, false, null, parent, null);
        return featureType;
    }

    @Override
    public Class getGeometryBinding() {
        return LineString.class;
    }

    @Override
    public Name getName() {
        return NAME;
    }
}
