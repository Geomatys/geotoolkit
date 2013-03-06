package org.geotoolkit.data.mif.geometry;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
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

import java.util.*;

/**
 * The class used to build Feature from MIF rectangle or MIF Round rectangle.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 27/02/13
 */
public class MIFRectangleBuilder extends MIFGeometryBuilder {
    public Name NAME = new DefaultName("RECTANGLE");
    public static final Name ROUND_NAME = new DefaultName("ROUNDING");
    public static final Name PEN_NAME = new DefaultName("PEN");
    public static final Name BRUSH_NAME = new DefaultName("BRUSH");

    private static final AttributeDescriptor ROUNDING;
    private static final AttributeDescriptor PEN;
    private static final AttributeDescriptor BRUSH;

    static {
        final AttributeType roundType = new DefaultAttributeType(ROUND_NAME, Double.class, false, false, null, null, null);
        ROUNDING = new DefaultAttributeDescriptor(roundType, ROUND_NAME, 0, 1, true, null);

        final AttributeType penType = new DefaultAttributeType(PEN_NAME, Pen.class, false, false, null, null, null);
        PEN = new DefaultAttributeDescriptor(penType, PEN_NAME, 0, 1, true, null);

        final AttributeType brushType = new DefaultAttributeType(BRUSH_NAME, Pen.class, false, false, null, null, null);
        BRUSH = new DefaultAttributeDescriptor(penType, BRUSH_NAME, 0, 1, true, null);
    }

    public SimpleFeatureType featureType;

    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {

        final double[] pts = new double[4];
        try {
            for (int i = 0; i < pts.length; i++) {
                pts[i] = scanner.nextDouble();
            }

            final CoordinateSequence seq;
            if(toApply != null) {
                try {
                    double[] afterT = new double[4];
                    toApply.transform(pts, 0, afterT, 0, 2);
                    seq = new PackedCoordinateSequence.Double(afterT, 2);
                } catch (Exception e) {
                    throw new DataStoreException("Unable to transform geometry", e);
                }
            } else {
                seq = new PackedCoordinateSequence.Double(pts, 2);
            }
            final Envelope env = new Envelope(seq.getCoordinate(0), seq.getCoordinate(1));
            toFill.getProperty(NAME).setValue(env);

        } catch (InputMismatchException ex) {
            throw new DataStoreException("Rectangle is not properly defined : not enough points found.", ex);
        }

        if(scanner.hasNextDouble()) {
            toFill.getProperty(ROUND_NAME).setValue(scanner.nextDouble());
        }

        if(scanner.hasNext(PEN_NAME.getLocalPart())) {
            /** todo : parse PEN tag. */
        }
    }

    @Override
    public FeatureType buildType(CoordinateReferenceSystem crs, FeatureType parent) {
        GeometryType geomType = new DefaultGeometryType(NAME, Envelope.class, crs, true, false, null, null, null);
        final GeometryDescriptor geomDesc = new DefaultGeometryDescriptor(geomType, NAME, 1, 1, true, null);

        final List<AttributeDescriptor> descList = new ArrayList<AttributeDescriptor>(2);
        descList.add(ROUNDING);
        descList.add(STYLE);

        featureType = new DefaultSimpleFeatureType(NAME, descList, geomDesc, false, null, parent, null);
        return featureType;
    }

    @Override
    public Class getGeometryBinding() {
        return Envelope.class;
    }

    @Override
    public Name getName() {
        return NAME;
    }
}
