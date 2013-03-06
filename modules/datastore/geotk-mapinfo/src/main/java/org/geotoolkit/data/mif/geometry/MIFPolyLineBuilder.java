package org.geotoolkit.data.mif.geometry;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
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
 * Util class to build a feature from Multi line object of a MIF file.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 26/02/13
 */
public class MIFPolyLineBuilder extends MIFGeometryBuilder {

    public static final Name NAME = new DefaultName("POLYLINE");
    public static final Name PEN_NAME = new DefaultName("PEN");
    public static final Name SMOOTH_NAME = new DefaultName("SMOOTH");

    private static final AttributeDescriptor SMOOTH;
    private static final AttributeDescriptor PEN;

    static {
        final AttributeType penType =
                new DefaultAttributeType(PEN_NAME, Pen.class, false, false, null, null, null);
        PEN = new DefaultAttributeDescriptor(penType, PEN_NAME, 0, 1, true, null);

        final AttributeType smoothType =
                new DefaultAttributeType(SMOOTH_NAME, Boolean.class, false, false, null, null, null);
        SMOOTH = new DefaultAttributeDescriptor(smoothType, SMOOTH_NAME, 1, 1, false, Boolean.FALSE);
    }

    public SimpleFeatureType featureType;

    /**
     * {@inheritDoc}
     *
     *
     *
     * @param scanner the Scanner to use for geometry parsing (should be placed on the beginning of the geometry).
     * @param toFill
     * @param toApply
     * @return
     * @throws DataStoreException
     */
    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {
        int numLines = 1;

        try {
            if(scanner.hasNext("\\w+") && "MULTIPLE".equalsIgnoreCase(scanner.next("\\w+"))) {
                numLines = scanner.nextInt();
            }

            final LineString[] lineTab = new LineString[numLines];
            for(int lineCount = 0 ; lineCount < numLines ; lineCount++) {
                // We put a x2 factor as we work in 2 dimensions.
                final int numCoord = scanner.nextInt()*2;
                final double[] linePts = new double[numCoord];
                for(int coordCount = 0 ; coordCount < numCoord; coordCount++) {
                    linePts[coordCount] = scanner.nextDouble();
                }

                final CoordinateSequence seq;
                if(toApply != null) {
                    try {
                        double[] afterT = new double[numCoord];
                        toApply.transform(linePts, 0, afterT, 0, numCoord/2);
                        seq = new PackedCoordinateSequence.Double(afterT, 2);
                    } catch (Exception e) {
                        throw new DataStoreException("Unable to transform geometry", e);
                    }
                } else {
                    seq = new PackedCoordinateSequence.Double(linePts, 2);
                }
                lineTab[lineCount] = GEOMETRY_FACTORY.createLineString(seq);
            }

            toFill.getProperty(NAME).setValue(GEOMETRY_FACTORY.createMultiLineString(lineTab));

        } catch (InputMismatchException ex) {
            throw new DataStoreException("Line is not properly defined : not enough points found.", ex);
        }

        if(scanner.hasNext(PEN_NAME.getLocalPart())) {
            /** todo : parse PEN tag. */
        }

        if(scanner.hasNext(SMOOTH_NAME.getLocalPart())) {
            toFill.getProperty(SMOOTH_NAME).setValue(Boolean.TRUE);
        } else {
            toFill.getProperty(SMOOTH_NAME).setValue(Boolean.FALSE);
        }
    }

    @Override
    public FeatureType buildType(CoordinateReferenceSystem crs, FeatureType parent) {
        GeometryType geomType = new DefaultGeometryType(NAME, MultiLineString.class, crs, true, false, null, null, null);
        final GeometryDescriptor geomDesc = new DefaultGeometryDescriptor(geomType, NAME, 1, 1, true, null);

        final List<AttributeDescriptor> descList = new ArrayList<AttributeDescriptor>(2);
        descList.add(PEN);
        descList.add(SMOOTH);

        featureType = new DefaultSimpleFeatureType(NAME, descList, geomDesc, false, null, parent, null);
        return featureType;
    }

    @Override
    public Class getGeometryBinding() {
        return MultiLineString.class;
    }

    @Override
    public Name getName() {
        return NAME;
    }
}
