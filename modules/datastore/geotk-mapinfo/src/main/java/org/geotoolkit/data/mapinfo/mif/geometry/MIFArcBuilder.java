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
import org.opengis.feature.type.*;
import org.opengis.referencing.operation.MathTransform;

import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.logging.Level;

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
    private static final AttributeDescriptor PEN;

    static {
        final AttributeType angleType = new DefaultAttributeType(new DefaultName("ANGLE"), Double.class,false, false, null, null, null);

        BEGIN_ANGLE = new DefaultAttributeDescriptor(angleType, BEGIN_ANGLE_NAME, 1, 1, false, null);
        END_ANGLE = new DefaultAttributeDescriptor(angleType, END_ANGLE_NAME, 1, 1, false, null);

        PEN = new DefaultAttributeDescriptor(STRING_TYPE, Pen.NAME, 1, 1, true, null);
    }

    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {

        final double[] linePts = new double[4];
        try {
            for (int i = 0; i < linePts.length; i++) {
                linePts[i] = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
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
            Double beginAngle = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
            Double endAngle   = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
            toFill.getProperty(BEGIN_ANGLE_NAME).setValue(beginAngle);
            toFill.getProperty(END_ANGLE_NAME).setValue(endAngle);
        } catch (InputMismatchException ex) {
            throw new DataStoreException("Arc is not properly defined : not enough points found.", ex);
        }

        if(scanner.hasNext(Pen.PEN_PATTERN) && toFill.getType().getDescriptors().contains(PEN)) {
            String args = scanner.next(Pen.PEN_PATTERN);
            String[] argsTab = args.substring(args.indexOf('(')+1, args.length()-1).trim().split(",");
            if (argsTab.length < 3) {
                LOGGER.log(Level.WARNING, "A PEN tag have been found, but can't be read (bad syntax ?). Ignore style.");
            }
            else {
                final int width = Integer.decode(argsTab[0]);
                final int pattern = Integer.decode(argsTab[1]);
                final int color = Integer.decode(argsTab[2]);
                Pen pen = new Pen(width, pattern, color);
                toFill.getProperty(Pen.NAME).setValue(pen);
            }
        }
    }

    @Override
    public String toMIFSyntax(Feature source) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Source feature", source);
        if(source.getDefaultGeometryProperty() == null) {
            throw new DataStoreException("Input feature does not contain any geometry.");
        }

        if (source.getProperty(BEGIN_ANGLE_NAME) == null || source.getProperty(END_ANGLE_NAME) == null) {
            throw new DataStoreException("Not enough information to build an arc (missing angle).");
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
            throw new DataStoreException("Unable to build an arc with the current geometry (Non compatible type"+value.getClass()+").");
        }
        builder.append('\n');

        builder.append(source.getProperty(BEGIN_ANGLE_NAME).getValue()).append(' ')
                .append(source.getProperty(BEGIN_ANGLE_NAME).getValue()).append('\n');

        if(source.getProperty(Pen.NAME) != null) {
            Object penValue = source.getProperty(Pen.NAME).getValue();
            if(penValue != null && penValue instanceof Pen) {
                builder.append(penValue).append('\n');
            }
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

    public Name getName() {
        return NAME;
    }

    @Override
    protected List<AttributeDescriptor> getAttributes() {
        List<AttributeDescriptor> descList = new ArrayList<AttributeDescriptor>(3);
        descList.add(BEGIN_ANGLE);
        descList.add(END_ANGLE);
        descList.add(PEN);

        return descList;
    }
}
