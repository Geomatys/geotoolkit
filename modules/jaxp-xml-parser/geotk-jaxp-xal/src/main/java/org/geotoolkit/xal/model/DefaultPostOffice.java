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
public class DefaultPostOffice implements PostOffice {

    private List<GenericTypedGrPostal> addressLines;
    private List<GenericTypedGrPostal> postOfficeNames;
    private PostOfficeNumber postOfficeNumber;
    private PostalRoute postalRoute;
    private PostBox postBox;
    private PostalCode postalCode;
    private String type;
    private String indicator;

    public DefaultPostOffice(){
        this.addressLines = EMPTY_LIST;
        this.postOfficeNames = EMPTY_LIST;
    }

    /**
     *
     * @param addressLines
     * @param localisation
     * @param postalRoute
     * @param postBox
     * @param postalCode
     * @param type
     * @param indicator
     * @throws XalException
     */
    public DefaultPostOffice(List<GenericTypedGrPostal> addressLines, Object localisation,
            PostalRoute postalRoute, PostBox postBox, PostalCode postalCode, String type, String indicator) throws XalException{

        this.addressLines = (addressLines == null) ? EMPTY_LIST : (List<GenericTypedGrPostal>) addressLines;
        if (localisation instanceof List){
            try{
                this.postOfficeNames = (localisation == null) ? EMPTY_LIST : (List<GenericTypedGrPostal>) localisation;
            } catch (ClassCastException e){
                throw new XalException("Cast error. List<GenericTypedGrPostal> requiered.");
            }
        } else if (localisation instanceof PostOfficeNumber){
            this.postOfficeNumber = (PostOfficeNumber) localisation;
            this.postOfficeNames = EMPTY_LIST;
        } else if (localisation != null) {
            throw new XalException("This kind of localisation is not allowed here."+this.getClass()+localisation);
        }
        this.postalRoute = postalRoute;
        this.postBox = postBox;
        this.postalCode = postalCode;
        this.type = type;
        this.indicator = indicator;
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
    public List<GenericTypedGrPostal> getPostOfficeNames() {return this.postOfficeNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostOfficeNumber getPostOfficeNumber() {return this.postOfficeNumber;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalRoute getPostalRoute() {return this.postalRoute;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBox getPostBox() {return this.postBox;}

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
    public String getIndicator() {return this.indicator;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAddressLines(List<GenericTypedGrPostal> addressLines) {
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostOfficeNames(List<GenericTypedGrPostal> postOfficeNames) {
        this.postOfficeNames = (postOfficeNames == null) ? EMPTY_LIST : postOfficeNames;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostOfficeNumber(PostOfficeNumber postOfficeNumber) {
        this.postOfficeNumber = postOfficeNumber;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostalRoute(PostalRoute postalRoute) {
        this.postalRoute = postalRoute;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostBox(PostBox postBox) {
        this.postBox = postBox;
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
        this.type = type;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

}
