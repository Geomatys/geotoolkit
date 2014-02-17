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
public class DefaultPostalRoute implements PostalRoute {

    private List<GenericTypedGrPostal> addressLines;
    private List<GenericTypedGrPostal> postalRouteNames;
    private PostalRouteNumber postalRouteNumber;
    private PostBox postBox;
    private String type;

    public DefaultPostalRoute(){
        this.addressLines = EMPTY_LIST;
        this.postalRouteNames = EMPTY_LIST;
    }

    /**
     * 
     * @param addressLines
     * @param localisation
     * @param postBox
     * @param type
     * @throws XalException
     */
    public DefaultPostalRoute(List<GenericTypedGrPostal> addressLines,
            Object localisation, PostBox postBox, String type) throws XalException{
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        if (localisation instanceof List){
            try{
                this.postalRouteNames = (List<GenericTypedGrPostal>) localisation;
            } catch (ClassCastException e){
                throw new XalException("Cast error. List<GenericTypedGrPostal> requiered.");
            }
        } else if (localisation instanceof PostalRouteNumber){
            this.postalRouteNumber = (PostalRouteNumber) localisation;
        } else if (localisation != null){
            throw new XalException("This kind of localisation is not allowed here.");
        }
        this.postBox = postBox;
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
    public List<GenericTypedGrPostal> getPostalRouteNames() {return this.postalRouteNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalRouteNumber getPostalRouteNumber() {return this.postalRouteNumber;}

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
    public void setPostalRouteNames(List<GenericTypedGrPostal> postalRouteNames) {
        this.postalRouteNames = postalRouteNames;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostalRouteNumber(PostalRouteNumber postalRouteNumber) {
        this.postalRouteNumber = postalRouteNumber;
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
    public void setType(String type) {
        this.type = type;
    }

}
