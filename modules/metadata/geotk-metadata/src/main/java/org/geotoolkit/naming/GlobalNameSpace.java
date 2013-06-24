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

import java.io.ObjectStreamException;
import net.jcip.annotations.Immutable;


/**
 * The global namespace. Only one instance of this class is allowed to exists. We do not expose
 * any global namespace in public API since ISO 19103 does not define them and users should not
 * need to handle them explicitely.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.util.iso} package.
 */
@Deprecated
@Immutable
final class GlobalNameSpace extends DefaultNameSpace {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 5276591595735817024L;

    /**
     * The unique global namespace.
     */
    public static final GlobalNameSpace GLOBAL = new GlobalNameSpace();

    /**
     * Creates the global namespace.
     */
    private GlobalNameSpace() {
    }

    /**
     * Indicates that this namespace is a "top level" namespace.
     */
    @Override
    public boolean isGlobal() {
        return true;
    }

    /**
     * Returns the unique instance of global name space on deserialization.
     *
     * @return The unique instance.
     * @throws ObjectStreamException Should never happen.
     */
    @Override
    Object readResolve() throws ObjectStreamException {
        return GLOBAL;
    }
}
