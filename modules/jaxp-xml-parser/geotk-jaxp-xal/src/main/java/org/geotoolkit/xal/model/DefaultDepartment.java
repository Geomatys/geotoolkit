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
 */
public class DefaultDepartment implements Department {

    private final List<GenericTypedGrPostal> addressLines;
    private final List<GenericTypedGrPostal> departmentNames;
    private final MailStop mailStop;
    private final PostalCode postalCode;
    private final String type;

    /**
     *
     * @param addressLines
     * @param departmentNames
     * @param mailStop
     * @param postalCode
     * @param type
     */
    public DefaultDepartment(List<GenericTypedGrPostal> addressLines, List<GenericTypedGrPostal> departmentNames,
            MailStop mailStop, PostalCode postalCode, String type){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.departmentNames = (departmentNames == null) ? EMPTY_LIST : departmentNames;
        this.mailStop = mailStop;
        this.postalCode = postalCode;
        this.type = type;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getAddressLines() {return this.addressLines;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getDepartmentNames() {return this.departmentNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public MailStop getMailStop() {return this.mailStop;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalCode getPostalCode() {return this.postalCode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {return this.type;}

}
