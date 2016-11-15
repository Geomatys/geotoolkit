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

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.data.mapinfo.mif.style.Pen;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;
import org.opengis.referencing.operation.MathTransform;

import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import org.apache.sis.feature.DefaultAttributeType;
import org.apache.sis.internal.feature.AttributeConvention;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;

/**
 * Util class to build a feature from Line object of a MIF file.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 26/02/13
 */
public class MIFLineBuilder extends MIFGeometryBuilder {

    public static final GenericName NAME = NamesExt.create("LINE");

    private static final AttributeType PEN = new DefaultAttributeType(Collections.singletonMap("name", Pen.NAME), String.class, 1, 1, null);

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

        toFill.setPropertyValue(MIFUtils.findGeometryProperty(toFill.getType()).getName().tip().toString(), line);

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
                final Pen pen = new Pen(width, pattern, color);
                toFill.setPropertyValue(Pen.NAME.toString(),pen);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toMIFSyntax(Feature feature) throws DataStoreException {
        super.toMIFSyntax(feature);
        StringBuilder builder = new StringBuilder(NAME.tip().toString());

        Object value = MIFUtils.getGeometryValue(feature);

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

        final Object pen = MIFUtils.getPropertySafe(feature, Pen.NAME.toString());
        if (pen instanceof Pen) {
            builder.append(pen).append('\n');
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
    public GenericName getName() {
        return NAME;
    }

    @Override
    protected List<AttributeType> getAttributes() {
        return Collections.singletonList(PEN);
    }
}
