package org.geotoolkit.data.mif.geometry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.data.mif.style.Symbol;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.geotoolkit.feature.type.DefaultGeometryDescriptor;
import org.geotoolkit.feature.type.DefaultGeometryType;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * Util class to build a feature from Point object of a MIF file.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 25/02/13
 */
public final class MIFPointBuilder extends MIFGeometryBuilder {

    public static final Name NAME = new DefaultName("POINT");

    public static final Name SYMBOL_NAME = new DefaultName("SYMBOL");
    public static final AttributeDescriptor SYMBOL_DESCRIPTOR;

    private SimpleFeatureType featureType = null;

    static {
        final DefaultAttributeType symbolType = new DefaultAttributeType(SYMBOL_NAME, Symbol.class, true, false, null, null, null);
        SYMBOL_DESCRIPTOR = new DefaultAttributeDescriptor(symbolType, SYMBOL_NAME, 0, 1, true, null);
    }

    /**
     * Build a feature describing a MIF point geometry. That assume that user gave a {@link Scanner} which is placed on
     * a POINT tag.
     *
     *
     * @param scanner The scanner to use for data reading (must be pointing on a mif POINT element).
     * @param toFill
     * @return a {@link SimpleFeature} matching the {@link SimpleFeatureType} given by
     * {@link MIFPointBuilder#buildType(org.opengis.referencing.crs.CoordinateReferenceSystem, org.opengis.feature.type.FeatureType)}.
     * @throws DataStoreException If there's a problem while parsing stream of the given Scanner.
     */
    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {
        Double x = null;
        Double y = null;
        if (scanner.hasNextDouble()) {
            x = scanner.nextDouble();
            if(scanner.hasNextDouble()) {
                y = scanner.nextDouble();
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

        if(scanner.hasNext("\\w+") && "symbol".equalsIgnoreCase(scanner.next("\\w+"))) {
            Symbol symbol;
            String args = scanner.next("\\([^\\)]\\)");
            String[] argsTab = args.substring(1, args.length()-1).trim().split(",");
            if (argsTab.length < 3) {
                LOGGER.log(Level.WARNING, "A SYMBOL tag have been found, but can't be read (bad syntax ?). Ignore style.");
            }
            else {
                /** todo : parse symbol */
            }
            symbol = new Symbol();
        }
    }

    /**
     * Build a feature type which represents a MIF point geometry
     *
     * @param crs The CRS to put in feature type. If null, no CRS will be pass to the feature type.
     * @return A {@link SimpleFeatureType} which describe a point (as MIF defines it).
     */
    @Override
    public SimpleFeatureType buildType(CoordinateReferenceSystem crs, FeatureType parent) {
        GeometryType geomType = new DefaultGeometryType(NAME, Point.class, crs, true, false, null, null, null);
        final GeometryDescriptor geomDesc = new DefaultGeometryDescriptor(geomType, NAME, 1, 1, true, null);

        featureType = new DefaultSimpleFeatureType(NAME, Collections.singletonList(SYMBOL_DESCRIPTOR), geomDesc, false, null, parent, null);
        return featureType;
    }

    @Override
    public Class getGeometryBinding() {
        return Point.class;
    }

    @Override
    public Name getName() {
        return NAME;
    }

}
