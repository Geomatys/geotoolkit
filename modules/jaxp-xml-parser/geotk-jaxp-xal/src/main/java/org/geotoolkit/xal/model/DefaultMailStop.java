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
public class DefaultMailStop implements MailStop {

    private List<GenericTypedGrPostal> addressLines;
    private List<GenericTypedGrPostal> mailStopNames;
    private MailStopNumber mailStopNumber;
    private String type;

    /**
     * 
     */
    public DefaultMailStop(){
        this.addressLines = EMPTY_LIST;
        this.mailStopNames = EMPTY_LIST;
    }

    /**
     *
     * @param addressLines
     * @param mailStopNames
     * @param mailStopNumber
     * @param type
     */
    public DefaultMailStop(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> mailStopNames,
            MailStopNumber mailStopNumber, String type){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.mailStopNames = (mailStopNames == null) ? EMPTY_LIST : mailStopNames;
        this.mailStopNumber = mailStopNumber;
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
    public List<GenericTypedGrPostal> getMailStopNames() {return this.mailStopNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public MailStopNumber getMailStopNumber() {return this.mailStopNumber;}

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
    public void setMailStopNames(List<GenericTypedGrPostal> mailStopNames) {
        this.mailStopNames = mailStopNames;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMailStopNumber(MailStopNumber mailStopNumber) {
        this.mailStopNumber = mailStopNumber;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setType(String type) {
        this.type = type;
    }

}
