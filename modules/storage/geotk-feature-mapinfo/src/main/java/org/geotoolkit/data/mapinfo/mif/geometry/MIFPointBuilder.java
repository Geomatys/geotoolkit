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
import com.vividsolutions.jts.geom.Point;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.data.mapinfo.mif.style.Symbol;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;
import org.opengis.referencing.operation.MathTransform;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;

import static org.geotoolkit.data.mapinfo.mif.style.Symbol.SYMBOL;
import org.geotoolkit.feature.FeatureExt;

/**
 * Util class to build a feature from Point object of a MIF file.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 25/02/13
 */
public final class MIFPointBuilder extends MIFGeometryBuilder {

    public static final GenericName NAME = NamesExt.create("POINT");

    /**
     * Build a feature describing a MIF point geometry. That assume that user gave a {@link Scanner} which is placed on
     * a POINT tag.
     *
     *
     * @param scanner The scanner to use for data reading (must be pointing on a mif POINT element).
     * @param toFill
     * @throws DataStoreException If there's a problem while parsing stream of the given Scanner.
     */
    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {
        Double x = null;
        Double y = null;
        if (scanner.hasNext(ProjectionUtils.DOUBLE_PATTERN)) {
            x = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
            if (scanner.hasNext(ProjectionUtils.DOUBLE_PATTERN)) {
                y = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
            }
        }
        if (x == null || y == null) {
            throw new DataStoreException("Unable to build point from given data");
        }

        final Coordinate result;
        if (toApply != null) {
            try {
                double[] afterT = new double[2];
                toApply.transform(new double[]{x, y}, 0, afterT, 0, 1);
                result = new Coordinate(afterT[0], afterT[1]);
            } catch (Exception e) {
                throw new DataStoreException("Unable to transform geometry.", e);
            }
        } else {
            result = new Coordinate(x, y);
        }
        final Point pt = GEOMETRY_FACTORY.createPoint(result);

        toFill.setPropertyValue(FeatureExt.getDefaultGeometry(toFill.getType()).getName().tip().toString(), pt);
        toFill.setPropertyValue(FeatureExt.getDefaultGeometry(toFill.getType()).getName().tip().toString(), pt);


        // Style
        if (scanner.hasNext(Symbol.SYMBOL_PATTERN) && toFill.getType().getProperties(true).contains(SYMBOL)) {
            String args = scanner.next() + scanner.nextLine();
            String[] argsTab = args.substring(args.indexOf('(') + 1, args.length() - 1)
                    .replaceAll("[^\\d^,]+", "")
                    .split(",");
            if (argsTab.length < 3) {
                LOGGER.log(Level.WARNING, "A PEN tag have been found, but can't be read (bad syntax ?). Ignore style.");
            } else {
                final int width = Integer.decode(argsTab[0]);
                final int pattern = Integer.decode(argsTab[1]);
                final int color = Integer.decode(argsTab[2]);
                Symbol symbol = new Symbol(width, pattern, color, null);
                toFill.setPropertyValue(Symbol.NAME.toString(), symbol);
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
        final Point pt;
        final Object value = MIFUtils.getGeometryValue(feature);
        if(value instanceof Point) {
            pt = (Point)value;
        } else {
            pt = GEOMETRY_FACTORY.createPoint((Coordinate) value);
        }
        builder.append(' ').append(pt.getX()).append(' ').append(pt.getY()).append('\n');

        final Object sValue = MIFUtils.getPropertySafe(feature, Symbol.NAME.toString());
        if(sValue instanceof Symbol) {
            builder.append(sValue).append('\n');
        }
        return builder.toString();
    }


    @Override
    public Class getGeometryBinding() {
        return Point.class;
    }

    @Override
    public Class[] getPossibleBindings() {
        return new Class[]{Point.class, Coordinate.class};
    }

    @Override
    public GenericName getName() {
        return NAME;
    }

    @Override
    protected List<AttributeType> getAttributes() {
        return Collections.singletonList(SYMBOL);
    }

}
