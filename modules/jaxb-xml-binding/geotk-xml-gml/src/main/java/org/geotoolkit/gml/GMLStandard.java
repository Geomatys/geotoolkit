/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.gml;

import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.metadata.iso.citation.DefaultCitation;


/**
 * A metadata standard for the {@link org.geotoolkit.gml} package.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public class GMLStandard extends MetadataStandard {
    /**
     * The prefix to omit from interface name.
     */
    private static final String PREFIX = "Abstract";

    /**
     * The package name for the sensor ML version to implement.
     */
    private final String implSubPackage;

    /**
     * Constructs a {@code MetadataStandard} for a specific GML version.
     */
    public GMLStandard(final String name, final Package pck, final String implSubPackage, final MetadataStandard... dependencies) {
        super(new DefaultCitation(name), pck, dependencies);
        this.implSubPackage = implSubPackage;
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
        if (interfaceName.regionMatches(s, PREFIX, 0, PREFIX.length())) {
            implName.delete(s, s + PREFIX.length());
        }
        implName.insert(s, implSubPackage + '.');
        try {
            impl = Class.forName(implName.toString());
        } catch (ClassNotFoundException e) {
            final int end = implName.length();
            implName.append("PropertyType");
            try {
                impl = Class.forName(implName.toString());
            } catch (ClassNotFoundException e2) {
                implName.setLength(end);
                implName.append("Type");
                try {
                    impl = Class.forName(implName.toString());
                } catch (ClassNotFoundException e3) {
                    return null;
                }
            }
        }
        return impl.asSubclass(type);
    }
}
