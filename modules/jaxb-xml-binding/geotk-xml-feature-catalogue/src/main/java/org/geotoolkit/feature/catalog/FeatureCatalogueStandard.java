/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2017, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.feature.catalog;

import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.opengis.feature.catalog.FeatureCatalogue;
import org.opengis.metadata.citation.Citation;

/**
 *
 * @author Guilhem  Legal (Geomatys)
 */
public class FeatureCatalogueStandard extends MetadataStandard {
    
    private static final String SUFFIX = "Impl";
    
    private static final String IMPL_SUBPACKAGE = "org.geotoolkit.feature.catalog";
    
    private static final String IMPL_UTIL_SUBPACKAGE = "org.geotoolkit.feature.catalog.util";
    
    /**
     * The singleton instance.
     */
    public static final MetadataStandard ISO_19110;
    static {
        final Package pck = FeatureCatalogue.class.getPackage();
        ISO_19110 = new FeatureCatalogueStandard(new DefaultCitation("Feature Catalogue"), pck);
    }
    
    public FeatureCatalogueStandard(Citation citation, Package interfacePackage, MetadataStandard... dependencies) {
        super(citation, interfacePackage, dependencies);
    }
    
    /**
     * Returns the implementation class for the given interface, or {@code null} if none.
     *
     * @param  <T>  The compile-time {@code type}.
     * @param  type The interface from the {@code org.geotoolkit.sml.xml} package.
     * @return The implementation class, or {@code null} if none.
     */
    @Override
    public <T> Class<? extends T> getImplementation(final Class<T> type) {
        if (!type.isInterface()) {
            return null;
        }
        Class<?> impl;
        final String interfaceName = type.getName();
        final int s = interfaceName.lastIndexOf('.') + 1;
        final StringBuilder implName = new StringBuilder(interfaceName);
        implName.delete(0, s);
        implName.insert(0, IMPL_SUBPACKAGE + '.');
        implName.insert(implName.length(), SUFFIX);
        try {
            impl = Class.forName(implName.toString());
        } catch (ClassNotFoundException e) {
            implName.replace(0, IMPL_SUBPACKAGE.length(), IMPL_UTIL_SUBPACKAGE);
            try {
                impl = Class.forName(implName.toString());
            } catch (ClassNotFoundException e2) {
                return null;
            }
        }
        return impl.asSubclass(type);
    }
}
