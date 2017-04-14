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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
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
import java.util.regex.Pattern;
import org.apache.sis.feature.DefaultAttributeType;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;

import static org.geotoolkit.data.mapinfo.mif.style.Pen.PEN;

/**
 * Util class to build a feature from Multi line object of a MIF file.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 26/02/13
 */
public class MIFPolyLineBuilder extends MIFGeometryBuilder {

    public static final GenericName NAME = NamesExt.create("PLINE");
    public static final GenericName SMOOTH_NAME = NamesExt.create("SMOOTH");

    private static final AttributeType SMOOTH = new DefaultAttributeType(Collections.singletonMap("name", SMOOTH_NAME), Boolean.class, 1, 1, null);

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
            Pattern multiple = Pattern.compile("multiple", Pattern.CASE_INSENSITIVE);
            if(scanner.hasNext(multiple)) {
                scanner.next();
                numLines = Integer.decode(scanner.next("\\d+"));
            }

            final LineString[] lineTab = new LineString[numLines];
            for(int lineCount = 0 ; lineCount < numLines ; lineCount++) {
                // We put a x2 factor as we work in 2 dimensions.
                final int numCoord = Integer.decode(scanner.next("\\d+"))*2;
                final double[] linePts = new double[numCoord];
                for(int coordCount = 0 ; coordCount < numCoord; coordCount++) {
                    linePts[coordCount] = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
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

            toFill.setPropertyValue(MIFUtils.findGeometryProperty(toFill.getType()).getName().tip().toString(), GEOMETRY_FACTORY.createMultiLineString(lineTab));

        } catch (InputMismatchException ex) {
            throw new DataStoreException("Line is not properly defined : not enough points found.", ex);
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

        if(scanner.hasNext(Pattern.compile(SMOOTH_NAME.tip().toString(), Pattern.CASE_INSENSITIVE))) {
            toFill.setPropertyValue(SMOOTH_NAME.toString(),Boolean.TRUE);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toMIFSyntax(Feature geometry) throws DataStoreException {
        super.toMIFSyntax(geometry);

        StringBuilder builder = new StringBuilder(NAME.tip().toString());

        MultiLineString polyLine = null;
        Object value = MIFUtils.getGeometryValue(geometry);
        if(value instanceof LineString) {
            polyLine = GEOMETRY_FACTORY.createMultiLineString(new LineString[]{(LineString)value});
        } else {
            polyLine = (MultiLineString) value;
            if(polyLine.getNumGeometries() > 1) {
                builder.append("MULTIPLE ").append(polyLine.getNumGeometries());
            }
        }
        builder.append('\n');

        for(int i = 0 ; i < polyLine.getNumGeometries() ; i++) {
            LineString line = (LineString) polyLine.getGeometryN(i);
            builder.append(line.getNumPoints()).append('\n');
            for(Coordinate pt : line.getCoordinates()) {
                builder.append(pt.x).append(' ').append(pt.y).append('\n');
            }
        }

        final Object pen = MIFUtils.getPropertySafe(geometry, Pen.NAME.toString());
        if (pen instanceof Pen) {
            builder.append(pen).append('\n');
        }

        final Object smooth = MIFUtils.getPropertySafe(geometry, SMOOTH_NAME.toString());
        if (Boolean.TRUE.equals(smooth)) {
            builder.append(SMOOTH_NAME.tip()).append('\n');
        }

        return builder.toString();
    }

    @Override
    public Class getGeometryBinding() {
        return MultiLineString.class;
    }

    @Override
    public Class[] getPossibleBindings() {
        return new Class[]{LineString.class, MultiLineString.class};
    }

    @Override
    public GenericName getName() {
        return NAME;
    }

    @Override
    protected List<AttributeType> getAttributes() {
        final List<AttributeType> descList = new ArrayList<AttributeType>(2);
        descList.add(PEN);
        descList.add(SMOOTH);

        return  descList;
    }
}
