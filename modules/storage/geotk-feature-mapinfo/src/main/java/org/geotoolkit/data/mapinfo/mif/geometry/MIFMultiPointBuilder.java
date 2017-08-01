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
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
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
 * Create collection of points from MIF MultiPoint
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 27/02/13
 */
public class MIFMultiPointBuilder extends MIFGeometryBuilder {

    public static final GenericName NAME = NamesExt.create("MULTIPOINT");

    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {
        try {
            final int numCoords = scanner.nextInt()*2;
            double[] coords = new double[numCoords];
            for(int ptCount = 0 ; ptCount < numCoords ; ptCount++) {
                coords[ptCount] = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
            }

            final CoordinateSequence seq;
            if(toApply != null) {
                try {
                    double[] afterT = new double[numCoords];
                    toApply.transform(coords, 0, afterT, 0, numCoords/2);
                    seq = new PackedCoordinateSequence.Double(afterT, 2);
                } catch (Exception e) {
                    throw new DataStoreException("Unable to transform geometry", e);
                }
            } else {
                seq = new PackedCoordinateSequence.Double(coords, 2);
            }

            toFill.setPropertyValue(FeatureExt.getDefaultGeometry(toFill.getType()).getName().tip().toString(), GEOMETRY_FACTORY.createMultiPoint(seq));

            if(scanner.hasNext(Symbol.SYMBOL_PATTERN) && toFill.getType().getProperties(true).contains(SYMBOL)) {
                String args = scanner.next()+scanner.nextLine();
                String[] argsTab = args.substring(args.indexOf('(')+1, args.length()-1)
                        .replaceAll("[^\\d^,]+", "")
                        .split(",");
                if (argsTab.length < 3) {
                    LOGGER.log(Level.WARNING, "A SYMBOL tag have been found, but can't be read (bad syntax ?). Ignore style.");
                }
                else {
                    final int width = Integer.decode(argsTab[0]);
                    final int pattern = Integer.decode(argsTab[1]);
                    final int color = Integer.decode(argsTab[2]);
                    Symbol symbol = new Symbol(width, pattern, color, null);
                    toFill.setPropertyValue(Symbol.NAME.toString(),symbol);
                }
            }

        } catch (Exception e) {
            throw new DataStoreException("MultiPoint instance can't be read.", e);
        }
    }

    @Override
    public String toMIFSyntax(Feature feature) throws DataStoreException {
        super.toMIFSyntax(feature);

        final StringBuilder builder = new StringBuilder(NAME.tip().toString());
        final Object value = MIFUtils.getGeometryValue(feature);
        final MultiPoint multiPt;
        if(value instanceof CoordinateSequence) {
            multiPt = GEOMETRY_FACTORY.createMultiPoint(((CoordinateSequence)value).toCoordinateArray());
        } else {
            multiPt = (MultiPoint) value;
        }
        builder.append(' ').append(multiPt.getNumGeometries()).append('\n');

        for(int i =0 ; i < multiPt.getNumGeometries(); i++) {
            Point pt = (Point) multiPt.getGeometryN(i);
            builder.append(pt.getX()).append(' ').append(pt.getY()).append('\n');
        }

        final Object sValue = MIFUtils.getPropertySafe(feature, Symbol.NAME.toString());
        if(sValue instanceof Symbol) {
            builder.append(sValue).append('\n');
        }

        return builder.toString();
    }

    @Override
    public Class getGeometryBinding() {
        return MultiPoint.class;
    }

    @Override
    public Class[] getPossibleBindings() {
        return new Class[]{MultiPoint.class, CoordinateSequence.class};
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
