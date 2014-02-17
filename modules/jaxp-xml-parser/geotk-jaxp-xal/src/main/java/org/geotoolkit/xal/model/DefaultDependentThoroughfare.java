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
public class DefaultDependentThoroughfare implements DependentThoroughfare {

    private List<GenericTypedGrPostal> addressLines;
    private GenericTypedGrPostal thoroughfarePreDirection;
    private GenericTypedGrPostal thoroughfareLeadingType;
    private List<GenericTypedGrPostal> thoroughfareNames;
    private GenericTypedGrPostal thoroughfareTrailingType;
    private GenericTypedGrPostal thoroughfarePostDirection;
    private String type;

    public DefaultDependentThoroughfare(){
        this.addressLines = EMPTY_LIST;
        this.thoroughfareNames = EMPTY_LIST;
    }
    /**
     *
     * @param addressLines
     * @param thoroughfarePreDirection
     * @param thoroughfareLeadingType
     * @param thoroughfareNames
     * @param thoroughfareTrailingType
     * @param thoroughfarePostDirection
     * @param type
     */
    public DefaultDependentThoroughfare(List<GenericTypedGrPostal> addressLines,
            GenericTypedGrPostal thoroughfarePreDirection, GenericTypedGrPostal thoroughfareLeadingType,
            List<GenericTypedGrPostal> thoroughfareNames, GenericTypedGrPostal thoroughfareTrailingType,
            GenericTypedGrPostal thoroughfarePostDirection, String type){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.thoroughfarePreDirection = thoroughfarePreDirection;
        this.thoroughfareLeadingType = thoroughfareLeadingType;
        this.thoroughfareNames = (thoroughfareNames == null) ? EMPTY_LIST : thoroughfareNames;
        this.thoroughfareTrailingType = thoroughfareTrailingType;
        this.thoroughfarePostDirection = thoroughfarePostDirection;
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
    public GenericTypedGrPostal getThoroughfarePreDirection() {return this.thoroughfarePreDirection;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getThoroughfareLeadingType() {return this.thoroughfareLeadingType;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getThoroughfareNames() {return this.thoroughfareNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getThoroughfareTrailingType() {return this.thoroughfareTrailingType;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getThoroughfarePostDirection() {return this.thoroughfarePostDirection;}

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
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setThoroughfarePredirection(GenericTypedGrPostal thoroughfarePreDirection) {
        this.thoroughfarePreDirection = thoroughfarePreDirection;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setThoroughfareLeadingType(GenericTypedGrPostal thoroughfareLeadingType) {
        this.thoroughfareLeadingType = thoroughfareLeadingType;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setThoroughfareNames(List<GenericTypedGrPostal> thoroughfareNames) {
        this.thoroughfareNames = (thoroughfareNames == null) ? EMPTY_LIST : thoroughfareNames;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setThoroughfareTrailingType(GenericTypedGrPostal thoroughfareTrailingType) {
        this.thoroughfareTrailingType = thoroughfareTrailingType;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setThoroughfarePostDirection(GenericTypedGrPostal thoroughfarePostDirection) {
        this.thoroughfarePostDirection = thoroughfarePostDirection;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public void setType(String type) {
        this.type = type;
    }

}
