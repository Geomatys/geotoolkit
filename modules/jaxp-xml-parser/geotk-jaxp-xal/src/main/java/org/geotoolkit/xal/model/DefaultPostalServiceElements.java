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
public class DefaultPostalServiceElements implements PostalServiceElements {

    private List<AddressIdentifier> addressIdentifiers;
    private GenericTypedGrPostal endorsementLineCode;
    private GenericTypedGrPostal keyLineCode;
    private GenericTypedGrPostal barCode;
    private SortingCode sortingCode;
    private GenericTypedGrPostal addressLatitude;
    private GenericTypedGrPostal addressLatitudeDirection;
    private GenericTypedGrPostal addressLongitude;
    private GenericTypedGrPostal addressLongitudeDirection;
    private List<GenericTypedGrPostal> supplementaryPostalServiceData;
    private String type;

    public DefaultPostalServiceElements() {
        this.addressIdentifiers = EMPTY_LIST;
        this.supplementaryPostalServiceData = EMPTY_LIST;
    }

    public DefaultPostalServiceElements(List<AddressIdentifier> addressIdentifiers,
            GenericTypedGrPostal endorsementLineCode, GenericTypedGrPostal keyLineCode,
            GenericTypedGrPostal barCode, SortingCode sortingCode, GenericTypedGrPostal addressLatitude,
            GenericTypedGrPostal addressLatitudeDirection, GenericTypedGrPostal addressLongitude,
            GenericTypedGrPostal addressLongitudeDirection,
            List<GenericTypedGrPostal> supplementaryPostalServiceData, String type) {
        this.addressIdentifiers = (addressIdentifiers == null) ? EMPTY_LIST : addressIdentifiers;
        this.endorsementLineCode = endorsementLineCode;
        this.keyLineCode = keyLineCode;
        this.barCode = barCode;
        this.sortingCode = sortingCode;
        this.addressLatitude = addressLatitude;
        this.addressLatitudeDirection = addressLatitudeDirection;
        this.addressLongitude = addressLongitude;
        this.addressLongitudeDirection = addressLongitudeDirection;
        this.supplementaryPostalServiceData = (supplementaryPostalServiceData == null)
                ? EMPTY_LIST : supplementaryPostalServiceData;
        this.type = type;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public List<AddressIdentifier> getAddressIdentifiers() {
        return this.addressIdentifiers;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getEndorsementLineCode() {
        return this.endorsementLineCode;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getKeyLineCode() {
        return this.keyLineCode;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getBarcode() {
        return this.barCode;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public SortingCode getSortingCode() {
        return this.sortingCode;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getAddressLatitude() {
        return this.addressLatitude;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getAddressLatitudeDirection() {
        return this.addressLatitudeDirection;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getAddressLongitude() {
        return this.addressLongitude;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getAddressLongitudeDirection() {
        return this.addressLongitudeDirection;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getSupplementaryPostalServiceData() {
        return this.supplementaryPostalServiceData;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public String getType() {
        return this.type;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public void setAddressIdentifiers(List<AddressIdentifier> addressIdentifiers) {
        this.addressIdentifiers = addressIdentifiers;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public void setEndorsementLineCode(GenericTypedGrPostal endorsementLineCode) {
        this.endorsementLineCode = endorsementLineCode;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public void setKeyLineCode(GenericTypedGrPostal keyLineCode) {
        this.keyLineCode = keyLineCode;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public void setBarcode(GenericTypedGrPostal barcode) {
        this.barCode = barcode;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public void setSortingCode(SortingCode sortingCode) {
        this.sortingCode = sortingCode;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public void setAddressLatitude(GenericTypedGrPostal addressLatitude) {
        this.addressLatitude = addressLatitude;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public void setAddressLatitudeDirection(GenericTypedGrPostal addressLatitudeDirection) {
        this.addressLatitudeDirection = addressLatitudeDirection;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public void setAddressLongitude(GenericTypedGrPostal addressLongitude) {
        this.addressLongitude = addressLongitude;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public void setAddressLongitudeDirection(GenericTypedGrPostal addressLongitudeDirection) {
        this.addressLongitudeDirection = addressLongitudeDirection;
    }

    /**
     *
     * {@inheritDoc }
     */
    @Override
    public void setSupplementaryPostalServiceData(List<GenericTypedGrPostal> supplementaryPostalServiceData) {
        this.supplementaryPostalServiceData = supplementaryPostalServiceData;
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
