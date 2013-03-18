package org.geotoolkit.data.mapinfo.mif.geometry;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.data.mapinfo.mif.style.Pen;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.referencing.operation.MathTransform;

import java.awt.geom.Rectangle2D;
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
                pts[i] = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
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

        if(scanner.hasNext(ProjectionUtils.DOUBLE_PATTERN)) {
            toFill.getProperty(ROUND_NAME).setValue(Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN)));
        }

        if(scanner.hasNext(PEN_NAME.getLocalPart())) {
            /** todo : parse PEN tag. */
        }
    }


    @Override
    public String toMIFSyntax(Feature source) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Source feature", source);
        if(source.getDefaultGeometryProperty() == null) {
            throw new DataStoreException("Input feature does not contain any geometry.");
        }

        StringBuilder builder = new StringBuilder(NAME.getLocalPart()).append(' ');
        Object value = source.getDefaultGeometryProperty().getValue();
        if(value instanceof Envelope) {
            Envelope env = (Envelope) value;
            builder.append(env.getMinX()).append(' ')
                    .append(env.getMinY()).append(' ')
                    .append(env.getMaxX()).append(' ')
                    .append(env.getMaxY());
        } else if (value instanceof Rectangle2D) {
            Rectangle2D rect = (Rectangle2D) value;
            builder.append(rect.getMinX()).append(' ')
                    .append(rect.getMinY()).append(' ')
                    .append(rect.getMaxX()).append(' ')
                    .append(rect.getMaxY());
        } else if(value instanceof Envelope2D) {
            Envelope2D env = (Envelope2D) value;
            builder.append(env.getMinX()).append(' ')
                    .append(env.getMinY()).append(' ')
                    .append(env.getMaxX()).append(' ')
                    .append(env.getMaxY());
        } else {
            throw new DataStoreException("Unable to build a rectangle with the current geometry (Non compatible type"+value.getClass()+").");
        }
        builder.append('\n');

        Property round = source.getProperty(ROUND_NAME);
        if(round != null) {
            builder.append(round.getValue()).append('\n');
        }

        return builder.toString();
    }

    @Override
    public Class getGeometryBinding() {
        return Envelope.class;
    }

    @Override
    public Class[] getPossibleBindings() {
        return new Class[]{Envelope.class, Envelope2D.class, Rectangle2D.class};
    }

    @Override
    public Name getName() {
        return NAME;
    }

    @Override
    protected List<AttributeDescriptor> getAttributes() {

        final List<AttributeDescriptor> descList = new ArrayList<AttributeDescriptor>(3);
        descList.add(ROUNDING);
        descList.add(PEN);
        descList.add(BRUSH);

        return descList;
    }
}
