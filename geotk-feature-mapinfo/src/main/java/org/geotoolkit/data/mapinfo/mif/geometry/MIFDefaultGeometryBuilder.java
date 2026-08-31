package org.geotoolkit.data.mapinfo.mif.geometry;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.apache.sis.feature.DefaultAttributeType;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.locationtech.jts.geom.*;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.GenericName;

/**
 * Class to build feature from abstract geometry type.
 *
 * @author Alexis Manin (Geomatys)
 * @author Mehdi Sidhoum (Geomatys)
 */
public class MIFDefaultGeometryBuilder extends MIFGeometryBuilder{

    public static final GenericName NAME = Names.createLocalName(null,null,"GEOMETRY");
    public static final GenericName GEOM_NAME = Names.createLocalName(null,null,"GEOMETRY");

    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {
        throw new IllegalStateException("building geometry with name GEOMETRY should never happen for MIF MID !");
    }

    @Override
    public FeatureType buildType(CoordinateReferenceSystem crs, FeatureType parent) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(NAME);
        ftb.setSuperTypes(parent);
        ftb.addAssociation(MIFCollectionBuilder.EMPTY_TYPE).setName(NAME).setMinimumOccurs(1).setMaximumOccurs(3);
        return ftb.build();
    }

    @Override
    public String toMIFSyntax(Feature feature) throws DataStoreException {

        if(MIFUtils.getPropertySafe(feature, GEOM_NAME.toString()) != null) {
            return jtsToMIFGeometry(feature);
        } else if(MIFUtils.getGeometryValue(feature) instanceof Geometry) {
            return jtsToMIFGeometry(feature);
        } else {
            throw new DataStoreException("Incompatible geometry type.");
        }
    }


    private String jtsToMIFGeometry(Feature feature) throws DataStoreException {

        final Object source = MIFUtils.getGeometryValue(feature);

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
    public GenericName getName() {
        return NAME;
    }

    @Override
    public List<AttributeType> getAttributes() {
        final AttributeType attType = new DefaultAttributeType(Collections.singletonMap("name", NAME), Feature.class, 1, 3, null);
        return Collections.singletonList(attType);
    }
}
