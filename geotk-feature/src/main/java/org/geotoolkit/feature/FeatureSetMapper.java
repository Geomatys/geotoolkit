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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.apache.sis.feature.AbstractOperation;
import org.apache.sis.feature.DefaultFeatureType;
import org.apache.sis.feature.FeatureOperations;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class FeatureSetMapper implements Function<Feature,Feature> {

    private static Map<String,Object> properties(IdentifiedType type) {
        ArgumentChecks.ensureNonNull("type", type);
        final Map<String,Object> p = new HashMap<>(8);
        p.put(DefaultFeatureType.NAME_KEY,        type.getName());
        p.put(DefaultFeatureType.DEFINITION_KEY,  type.getDefinition());
        type.getDescription().ifPresent((t) -> p.put(DefaultFeatureType.DESCRIPTION_KEY, t));
        type.getDesignation().ifPresent((t) -> p.put(DefaultFeatureType.DESIGNATION_KEY, t));
        return p;
    }

    public abstract FeatureType getMappedType();


    /**
     * Update link and envelope operations, those have property references which may
     * come from the original feature type.
     *
     * @param ftb FeatureTypeBuilder to fix.
     */
    protected static void fixOperations(FeatureTypeBuilder ftb) {

        final List<PropertyType> newProperties = new ArrayList<>();
        final Iterator<PropertyTypeBuilder> iterator = ftb.properties().iterator();
        while (iterator.hasNext()) {
            final PropertyTypeBuilder ptb = iterator.next();
            final PropertyType property = ptb.build();
            if (property instanceof Operation) {
                Operation op = (Operation) property;
                String code = op.getParameters().getName().getCode();

                if ("Envelope".equals(code)) {
                    final Set<String> dependencies = ((AbstractOperation)op).getDependencies();

                    final PropertyType[] targets = new PropertyType[dependencies.size()];
                    int i=0;
                    for (String dep : dependencies) {
                        final PropertyType target = ftb.getProperty(dep).build();
                        targets[i] = target;
                        i++;
                    }
                    //update old reference
                    //Note : we don't know if the envelope reference were valid or not
                    //we replace it to be sure.
                    final Map<String,Object> identification = properties(op);
                    try {
                        op = FeatureOperations.envelope(identification, null, targets);
                    } catch (FactoryException ex) {
                        throw new IllegalArgumentException(ex.getMessage(),ex);
                    }
                } else if ("Link".equals(code)) {
                    final Set<String> dependencies = ((AbstractOperation)op).getDependencies();
                    final PropertyType target = ftb.getProperty(dependencies.iterator().next()).build();
                    if (op.getResult() != target) {
                        //update old reference
                        final Map<String,Object> identification = properties(op);
                        op = FeatureOperations.link(identification, target);
                    }
                }
                newProperties.add(op);
            } else {
                newProperties.add(ptb.build());
            }
        }

        //replace properties to preserve order otherwise re-created operations would be at the end.
        ftb.properties().clear();
        newProperties.stream().forEach(ftb::addProperty);

    }

}
