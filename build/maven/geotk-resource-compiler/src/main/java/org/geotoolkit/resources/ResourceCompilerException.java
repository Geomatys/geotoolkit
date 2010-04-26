/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.resources;

import org.apache.maven.plugin.MojoExecutionException;


/**
 * Thrown when the {@link IndexedResourceCompiler} exit abnormally.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.00
 */
@SuppressWarnings("serial")
public final class ResourceCompilerException extends MojoExecutionException {
    /**
     * Creates an exception with the given detail message.
     *
     * @param message The detail message.
     */
    ResourceCompilerException(final String message) {
        super(message);
    }

    /**
     * Creates an exception with the given cause.
     *
     * @param cause The cause of this exception.
     */
    ResourceCompilerException(final Throwable cause) {
        super(cause.getLocalizedMessage(), cause);
    }
}
