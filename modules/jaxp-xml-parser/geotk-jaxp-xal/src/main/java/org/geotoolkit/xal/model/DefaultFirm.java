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
public class DefaultFirm implements Firm {

    private List<GenericTypedGrPostal> addressLines;
    private List<GenericTypedGrPostal> firmNames;
    private List<Department> departments;
    private MailStop mailStop;
    private PostalCode postalCode;
    private String type;

    public DefaultFirm(){
        this.addressLines = EMPTY_LIST;
        this.firmNames = EMPTY_LIST;
        this.departments = EMPTY_LIST;
    }

    /**
     *
     * @param addressLines
     * @param firmNames
     * @param departments
     * @param mailStop
     * @param postalCode
     * @param type
     */
    public DefaultFirm(List<GenericTypedGrPostal> addressLines, List<GenericTypedGrPostal> firmNames,
            List<Department> departments, MailStop mailStop, PostalCode postalCode, String type){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.firmNames = (firmNames == null) ? EMPTY_LIST : firmNames;
        this.departments = (departments == null) ? EMPTY_LIST : departments;
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
    public List<GenericTypedGrPostal> getFirmNames() {return this.firmNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Department> getDepartments() {return this.departments;}

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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAddressLines(List<GenericTypedGrPostal> addressLines) {
        this.addressLines = addressLines;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setFirmNames(List<GenericTypedGrPostal> firmNames) {
        this.firmNames = firmNames;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMailStop(MailStop mailStop) {
        this.mailStop = mailStop;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostalCode(PostalCode postalCode) {
        this.postalCode = postalCode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setType(String type) {
        this.type =type;
    }

}
