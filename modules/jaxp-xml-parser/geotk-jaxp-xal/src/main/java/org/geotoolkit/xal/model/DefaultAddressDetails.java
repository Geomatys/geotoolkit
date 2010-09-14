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

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAddressDetails implements AddressDetails {

    private PostalServiceElements postalServiceElements;
    private GenericTypedGrPostal address;
    private AddressLines addressLines;
    private Country country;
    private AdministrativeArea administrativeArea;
    private Locality locality;
    private Thoroughfare thoroughfare;
    private String addressType;
    private String currentStatus;
    private String validFromDate;
    private String validToDate;
    private String usage;
    private GrPostal grPostal;
    private String addressDetailsKey;

    public DefaultAddressDetails(){}
    
    /**
     * 
     * @param postalServiceElements
     * @param localisation
     * @param addressType
     * @param currentStatus
     * @param validFromDate
     * @param validToDate
     * @param usage
     * @param grPostal
     * @param addressDetailsKey
     * @throws XalException
     */
    public DefaultAddressDetails(PostalServiceElements postalServiceElements, 
            Object localisation, String addressType, String currentStatus,
            String validFromDate, String validToDate,
            String usage, GrPostal grPostal, String addressDetailsKey)
            throws XalException{
        this.postalServiceElements = postalServiceElements;
        if (localisation instanceof GenericTypedGrPostal){
            this.address = (GenericTypedGrPostal) localisation;
        } else if (localisation instanceof AddressLines){
            this.addressLines = (AddressLines) localisation;
        } else if (localisation instanceof Country){
            this.country = (Country) localisation;
        } else if (localisation instanceof AdministrativeArea){
            this.administrativeArea = (AdministrativeArea) localisation;
        } else if (localisation instanceof Locality){
            this.locality = (Locality) localisation;
        } else if (localisation instanceof Thoroughfare){
            this.thoroughfare = ((Thoroughfare) localisation);
        } else if (localisation !=  null) {
            throw new XalException("This kind of localisation is not allowed.");
        }

        this.addressType = addressType;
        this.currentStatus = currentStatus;
        this.validFromDate = validFromDate;
        this.validToDate = validToDate;
        this.usage = usage;
        this.grPostal = grPostal;
        this.addressDetailsKey = addressDetailsKey;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalServiceElements getPostalServiceElements() {return this.postalServiceElements;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GenericTypedGrPostal getAddress() {return this.address;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AddressLines getAddressLines() {return this.addressLines;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Country getCountry() {return this.country;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AdministrativeArea getAdministrativeArea() {return this.administrativeArea;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Locality getLocality() {return this.locality;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Thoroughfare getThoroughfare() {return this.thoroughfare;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getAddressType() {return this.addressType;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getCurrentStatus() {return this.currentStatus;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getValidFromDate() {return this.validFromDate;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getValidToDate() {return this.validToDate;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getUsage() {return this.usage;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getAddressDetailsKey() {return this.addressDetailsKey;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostalServiceElements(PostalServiceElements postalServiceElements) {
        this.postalServiceElements = postalServiceElements;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAddress(GenericTypedGrPostal address) {
        this.address = address;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAddressLines(AddressLines addressLines) {
        this.addressLines = addressLines;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAdministrativeArea(AdministrativeArea administrativeArea) {
        this.administrativeArea = administrativeArea;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setThoroughfare(Thoroughfare throughfare) {
        this.thoroughfare = throughfare;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setValidFromDate(String validFromDate) {
        this.validFromDate = validFromDate;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setValidToDate(String validToDate) {
        this.validToDate = validToDate;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setGrPostal(GrPostal grPostal) {
        this.grPostal = grPostal;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAddressDetailsKey(String addressDetailsKey) {
        this.addressDetailsKey = addressDetailsKey;
    }

}
