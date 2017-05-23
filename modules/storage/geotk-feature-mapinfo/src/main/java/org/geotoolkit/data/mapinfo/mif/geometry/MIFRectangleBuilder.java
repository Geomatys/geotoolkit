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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.data.mapinfo.mif.style.Brush;
import org.geotoolkit.data.mapinfo.mif.style.Pen;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;
import org.opengis.referencing.operation.MathTransform;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import org.apache.sis.feature.DefaultAttributeType;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;

import static org.geotoolkit.data.mapinfo.mif.style.Brush.BRUSH;
import static org.geotoolkit.data.mapinfo.mif.style.Pen.PEN;
import org.geotoolkit.geometry.jts.JTS;

/**
 * The class used to build Feature from MIF rectangle or MIF Round rectangle.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 27/02/13
 */
public class MIFRectangleBuilder extends MIFGeometryBuilder {
    public GenericName NAME = NamesExt.create("RECTANGLE");
    public static final GenericName ROUND_NAME = NamesExt.create("ROUNDING");

    private static final AttributeType ROUNDING = new DefaultAttributeType(Collections.singletonMap("name", ROUND_NAME), Float.class, 1, 1, null);

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
            toFill.setPropertyValue(MIFUtils.findGeometryProperty(toFill.getType()).getName().tip().toString(), JTS.toGeometry(env));

        } catch (InputMismatchException ex) {
            throw new DataStoreException("Rectangle is not properly defined : not enough points found.", ex);
        }

        if(scanner.hasNext(ProjectionUtils.DOUBLE_PATTERN)) {
            toFill.setPropertyValue(ROUND_NAME.toString(),Float.parseFloat(scanner.next(ProjectionUtils.DOUBLE_PATTERN)));
        }

        if(scanner.hasNext(Pen.PEN_PATTERN) && toFill.getType().getProperties(true).contains(PEN)) {
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
                toFill.setPropertyValue(Pen.NAME.toString(),pen);
            }
        }

        if(scanner.hasNext(Brush.BRUSH_PATTERN) && toFill.getType().getProperties(true).contains(BRUSH)) {
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
                toFill.setPropertyValue(Brush.NAME.toString(),brush);
            }
        }
    }


    @Override
    public String toMIFSyntax(Feature source) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Source feature", source);
        if(!FeatureExt.hasAGeometry(source.getType())) {
            throw new DataStoreException("Input feature does not contain any geometry.");
        }

        StringBuilder builder = new StringBuilder(NAME.tip().toString()).append(' ');
        appendMIFEnvelope(builder, MIFUtils.getGeometryValue(source));
        builder.append('\n');

        final Object round = MIFUtils.getPropertySafe(source, ROUND_NAME.toString());
        if(round != null) {
            builder.append(round).append('\n');
        }

        final Object pen = MIFUtils.getPropertySafe(source, Pen.NAME.toString());
        if (pen instanceof Pen) {
            builder.append(pen).append('\n');
        }

        final Object brush = MIFUtils.getPropertySafe(source, Brush.NAME.toString());
        if (brush instanceof Brush) {
            builder.append(brush).append('\n');
        }

        return builder.toString();
    }

    /**
     * Append MIF-MID representation of given object in input string builder.
     * @param toAppendInto The builder to add data into.
     * @param sourceEnvelope The object which represents the envelope to write.
     * @throws NullPointerException If any input is null.
     * @throws DataStoreException If input object is not manageable for now. For
     * an overview of possible types, see {@link #getPossibleBindings() }.
     */
    protected void appendMIFEnvelope(final StringBuilder toAppendInto, Object sourceEnvelope) throws DataStoreException, NullPointerException {
        if (sourceEnvelope instanceof Geometry)
            sourceEnvelope = ((Geometry) sourceEnvelope).getEnvelopeInternal();
        if (sourceEnvelope instanceof com.esri.core.geometry.Envelope) {
            final com.esri.core.geometry.Envelope env = (com.esri.core.geometry.Envelope) sourceEnvelope;
            toAppendInto.append(env.getXMin()).append(' ')
                    .append(env.getYMin()).append(' ')
                    .append(env.getXMax()).append(' ')
                    .append(env.getYMax());
        } else if (sourceEnvelope instanceof Envelope) {
            Envelope env = (Envelope) sourceEnvelope;
            toAppendInto.append(env.getMinX()).append(' ')
                    .append(env.getMinY()).append(' ')
                    .append(env.getMaxX()).append(' ')
                    .append(env.getMaxY());
        } else if (sourceEnvelope instanceof Rectangle2D) {
            Rectangle2D rect = (Rectangle2D) sourceEnvelope;
            toAppendInto.append(rect.getMinX()).append(' ')
                    .append(rect.getMinY()).append(' ')
                    .append(rect.getMaxX()).append(' ')
                    .append(rect.getMaxY());
        } else if (sourceEnvelope instanceof Envelope2D) {
            Envelope2D env = (Envelope2D) sourceEnvelope;
            toAppendInto.append(env.getMinX()).append(' ')
                    .append(env.getMinY()).append(' ')
                    .append(env.getMaxX()).append(' ')
                    .append(env.getMaxY());
        } else if (sourceEnvelope == null) {
            throw new NullArgumentException("Input envelope is null !");
        } else {
            throw new DataStoreException("Unable to build a rectangle with the current geometry (Non compatible type" + sourceEnvelope.getClass() + ").");
        }
    }

    @Override
    public Class getGeometryBinding() {
        return Geometry.class; // TODO : replace with esri envelope once rendering engine supports it.
    }

    @Override
    public Class[] getPossibleBindings() {
        return new Class[]{Geometry.class, com.esri.core.geometry.Envelope.class, Envelope.class, Envelope2D.class, Rectangle2D.class};
    }

    @Override
    public GenericName getName() {
        return NAME;
    }

    @Override
    protected List<AttributeType> getAttributes() {

        final List<AttributeType> descList = new ArrayList<>(3);
        descList.add(ROUNDING);
        descList.add(PEN);
        descList.add(BRUSH);

        return descList;
    }
}
