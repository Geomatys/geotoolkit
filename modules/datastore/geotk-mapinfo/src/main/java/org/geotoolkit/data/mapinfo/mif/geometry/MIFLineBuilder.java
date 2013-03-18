package org.geotoolkit.data.mapinfo.mif.geometry;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.data.mapinfo.mif.style.Pen;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.referencing.operation.MathTransform;

import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
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
                linePts[i] = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toMIFSyntax(Feature geometry) throws DataStoreException {
        super.toMIFSyntax(geometry);
        StringBuilder builder = new StringBuilder(NAME.getLocalPart());
        LineString line = (LineString) geometry.getDefaultGeometryProperty().getValue();
        Point pt1 =  line.getStartPoint();
        Point pt2 =  line.getEndPoint();
        builder.append(' ').append(pt1.getX()).append(' ').append(pt1.getY())
                .append(' ').append(pt2.getX()).append(pt2.getY()).append('\n');
        return builder.toString();
    }

    @Override
    public Class getGeometryBinding() {
        return LineString.class;
    }

    @Override
    public Class[] getPossibleBindings() {
        return new Class[]{LineString.class};
    }

    @Override
    public Name getName() {
        return NAME;
    }

    @Override
    protected List<AttributeDescriptor> getAttributes() {
        return Collections.singletonList(PEN);
    }
}
