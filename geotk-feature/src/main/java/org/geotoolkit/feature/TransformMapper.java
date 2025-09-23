/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.feature;

import org.locationtech.jts.geom.Geometry;
import java.util.HashSet;
import java.util.Set;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.util.Deprecable;
import org.geotoolkit.geometry.jts.transform.GeometryTransformer;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;
import org.opengis.referencing.operation.TransformException;

/**
 * Mapper which change all geometry attributes CoordinateReferenceSystem.
 * All geometric properties are transformed not just the default geometry.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TransformMapper extends FeatureSetMapper {

    private final GeometryTransformer transformer;
    private final Set<String> fullNames = new HashSet<>();
    private final Set<String> geomNames = new HashSet<>();
    private final FeatureType mapped;

    /**
     * Filter feature type properties.
     *
     * @param base reference feature type
     * @param transformer Geometry transformation operation.
     */
    public TransformMapper(FeatureType base, final GeometryTransformer transformer) {
        this.transformer = transformer;

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(base.getName());
        ftb.setDefinition(base.getDefinition());
        base.getDescription().ifPresent(ftb::setDescription);
        base.getDesignation().ifPresent(ftb::setDesignation);
        ftb.setDeprecated(base instanceof Deprecable && ((Deprecable)base).isDeprecated());

        for (PropertyType property : base.getProperties(true)) {

            //try to preserve basic operations
            if (property instanceof Operation) {
                String code = ((Operation) property).getParameters().getName().getCode();
                if ("Envelope".equals(code) || "Link".equals(code)) {
                    ftb.addProperty(property);
                    continue;
                }
            }

            fullNames.add(property.getName().toString());

            //unroll operation
            if (property instanceof Operation) {
                final PropertyType baseProperty = property;
                while (property instanceof Operation) {
                    property = (PropertyType) ((Operation)property).getResult();
                }
                //we must preserve the original operation name.
                final PropertyTypeBuilder typeBuilder = ftb.addProperty(property);
                property = (PropertyType) typeBuilder.setName(baseProperty.getName()).build();
                ftb.properties().remove(typeBuilder);
            }

            if (AttributeConvention.isGeometryAttribute(property)) {
                geomNames.add(property.getName().toString());
            }

            ftb.addProperty(property);
        }

        fixOperations(ftb);
        mapped = ftb.build();
    }

    @Override
    public FeatureType getMappedType() {
        return mapped;
    }

    @Override
    public Feature apply(Feature t) {
        final Feature feature = mapped.newInstance();
        for (String name : fullNames) {
            Object value = t.getPropertyValue(name);
            if (geomNames.contains(name) && value != null) {
                try {
                    value = transformer.transform((Geometry)value);
                } catch (TransformException ex) {
                    //TODO replace by a runtime exception from SIS, which one ?
                    throw new RuntimeException(ex.getMessage(),ex);
                }
            }
            feature.setPropertyValue(name, value);
        }
        return feature;
    }

}
