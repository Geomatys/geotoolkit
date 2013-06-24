/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.naming;

import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.Immutable;

import org.opengis.util.TypeName;
import org.opengis.util.NameSpace;
import org.opengis.util.MemberName;


/**
 * The name of an {@linkplain MemberName attribute} type.
 * <p>
 * {@code DefaultTypeName} can be instantiated by any of the following methods:
 * <ul>
 *   <li>{@link DefaultNameFactory#createTypeName(NameSpace, CharSequence)}</li>
 * </ul>
 *
 * @author Guilhem Legal (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.util.iso} package.
 */
@Deprecated
@Immutable
@XmlRootElement(name = "TypeName")
public class DefaultTypeName extends DefaultLocalName implements TypeName {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -7985388992575173993L;

    /**
     * Empty constructor to be used by JAXB only. Despite its "final" declaration,
     * the {@link #name} field will be set by JAXB during unmarshalling.
     */
    private DefaultTypeName() {
    }

    /**
     * Constructs a type name from the given character sequence. The argument are given unchanged
     * to the {@linkplain DefaultLocalName#DefaultLocalName(NameSpace,CharSequence) super-class
     * constructor}.
     *
     * @param scope The scope of this name, or {@code null} for a global scope.
     * @param name The local name (never {@code null}).
     */
    protected DefaultTypeName(final NameSpace scope, final CharSequence name) {
        super(scope, name);
    }
}
