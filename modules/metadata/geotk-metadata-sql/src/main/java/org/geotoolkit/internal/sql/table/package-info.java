/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2011, Geomatys
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

/**
 * Helper framework for creating {@link org.geotoolkit.internal.sql.table.Entry} objects from
 * records in a {@link org.geotoolkit.internal.sql.table.Table}. Each table fetch the records
 * using a {@link org.geotoolkit.internal.sql.table.Query}.
 * <p>
 * This package is internal (not public) because the API is very specific to the needs of the
 * {@code geotk-coverage-sql} module, may change in any future version, and could be considered
 * as a duplicated (at least partially) of persistence frameworks like Hibernate. It was created
 * before Hibernate became in wide use, and is still used because it provides us maximal control
 * over the way we are querying the SQL database.
 * <p>
 * This package logically belong to the {@code geotk-coverage-sql} module.  But it is defined in
 * the {@code geotk-metadata-sql} module in order to allow other modules to use it. The metadata
 * SQL module does not use it itself right now, but it could use it in some future version.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
package org.geotoolkit.internal.sql.table;
