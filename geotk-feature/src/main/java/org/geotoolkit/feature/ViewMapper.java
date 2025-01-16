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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.sis.feature.AbstractOperation;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.util.Deprecable;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;

/**
 * Mapper which hide unwanted properties.
 * This mapper try to preserve operations but if some used properties are missing
 * from the selection then they are converted to attributes.
 *
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ViewMapper extends FeatureSetMapper {

    private final Set<String> fullNames = new HashSet<>();
    private final FeatureType mapped;

    /**
     * Filter feature type properties.
     *
     * @param base reference feature type
     * @param propertyNames properties to include in the feature type view
     */
    public ViewMapper(FeatureType base, String ... propertyNames) {
        // Use of linked hash set is important for order preservation.
        this(base, new LinkedHashSet<>(Arrays.asList(propertyNames)));
    }
    /**
     * Filter feature type properties.
     *
     * @param base reference feature type
     * @param propertyNames properties to include in the feature type view
     */
    public ViewMapper(FeatureType base, Set<String> propertyNames) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(base.getName());
        ftb.setDefinition(base.getDefinition());
        base.getDescription().ifPresent(ftb::setDescription);
        base.getDesignation().ifPresent(ftb::setDesignation);
        ftb.setDeprecated(base instanceof Deprecable && ((Deprecable)base).isDeprecated());

        for (String pname : propertyNames) {
            PropertyType property;
            try {
                property = base.getProperty(pname);
            } catch (PropertyNotFoundException ex) {
                continue;
            }

            //try to preserve basic operations
            preserve:
            if (property instanceof Operation) {
                final Operation op = (Operation) property;

                String code = op.getParameters().getName().getCode();
                if ("Envelope".equals(code) || "Link".equals(code)) {
                    final Set<String> dependencies = ((AbstractOperation)op).getDependencies();
                    //check all dependencies are in the list
                    //name might not be declared the exact same way, compare properties.
                    for (String dep : dependencies) {
                        boolean used = false;
                        final PropertyType usedProperty = base.getProperty(dep);
                        for (String sname : propertyNames) {
                            final PropertyType sproperty;
                            try {
                                sproperty = base.getProperty(sname);
                            } catch (PropertyNotFoundException ex) {
                                continue;
                            }
                            if (sproperty.equals(usedProperty)) {
                                used = true;
                                break;
                            }
                        }
                        if (!used) {
                            break preserve;
                        }
                    }
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
                ftb.addProperty(property).setName(baseProperty.getName());
            } else {
                ftb.addProperty(property);
            }
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
            feature.setPropertyValue(name, t.getPropertyValue(name));
        }
        return feature;
    }

}
