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
package org.geotoolkit.data.mariner;

import org.geotoolkit.data.s57.annexe.S57TypeBank;

/**
 * S-52 Mariner object types bank.
 *
 * @author Johann Sorel (Geomatys)
 */
public class MarinerTypeBank extends S57TypeBank{

    public MarinerTypeBank() {
        super(MarinerTypeBank.class.getResource("/org/geotoolkit/mariner/MarinerFeatureType.properties"),
              MarinerTypeBank.class.getResource("/org/geotoolkit/mariner/MarinerPropertyType.properties"));
    }


}
