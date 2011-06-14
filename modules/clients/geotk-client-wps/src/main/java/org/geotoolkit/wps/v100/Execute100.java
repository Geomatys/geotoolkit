/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wps.v100;

import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wps.AbstractExecute;

/**
 *
 * @author Quentin Boileau
 * @module pending
 */
public class Execute100 extends AbstractExecute {
    
    public Execute100(final String serverURL, final ClientSecurity security){
        super(serverURL, "1.0.0",security);
    }
      
}
