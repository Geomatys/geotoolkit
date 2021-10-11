/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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
package org.geotoolkit.csw.xml;

import org.geotoolkit.ows.xml.AbstractCapabilitiesBase;
import org.geotoolkit.filter.capability.FilterCapabilities;

/**
 * TODO this interface duplicate a lot from AbstractCapabiltiesBase because of CSWResponse.
 * @author Guilhem legal (Geomatys)
 * @module
 */
public interface AbstractCapabilities extends AbstractCapabilitiesBase {

    FilterCapabilities getFilterCapabilities();

}
