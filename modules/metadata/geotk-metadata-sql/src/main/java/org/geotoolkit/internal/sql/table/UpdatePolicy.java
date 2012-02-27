/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.internal.sql.table;


/**
 * The policy to apply during a table update if a record already exists for the same
 * primary key.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public enum UpdatePolicy {
    /**
     * The new record is discarded and the existing one is keep unchanged.
     */
    KEEP_EXISTING,

    /**
     * The old record is deleted and the new record is inserted as a replacement.
     */
    REPLACE_EXISTING,

    /**
     * Remove all previous records before to insert new ones.
     */
    CLEAR_ALL_BEFORE_UPDATE
}
