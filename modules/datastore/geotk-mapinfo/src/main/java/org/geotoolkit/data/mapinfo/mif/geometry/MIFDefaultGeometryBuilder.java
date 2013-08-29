package org.geotoolkit.data.mapinfo.mif.geometry;

import com.vividsolutions.jts.geom.*;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Class to build feature from abstract geometry type.
 *
 * @author Alexis Manin (Geomatys)
 * @author Mehdi Sidhoum (Geomatys)
 */
public class MIFDefaultGeometryBuilder extends MIFGeometryBuilder{

    public static final Name NAME = new DefaultName("GEOMETRY");
    public static final Name GEOM_NAME = new DefaultName("GEOMETRY");

    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {
        throw new IllegalStateException("building geometry with name GEOMETRY should never happen for MIF MID !");
    }

    @Override
    public FeatureType buildType(CoordinateReferenceSystem crs, FeatureType parent) {
        AttributeType type = new DefaultAttributeType(NAME, Feature.class, false, false, null, null, null);
        AttributeDescriptor desc = new DefaultAttributeDescriptor(type, NAME, 1, 3, false, null);

        return new DefaultSimpleFeatureType(NAME, Collections.singletonList(desc), null, false, null, parent, null);
    }

    @Override
    public String toMIFSyntax(Feature geometry) throws DataStoreException {

        if(geometry.getProperty(GEOM_NAME) != null) {
            return jtsToMIFGeometry(geometry);
        } else if(geometry.getDefaultGeometryProperty().getValue() instanceof Geometry) {
            return jtsToMIFGeometry(geometry);
        } else {
            throw new DataStoreException("Incompatible geometry type.");
        }
    }


    private String jtsToMIFGeometry(Feature feature) throws DataStoreException {

        final Object source = feature.getDefaultGeometryProperty().getValue();

        for(MIFUtils.GeometryType gType : MIFUtils.GeometryType.values()) {
            final Class[] bindings = gType.binding.getPossibleBindings();
            for (int i=0 ; i < bindings.length; i++) {
                 if (bindings[i].isAssignableFrom(source.getClass())) {
                     return gType.binding.toMIFSyntax(feature);
                 }
            }
        }
        throw new DataStoreException("Incompatible geometry type.");
    }


    @Override
    public Class getGeometryBinding() {
        return Geometry.class;
    }

    /**
     * Here We set an empty array because this class should be use only as last resort.
     * @return All possible matching types.
     */
    @Override
    public Class[] getPossibleBindings() {
        return new Class[]{};
    }

    @Override
    public Name getName() {
        return NAME;
    }

    @Override
    protected List<AttributeDescriptor> getAttributes() {
        AttributeType type = new DefaultAttributeType(NAME, Feature.class, false, false, null, null, null);
        AttributeDescriptor desc = new DefaultAttributeDescriptor(type, NAME, 1, 3, false, null);
        return Collections.singletonList(desc);
    }
}
