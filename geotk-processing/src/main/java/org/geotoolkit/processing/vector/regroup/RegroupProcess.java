/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.processing.vector.regroup;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.collection.BackingStoreException;
import org.locationtech.jts.geom.Geometry;
import java.util.*;
import java.util.stream.Stream;
import org.apache.sis.feature.AbstractOperation;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.processing.vector.regroup.RegroupDescriptor.*;


/**
 * Regroup features and there geometries on a specify attribute name. Each different values of this
 * attribute generate a Feature.
 * @author Quentin Boileau
 * @module
 */
public class RegroupProcess extends AbstractProcess {
    /**
     * Default constructor
     */
    public RegroupProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection inputFeatureList  = inputParameters.getValue(FEATURE_IN);
        final String inputAttributeName           = inputParameters.getValue(REGROUP_ATTRIBUTE);
        final String inputGeometryName            = inputParameters.getValue(GEOMETRY_NAME);
        final FeatureCollection resultFeatureList = new RegroupFeatureCollection(inputFeatureList, inputAttributeName, inputGeometryName);
        outputParameters.getOrCreate(FEATURE_OUT).setValue(resultFeatureList);
    }

    /**
     * Create a new FeatureType with only the attribute and the geometry specified in process input.
     *
     * @param geometryName if null we use the default Feature geometry
     */
    static FeatureType regroupFeatureType(final FeatureType oldFeatureType, String geometryName, final String regroupAttribute) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder(oldFeatureType);

        //if keepedGeometry is null we use the default Geometry
        if (geometryName == null) {
            geometryName = AttributeConvention.GEOMETRY;
        }

        PropertyType property = oldFeatureType.getProperty(geometryName);
        if(property instanceof AbstractOperation){
            final Set<String> deps = ((AbstractOperation)property).getDependencies();
            if(deps.size()==1){
                geometryName = deps.iterator().next();
            }
        }

        ftb.properties().clear();
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);

        for(PropertyType pt : oldFeatureType.getProperties(true)){
            if(AttributeConvention.isGeometryAttribute(pt)){
                if(pt.getName().toString().equals(geometryName)){
                    ftb.addAttribute((AttributeType) pt).addRole(AttributeRole.DEFAULT_GEOMETRY);
                }
            }else if(pt.getName().toString().equals(regroupAttribute)){
                ftb.addProperty(pt);
            }
        }
        return ftb.build();
    }

    /**
     * Create a Feature with one of attribute values and an union of all features geometry with the
     * same attribute value.
     *
     * @param regroupAttribute attribute specified in process input
     * @param attributeValue one value of the specified attribute
     * @param newFeatureType the new FeatureTYpe
     * @param geometryName if null we use the default Feature geometry
     * @param filtredList the input FeatureCollection filtered on attribute value
     */
    static Feature regroupFeature(final String regroupAttribute, final Object attributeValue,
            final FeatureType newFeatureType, String geometryName, final FeatureSet filtredList)
    {
        final List<Geometry> geoms = new ArrayList<>();
        try (final Stream<Feature> stream = filtredList.features(false)) {
            final Iterator<Feature> featureIter = stream.iterator();
            while (featureIter.hasNext()) {
                final Feature feature = featureIter.next();
                if (geometryName == null) {
                    geometryName = AttributeConvention.GEOMETRY;
                }
                for (final PropertyType property : feature.getType().getProperties(true)) {
                    //if property is a geometry
                    if (AttributeConvention.isGeometryAttribute(property)) {
                        //if it's the property we needed
                        final String name = property.getName().toString();
                        if (name.equals(geometryName)) {
                            Geometry candidate = (Geometry) feature.getPropertyValue(name);
                            geoms.add(candidate);
                        }
                    }
                }
            }
        } catch (DataStoreException ex) {
            throw new BackingStoreException(ex);
        }
        Geometry regroupGeometry = org.geotoolkit.geometry.jts.JTS.getFactory().buildGeometry(geoms);
        Feature resultFeature;
        //In case
        if (regroupAttribute == null && attributeValue == null) {
            resultFeature = newFeatureType.newInstance();
            resultFeature.setPropertyValue(AttributeConvention.IDENTIFIER, "groupedGeometryFeature");
            resultFeature.setPropertyValue(geometryName, regroupGeometry);
        } else {
            //result feature
            resultFeature = newFeatureType.newInstance();
            resultFeature.setPropertyValue(AttributeConvention.IDENTIFIER, regroupAttribute + "-" + attributeValue);
            resultFeature.setPropertyValue(regroupAttribute, attributeValue);
            resultFeature.setPropertyValue(geometryName, regroupGeometry);
        }
        return resultFeature;
    }

    /**
     * Browse in input FeatureCollection all different values of the specified attribute
     * If regroupAttribute is null, we return an empty Collection.
     */
    static Collection<Object> getAttributeValues(final String regroupAttribute, final FeatureSet featureList) {
        final Collection<Object> values = new ArrayList<>();
        if (regroupAttribute != null) {
            try (final Stream<Feature> stream = featureList.features(false)) {
                final Iterator<Feature> featureIter = stream.iterator();
                while (featureIter.hasNext()) {
                    final Feature feature = featureIter.next();
                    final Object value = feature.getPropertyValue(regroupAttribute);
                    if (!values.contains(value)) {
                        values.add(value);
                    }
                }
            } catch (DataStoreException ex) {
                throw new BackingStoreException(ex);
            }
        }
        return values;
    }
}
