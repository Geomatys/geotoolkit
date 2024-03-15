/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2024, Geomatys
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
package org.geotoolkit.display2d.painter;

import org.opengis.style.Symbolizer;

/**
 * Resource symbolizers act on a resource as a whole, not on individual features.
 * Such symbolizers are not defined by the Symbology Encoding specification but are
 * often required to produce uncommon presentations.
 *
 * <p>
 * NOTE: this class is a first draft subject to modifications.
 * </p>
 *
 * @author  Johann Sorel (Geomatys)
 * @version 1.2
 * @since   1.2
 */
public interface ResourceSymbolizer extends Symbolizer {

}
