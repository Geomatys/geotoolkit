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

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPostOffice implements PostOffice {

    private final List<GenericTypedGrPostal> addressLines;
    private List<GenericTypedGrPostal> postOfficeNames;
    private PostOfficeNumber postOfficeNumber;
    private final PostalRoute postalRoute;
    private final PostBox postBox;
    private final PostalCode postalCode;
    private final String type;
    private final String indicator;

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

        this.addressLines = addressLines;
        if (localisation instanceof List){
            try{
                this.postOfficeNames = (List<GenericTypedGrPostal>) localisation;
            } catch (ClassCastException e){
                throw new XalException("Cast error. List<GenericTypedGrPostal> requiered.");
            }
        } else if (localisation instanceof PostOfficeNumber){
            this.postOfficeNumber = (PostOfficeNumber) localisation;
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

}
