package org.geotoolkit.data.mapinfo.mif.geometry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.data.mapinfo.mif.style.Pen;
import org.geotoolkit.data.mapinfo.mif.style.Symbol;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.referencing.operation.MathTransform;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Util class to build a feature from Point object of a MIF file.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 25/02/13
 */
public final class MIFPointBuilder extends MIFGeometryBuilder {

    public static final Name NAME = new DefaultName("POINT");

    public static final AttributeDescriptor SYMBOL_DESCRIPTOR;

    static {
        final DefaultAttributeType symbolType = new DefaultAttributeType(Symbol.NAME, Symbol.class, true, false, null, null, null);
        SYMBOL_DESCRIPTOR = new DefaultAttributeDescriptor(symbolType, Symbol.NAME, 1, 1, true, null);
    }

    /**
     * Build a feature describing a MIF point geometry. That assume that user gave a {@link Scanner} which is placed on
     * a POINT tag.
     *
     *
     * @param scanner The scanner to use for data reading (must be pointing on a mif POINT element).
     * @param toFill
     * @return a {@link SimpleFeature} matching the {@link SimpleFeatureType} given by
     * {@link MIFGeometryBuilder#buildType(org.opengis.referencing.crs.CoordinateReferenceSystem, org.opengis.feature.type.FeatureType)}.
     * @throws DataStoreException If there's a problem while parsing stream of the given Scanner.
     */
    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {
        Double x = null;
        Double y = null;
        if (scanner.hasNext(ProjectionUtils.DOUBLE_PATTERN)) {
            x = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
            if(scanner.hasNext(ProjectionUtils.DOUBLE_PATTERN)) {
                y = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
            }
        }
        if(x == null || y == null) {
            throw new DataStoreException("Unable to build point from given data");
        }

        final Coordinate result;
        if(toApply != null) {
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

        toFill.getProperty(NAME).setValue(pt);

        // Style
        if(scanner.hasNext(Symbol.SYMBOL_PATTERN) && toFill.getType().getDescriptors().contains(SYMBOL_DESCRIPTOR)) {
            String args = scanner.next(Symbol.SYMBOL_PATTERN);
            String[] argsTab = args.substring(args.indexOf('(')+1, args.length()-1).trim().split(",");
            if (argsTab.length < 3) {
                LOGGER.log(Level.WARNING, "A PEN tag have been found, but can't be read (bad syntax ?). Ignore style.");
            }
            else {
                final int width = Integer.decode(argsTab[0]);
                final int pattern = Integer.decode(argsTab[1]);
                final int color = Integer.decode(argsTab[2]);
                Symbol symbol = new Symbol(width, pattern, color, null);
                toFill.getProperty(Symbol.NAME).setValue(symbol);
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
        final Point pt;
        final Object value = (Point) geometry.getDefaultGeometryProperty().getValue();
        if(value instanceof Point) {
            pt = (Point)value;
        } else {
            pt = GEOMETRY_FACTORY.createPoint((Coordinate) value);
        }
        builder.append(' ').append(pt.getX()).append(' ').append(pt.getY()).append('\n');

        if(geometry.getProperty(Symbol.NAME) != null) {
            Object sValue = geometry.getProperty(Symbol.NAME).getValue();
            if(sValue != null && sValue instanceof Symbol) {
                builder.append(sValue).append('\n');
            }
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
    public Name getName() {
        return NAME;
    }

    @Override
    protected List<AttributeDescriptor> getAttributes() {
        return Collections.singletonList(SYMBOL_DESCRIPTOR);
    }

}
