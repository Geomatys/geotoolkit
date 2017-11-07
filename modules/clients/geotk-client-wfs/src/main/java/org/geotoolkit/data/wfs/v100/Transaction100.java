/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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

package org.geotoolkit.data.wfs.v100;

import org.geotoolkit.data.wfs.AbstractTransactionRequest;
import org.geotoolkit.security.ClientSecurity;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class Transaction100 extends AbstractTransactionRequest{

    public Transaction100(final String serverURL, final ClientSecurity security){
        super(serverURL,"1.0.0",security);
    }

}
