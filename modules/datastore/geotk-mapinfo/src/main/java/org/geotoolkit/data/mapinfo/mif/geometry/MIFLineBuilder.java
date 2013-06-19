package org.geotoolkit.data.mapinfo.mif.geometry;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.data.mapinfo.mif.style.Pen;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;
import org.opengis.referencing.operation.MathTransform;

import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * Util class to build a feature from Line object of a MIF file.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 26/02/13
 */
public class MIFLineBuilder extends MIFGeometryBuilder {

    public static final Name NAME = new DefaultName("LINE");

    private static final AttributeDescriptor PEN;
    static {
        final AttributeType penType =
                new DefaultAttributeType(Pen.NAME, String.class, false, false, null, null, null);
        PEN = new DefaultAttributeDescriptor(penType, Pen.NAME, 1, 1, true, null);
    }

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

        if(scanner.hasNext(Pen.PEN_PATTERN) && toFill.getType().getDescriptors().contains(PEN)) {
            String args = scanner.next()+scanner.nextLine();
            String[] argsTab = args.substring(args.indexOf('(')+1, args.length()-1)
                    .replaceAll("[^\\d^,]+", "")
                    .split(",");
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toMIFSyntax(Feature geometry) throws DataStoreException {
        super.toMIFSyntax(geometry);
        StringBuilder builder = new StringBuilder(NAME.getLocalPart());

        Object value = geometry.getDefaultGeometryProperty().getValue();

        if(value instanceof LineSegment) {
            LineSegment line = (LineSegment) value;
            Coordinate pt1 =  line.p0;
            Coordinate pt2 =  line.p1;
            builder.append(' ').append(line.p0.x).append(' ').append(line.p0.y)
                    .append(' ').append(line.p1.x).append(' ').append(line.p1.y).append('\n');
        } else if (value instanceof LineString) {
            LineString line = (LineString) value;
            Point pt1 =  line.getStartPoint();
            Point pt2 =  line.getEndPoint();
            builder.append(' ').append(pt1.getX()).append(' ').append(pt1.getY())
                    .append(' ').append(pt2.getX()).append(' ').append(pt2.getY()).append('\n');
        }

        if(geometry.getProperty(Pen.NAME) != null) {
            Object penValue = geometry.getProperty(Pen.NAME).getValue();
            if(penValue != null && penValue instanceof Pen) {
                builder.append(penValue).append('\n');
            }
        }

        return builder.toString();
    }

    @Override
    public Class getGeometryBinding() {
        return LineString.class;
    }

    @Override
    public Class[] getPossibleBindings() {
        return new Class[]{LineString.class, LineSegment.class};
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
