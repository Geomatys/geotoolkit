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
import org.geotoolkit.data.mapinfo.mif.style.Brush;
import org.geotoolkit.data.mapinfo.mif.style.Pen;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;
import org.opengis.referencing.operation.MathTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;

import static org.geotoolkit.data.mapinfo.mif.style.Brush.BRUSH;
import static org.geotoolkit.data.mapinfo.mif.style.Pen.PEN;

/**
 * Utility class to read and write MIF-MID Region geometries.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 26/02/13
 */
public class MIFRegionBuilder extends MIFGeometryBuilder {

    public static final GenericName NAME = NamesExt.create("REGION");

    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {

        final int numPolygons = scanner.nextInt();
        final Polygon[] polygons = new Polygon[numPolygons];
        for (int polygonCount = 0; polygonCount < numPolygons; polygonCount++) {

            final int numCoords = scanner.nextInt()*2;
            final double[] polygonPts = new double[numCoords];
            for (int coordCount = 0; coordCount < numCoords; coordCount++) {
                polygonPts[coordCount] = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
            }

            final CoordinateSequence seq;
            if (toApply != null) {
                try {
                    double[] afterT = new double[numCoords];
                    toApply.transform(polygonPts, 0, afterT, 0, numCoords / 2);
                    seq = new PackedCoordinateSequence.Double(afterT, 2);
                } catch (Exception e) {
                    throw new DataStoreException("Unable to transform geometry", e);
                }
            } else {
                seq = new PackedCoordinateSequence.Double(polygonPts, 2);
            }
            final LinearRing ring = GEOMETRY_FACTORY.createLinearRing(seq);
            polygons[polygonCount] = GEOMETRY_FACTORY.createPolygon(ring, null);
        }

        toFill.setPropertyValue(MIFUtils.findGeometryProperty(toFill.getType()).getName().tip().toString(), GEOMETRY_FACTORY.createMultiPolygon(polygons));


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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toMIFSyntax(Feature geometry) throws DataStoreException {
        super.toMIFSyntax(geometry);
        StringBuilder builder = new StringBuilder(NAME.tip().toString());

        MultiPolygon multiPolygon = null;
        Object value = MIFUtils.getGeometryValue(geometry);
        if(value instanceof Polygon) {
            multiPolygon = GEOMETRY_FACTORY.createMultiPolygon(new Polygon[]{(Polygon)value});
        } else {
            multiPolygon = (MultiPolygon) value;
        }
        builder.append(' ').append(multiPolygon.getNumGeometries()).append('\n');

        for(int i = 0 ; i < multiPolygon.getNumGeometries() ; i++) {
            Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
            builder.append(polygon.getNumPoints()).append('\n');
            for(Coordinate pt : polygon.getCoordinates()) {
                builder.append(pt.x).append(' ').append(pt.y).append('\n');
            }
        }

        // Write styles.
        final Object pen = MIFUtils.getPropertySafe(geometry, Pen.NAME.toString());
        if (pen instanceof Pen) {
            builder.append(pen).append('\n');
        }

        final Object brush = MIFUtils.getPropertySafe(geometry, Brush.NAME.toString());
        if (brush instanceof Brush) {
            builder.append(brush).append('\n');
        }

        return builder.toString();
    }

    @Override
    public Class getGeometryBinding() {
        return MultiPolygon.class;
    }

    @Override
    public Class[] getPossibleBindings() {
        return new Class[]{MultiPolygon.class, Polygon.class};
    }

    @Override
    public GenericName getName() {
        return NAME;
    }

    @Override
    protected List<AttributeType> getAttributes() {
        final List<AttributeType> descList = new ArrayList<>(2);
        descList.add(PEN);
        descList.add(BRUSH);

        return descList;
    }
}
