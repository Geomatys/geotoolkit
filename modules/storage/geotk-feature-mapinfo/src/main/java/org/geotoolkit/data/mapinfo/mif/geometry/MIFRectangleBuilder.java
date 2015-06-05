/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.mapinfo.mif.geometry;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.data.mapinfo.mif.style.Brush;
import org.geotoolkit.data.mapinfo.mif.style.Pen;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.Name;
import org.opengis.referencing.operation.MathTransform;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.feature.type.FeatureType;

/**
 * The class used to build Feature from MIF rectangle or MIF Round rectangle.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 27/02/13
 */
public class MIFRectangleBuilder extends MIFGeometryBuilder {
    public Name NAME = DefaultName.create("RECTANGLE");
    public static final Name ROUND_NAME = DefaultName.create("ROUNDING");

    private static final AttributeDescriptor ROUNDING;
    private static final AttributeDescriptor PEN;
    private static final AttributeDescriptor BRUSH;

    static {
        ROUNDING = new DefaultAttributeDescriptor(STRING_TYPE, ROUND_NAME, 1, 1, true, null);

        PEN = new DefaultAttributeDescriptor(STRING_TYPE, Pen.NAME, 1, 1, true, null);

        BRUSH = new DefaultAttributeDescriptor(STRING_TYPE, Brush.NAME, 1, 1, true, null);
    }

    public FeatureType featureType;

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
            toFill.getDefaultGeometryProperty().setValue(env);

        } catch (InputMismatchException ex) {
            throw new DataStoreException("Rectangle is not properly defined : not enough points found.", ex);
        }

        if(scanner.hasNext(ProjectionUtils.DOUBLE_PATTERN)) {
            toFill.getProperty(ROUND_NAME).setValue(Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN)));
        }

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

        if(scanner.hasNext(Brush.BRUSH_PATTERN) && toFill.getType().getDescriptors().contains(BRUSH)) {
            String args = scanner.next()+scanner.nextLine();
            String[] argsTab = args.substring(args.indexOf('(')+1, args.length()-1)
                    .replaceAll("[^\\d^,]+", "")
                    .split(",");
            if (argsTab.length < 2) {
                LOGGER.log(Level.WARNING, "A BRUSH tag have been found, but can't be read (bad syntax ?). Ignore style.");
            }
            else {
                final int pattern = Integer.decode(argsTab[0]);
                final int foreground = Integer.decode(argsTab[1]);
                Brush brush = new Brush(pattern, foreground);
                if(argsTab.length > 2) {
                    final int background = Integer.decode(argsTab[2]);
                    brush.setBackgroundCC(background);
                }
                toFill.getProperty(Brush.NAME).setValue(brush);
            }
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

        if(source.getProperty(Pen.NAME) != null) {
            Object penValue = source.getProperty(Pen.NAME).getValue();
            if(penValue != null && penValue instanceof Pen) {
                builder.append(penValue).append('\n');
            }
        }

        if(source.getProperty(Brush.NAME) != null) {
            Object brValue = source.getProperty(Brush.NAME).getValue();
            if(brValue != null && brValue instanceof Brush) {
                builder.append(brValue).append('\n');
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
