/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.referencing;

import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Specifies the degree of strictness when comparing two {@linkplain CoordinateReferenceSystem
 * Coordinate Reference System} objects for equality.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.14
 * @module
 */
public enum ComparisonMode {
    /**
     * All attributes of the compared objects shall be strictly equals.
     * This is the default behavior of {@link AbstractIdentifiedObject#equals(Object)}.
     */
    STRICT,

    /**
     * Only the attributes relevant to {@link MathTransform} shall be compared.
     * Metadata like the identifier or the domain of validity shall be ignored.
     *
     * @see CRS#equalsIgnoreMetadata(Object, Object)
     */
    IGNORE_METADATA
}
