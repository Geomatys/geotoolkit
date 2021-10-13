/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.mapinfo.mif.style;

import org.opengis.style.Symbolizer;

import java.io.Serializable;

/**
 * Base class to represent MIF styles.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 01/03/13
 */
public interface MIFSymbolizer extends Serializable, Symbolizer {

    public String toMIFText();
}
