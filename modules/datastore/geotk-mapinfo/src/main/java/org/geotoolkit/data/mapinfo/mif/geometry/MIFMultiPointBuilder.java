package org.geotoolkit.data.mapinfo.mif.geometry;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.data.mapinfo.mif.style.Symbol;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.referencing.operation.MathTransform;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Create collection of points from MIF MultiPoint
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 27/02/13
 */
public class MIFMultiPointBuilder extends MIFGeometryBuilder {

    public static final Name NAME = new DefaultName("MULTIPOINT");
    public static final Name SYMBOL_NAME = new DefaultName("SYMBOL");
    public static final AttributeDescriptor SYMBOL_DESCRIPTOR;

    private SimpleFeatureType featureType = null;

    static {
        final DefaultAttributeType symbolType = new DefaultAttributeType(SYMBOL_NAME, Symbol.class, true, false, null, null, null);
        SYMBOL_DESCRIPTOR = new DefaultAttributeDescriptor(symbolType, SYMBOL_NAME, 0, 1, true, null);
    }

    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {
        try {
            final int numCoords = scanner.nextInt()*2;
            double[] coords = new double[numCoords];
            for(int ptCount = 0 ; ptCount < numCoords ; ptCount++) {
                coords[ptCount] = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
            }

            final CoordinateSequence seq;
            if(toApply != null) {
                try {
                    double[] afterT = new double[numCoords];
                    toApply.transform(coords, 0, afterT, 0, numCoords/2);
                    seq = new PackedCoordinateSequence.Double(afterT, 2);
                } catch (Exception e) {
                    throw new DataStoreException("Unable to transform geometry", e);
                }
            } else {
                seq = new PackedCoordinateSequence.Double(coords, 2);
            }

            toFill.getProperty(NAME).setValue(GEOMETRY_FACTORY.createMultiPoint(seq));

            if(scanner.hasNext("\\w+") && scanner.next().equalsIgnoreCase(SYMBOL_NAME.getLocalPart())) {

            }
        } catch (Exception e) {
            throw new DataStoreException("MultiPoint instance can't be read.", e);
        }
    }

    @Override
    public String toMIFSyntax(Feature geometry) throws DataStoreException {
        super.toMIFSyntax(geometry);

        final StringBuilder builder = new StringBuilder(NAME.getLocalPart());
        final Object value = geometry.getDefaultGeometryProperty().getValue();
        final MultiPoint multiPt;
        if(value instanceof CoordinateSequence) {
            multiPt = GEOMETRY_FACTORY.createMultiPoint(((CoordinateSequence)value).toCoordinateArray());
        } else {
            multiPt = (MultiPoint) geometry.getDefaultGeometryProperty().getValue();
        }
        builder.append(multiPt.getNumGeometries()).append('\n');

        for(int i =0 ; i < multiPt.getNumGeometries(); i++) {
            Point pt = (Point) multiPt.getGeometryN(i);
            builder.append(pt.getX()).append(pt.getY()).append('\n');
        }

        return builder.toString();
    }

    @Override
    public Class getGeometryBinding() {
        return MultiPoint.class;
    }

    @Override
    public Class[] getPossibleBindings() {
        return new Class[]{MultiPoint.class, CoordinateSequence.class};
    }

    @Override
    public Name getName() {
        return NAME;
    }

    @Override
    protected List<AttributeDescriptor> getAttributes() {
        return Collections.singletonList(SYMBOL_DESCRIPTOR);
    }
}
