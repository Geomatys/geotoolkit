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
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 26/02/13
 */
public class MIFArcBuilder extends MIFGeometryBuilder {

    public static final Name NAME = new DefaultName("ARC");
    public static final Name BEGIN_ANGLE_NAME = new DefaultName("BEGIN_ANGLE");
    public static final Name END_ANGLE_NAME = new DefaultName("END_ANGLE");

    public static final AttributeDescriptor BEGIN_ANGLE;
    public static final AttributeDescriptor END_ANGLE;

    static {
        final AttributeType angleType = new DefaultAttributeType(new DefaultName("ANGLE"), Double.class,false, false, null, null, null);

        BEGIN_ANGLE = new DefaultAttributeDescriptor(angleType, BEGIN_ANGLE_NAME, 1, 1, false, null);
        END_ANGLE = new DefaultAttributeDescriptor(angleType, END_ANGLE_NAME, 1, 1, false, null);
    }

    public SimpleFeatureType featureType;

    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {

        final double[] linePts = new double[4];
        try {
            for (int i = 0; i < linePts.length; i++) {
                linePts[i] = scanner.nextDouble();
            }

            final CoordinateSequence seq;
            if(toApply != null) {
                try {
                double[] afterT = new double[4];
                toApply.transform(linePts, 0, afterT, 0, 2);
                seq = new PackedCoordinateSequence.Double(afterT, 2);
                } catch (Exception e) {
                    throw new DataStoreException("Unable to transform geometry", e);
                }
            } else {
                seq = new PackedCoordinateSequence.Double(linePts, 2);
            }
            final Envelope line = new Envelope(seq.getCoordinate(0), seq.getCoordinate(1));
            toFill.getProperty(NAME).setValue(line);

            // Get arc angles
            Double beginAngle = scanner.nextDouble();
            Double endAngle   = scanner.nextDouble();
            toFill.getProperty(BEGIN_ANGLE_NAME).setValue(beginAngle);
            toFill.getProperty(END_ANGLE_NAME).setValue(endAngle);
        } catch (InputMismatchException ex) {
            throw new DataStoreException("Arc is not properly defined : not enough points found.", ex);
        }

        if(scanner.hasNext("PEN")) {
            /** todo : parse PEN tag. */
        }
    }

    @Override
    public FeatureType buildType(CoordinateReferenceSystem crs, FeatureType parent) {
        final Name name = getName();
        GeometryType geomType = new DefaultGeometryType(name, getGeometryBinding(), crs, true, false, null, null, null);
        final GeometryDescriptor geomDesc = new DefaultGeometryDescriptor(geomType, name, 1, 1, true, null);

        List<AttributeDescriptor> descList = new ArrayList<AttributeDescriptor>(3);
        descList.add(BEGIN_ANGLE);
        descList.add(END_ANGLE);
        descList.add(STYLE);
        featureType = new DefaultSimpleFeatureType(name, descList, geomDesc, false, null, parent, null);
        return featureType;
    }

    @Override
    public Class getGeometryBinding() {
        return Envelope.class;
    }

    public Name getName() {
        return NAME;
    }
}
