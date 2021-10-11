/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2018, Geomatys
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
 * A schema for storing coverages metadata in a SQL database. The coverage sample values are
 * stored as ordinary files encoded in arbitrary image formats (PNG, RAW, ASCII, JPEG-2000,
 * <i>etc.</i> - note that the classic JPEG format is not recommended).
 *
 * A <a href="http://www.postgresql.org/">PostgreSQL</a> database is used for storing coverage
 * <em>metadata</em> like geographic envelopes and meaning of pixel values. The database is also
 * used as an index for searching image files from a 2D, 3D or 4D spatio-temporal envelopes.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Rémi Eve (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @author Antoine Hnawia (IRD)
 * @author Sam Hiatt
  */
package org.geotoolkit.coverage.sql;
