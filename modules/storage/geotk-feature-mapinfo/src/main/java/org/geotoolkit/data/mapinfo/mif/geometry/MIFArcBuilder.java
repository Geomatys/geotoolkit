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
import org.geotoolkit.data.mapinfo.mif.style.Pen;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;
import org.opengis.referencing.operation.MathTransform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import org.apache.sis.feature.DefaultAttributeType;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.geotoolkit.geometry.jts.JTS;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 26/02/13
 */
public class MIFArcBuilder extends MIFRectangleBuilder {

    public static final GenericName NAME = NamesExt.create("ARC");
    public static final GenericName BEGIN_ANGLE_NAME = NamesExt.create("BEGIN_ANGLE");
    public static final GenericName END_ANGLE_NAME = NamesExt.create("END_ANGLE");

    public static final AttributeType BEGIN_ANGLE = new DefaultAttributeType(Collections.singletonMap("name", BEGIN_ANGLE_NAME), Double.class, 1, 1, null);
    public static final AttributeType END_ANGLE = new DefaultAttributeType(Collections.singletonMap("name", END_ANGLE_NAME), Double.class, 1, 1, null);
    private static final AttributeType PEN = new DefaultAttributeType(Collections.singletonMap("name", Pen.NAME), Pen.class, 1, 1, null);


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
            toFill.setPropertyValue(FeatureExt.getDefaultGeometry(toFill.getType()).getName().tip().toString(), JTS.toGeometry(line));

            // Get arc angles
            Double beginAngle = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
            Double endAngle   = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
            toFill.setPropertyValue(BEGIN_ANGLE_NAME.toString(),beginAngle);
            toFill.setPropertyValue(END_ANGLE_NAME.toString(),endAngle);
        } catch (InputMismatchException ex) {
            throw new DataStoreException("Arc is not properly defined : not enough points found.", ex);
        }

        if(scanner.hasNext(Pen.PEN_PATTERN) && toFill.getType().getProperties(true).contains(PEN)) {
            String args = scanner.nextLine();
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
    }

    @Override
    public String toMIFSyntax(Feature source) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Source feature", source);
        if(FeatureExt.hasAGeometry(source.getType())) {
            throw new DataStoreException("Input feature does not contain any geometry.");
        }

        final Object beginAngle = MIFUtils.getPropertySafe(source, BEGIN_ANGLE_NAME.toString());
        final Object endAngle = MIFUtils.getPropertySafe(source, END_ANGLE_NAME.toString());
        if (beginAngle == null || endAngle == null) {
            throw new DataStoreException("Not enough information to build an arc (missing angle).");
        }

        StringBuilder builder = new StringBuilder(NAME.tip().toString()).append(' ');
        appendMIFEnvelope(builder, MIFUtils.getGeometryValue(source));
        builder.append('\n');

        builder.append(beginAngle).append(' ')
                .append(endAngle).append('\n');

        final Object pen = MIFUtils.getPropertySafe(source, Pen.NAME.toString());
        if (pen instanceof Pen) {
            builder.append(pen).append('\n');
        }

        return builder.toString();
    }

    public GenericName getName() {
        return NAME;
    }

    @Override
    protected List<AttributeType> getAttributes() {
        List<AttributeType> descList = new ArrayList<AttributeType>(3);
        descList.add(BEGIN_ANGLE);
        descList.add(END_ANGLE);
        descList.add(PEN);

        return descList;
    }
}
