package org.geotoolkit.data.mif.geometry;

import org.geotoolkit.data.mif.MIFUtils;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.util.Collections;
import java.util.Scanner;

/**
 * Build features representing MIF Collection geometries.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 27/02/13
 */
public class MIFCollectionBuilder extends MIFGeometryBuilder {
    public static final Name NAME = new DefaultName("COLLECTION");
    public static final Name GEOM_NAME = new DefaultName("GEOMETRY");

    private SimpleFeatureType featureType;
    private CoordinateReferenceSystem collectionCRS = null;
    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {

        int numGeom = 0;
        try {
            numGeom = scanner.nextInt();
        } catch (Exception e) {
            throw new DataStoreException("Number of geometries in Collection is not specified", e);
        }

        for(int geomCount=0 ; geomCount < numGeom ; geomCount++ ) {
            while(scanner.hasNextLine()) {
                final String tmpWord = scanner.findInLine("\\w+");
                final MIFUtils.GeometryType enumType = MIFUtils.getGeometryType(tmpWord);
                if (enumType != null) {
                    final FeatureType type = enumType.getBinding(collectionCRS, null);
                    final Feature currentFeature = FeatureUtilities.defaultFeature(type, tmpWord+geomCount);
                    enumType.readGeometry(scanner, toFill, toApply);
                    toFill.getProperties(GEOM_NAME).add(currentFeature);
                    break;
                }
            }
        }
    }

    @Override
    public FeatureType buildType(CoordinateReferenceSystem crs, FeatureType parent) {
        AttributeType type = new DefaultAttributeType(NAME, Feature.class, false, false, null, null, null);
        AttributeDescriptor desc = new DefaultAttributeDescriptor(type, NAME, 1, Integer.MAX_VALUE, false, null);

        featureType = new DefaultSimpleFeatureType(NAME, Collections.singletonList(desc), null, false, null, parent, null);
        collectionCRS = crs;

        return featureType;
    }

    @Override
    public Class getGeometryBinding() {
        return Feature.class;
    }

    @Override
    public Name getName() {
        return NAME;
    }
}
