/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.xal.model;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultXal implements Xal{

    private final List<AddressDetails> addressDetails;
    private final String version;

    public DefaultXal(List<AddressDetails> addressDetails, String version){
        this.addressDetails = (addressDetails == null) ? EMPTY_LIST : addressDetails;
        this.version = version;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public List<AddressDetails> getAddressDetails() {return this.addressDetails;}

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public String getVersion() {return this.version;}

}
