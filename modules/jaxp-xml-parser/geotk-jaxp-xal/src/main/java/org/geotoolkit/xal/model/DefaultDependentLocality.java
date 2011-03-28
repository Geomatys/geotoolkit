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
public class DefaultDependentLocality implements DependentLocality {

    private List<GenericTypedGrPostal> addressLines;
    private List<GenericTypedGrPostal> dependentLocalityNames;
    private DependentLocalityNumber dependentLocalityNumber;
    private PostBox postBox;
    private LargeMailUser largeMailUser;
    private PostOffice postOffice;
    private PostalRoute postalRoute;
    private Thoroughfare thoroughfare;
    private Premise premise;
    private DependentLocality dependentLocality;
    private PostalCode postalCode;
    private String type;
    private String usageType;
    private String connector;
    private String indicator;

    public DefaultDependentLocality(){
        this.addressLines = EMPTY_LIST;
        this.dependentLocalityNames = EMPTY_LIST;
    }

    /**
     *
     * @param addressLines
     * @param dependentLocalityNames
     * @param dependentLocalityNumber
     * @param localisation
     * @param thoroughfare
     * @param premise
     * @param dependentLocality
     * @param postalCode
     * @param type
     * @param usageType
     * @param connector
     * @param indicator
     * @throws XalException
     */
    public DefaultDependentLocality(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> dependentLocalityNames,
            DependentLocalityNumber dependentLocalityNumber,
            Object localisation, Thoroughfare thoroughfare, Premise premise,
            DependentLocality dependentLocality, PostalCode postalCode,
            String type, String usageType, String connector, String indicator) throws XalException{
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.dependentLocalityNames = (dependentLocalityNames == null) ? EMPTY_LIST : dependentLocalityNames;
        this.dependentLocalityNumber = dependentLocalityNumber;
        if (localisation instanceof PostBox){
            this.postBox = (PostBox) localisation;
        } else if (localisation instanceof LargeMailUser){
            this.largeMailUser = (LargeMailUser) localisation;
        } else if (localisation instanceof PostOffice){
            this.postOffice = (PostOffice) localisation;
        } else if (localisation instanceof PostalRoute){
            this.postalRoute = (PostalRoute) localisation;
        } else if ( localisation != null){
            throw new XalException("This kind of localisation is not allowed here."+localisation.getClass());
        }
        this.thoroughfare = thoroughfare;
        this.premise = premise;
        this.dependentLocality = dependentLocality;
        this.postalCode = postalCode;
        this.type = type;
        this.usageType = usageType;
        this.connector = connector;
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
    public List<GenericTypedGrPostal> getDependentLocalityNames() {return this.dependentLocalityNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public DependentLocalityNumber getDependentLocalityNumber() {return this.dependentLocalityNumber;}

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
    public LargeMailUser getLargeMailUser() {return this.largeMailUser;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostOffice getPostOffice() {return this.postOffice;}

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
    public Thoroughfare getThoroughfare() {return this.thoroughfare;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Premise getPremise() {return this.premise;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public DependentLocality getDependentLocality() {return this.dependentLocality;}

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
    public String getUsageType() {return this.usageType;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getConnector() {return this.connector;}

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
        this.addressLines = addressLines;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDependentLocalityNames(List<GenericTypedGrPostal> dependentLocalityNames) {
        this.dependentLocalityNames = dependentLocalityNames;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDependentLocalityNumber(DependentLocalityNumber dependentLocalityNumber) {
        this.dependentLocalityNumber = dependentLocalityNumber;
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
    public void setLargeMailUser(LargeMailUser largeMailUser) {
        this.largeMailUser = largeMailUser;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPostOffice(PostOffice postOffice) {
        this.postOffice = postOffice;
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
    public void setThoroughfare(Thoroughfare thoroughfare) {
        this.thoroughfare = thoroughfare;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPremise(Premise premise) {
        this.premise = premise;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDependentLocality(DependentLocality dependentLocality) {
        this.dependentLocality = dependentLocality;
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
    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setConnector(String connector) {
        this.connector = connector;
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
