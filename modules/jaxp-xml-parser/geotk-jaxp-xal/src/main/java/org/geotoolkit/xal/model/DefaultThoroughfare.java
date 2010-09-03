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
public class DefaultThoroughfare implements Thoroughfare {

    private List<GenericTypedGrPostal> addressLines;
    private List<Object> thoroughfareNumbers;
    private List<ThoroughfareNumberPrefix> thoroughfareNumberPrefixes;
    private List<ThoroughfareNumberSuffix> thoroughfareNumberSuffixes;
    private GenericTypedGrPostal thoroughfarePreDirection;
    private GenericTypedGrPostal thoroughfareLeadingType;
    private List<GenericTypedGrPostal> thoroughfareNames;
    private GenericTypedGrPostal thoroughfareTrailingType;
    private GenericTypedGrPostal thoroughfarePostDirection;
    private DependentThoroughfare dependentThoroughfare;
    private DependentLocality dependentLocality;
    private Premise premise;
    private Firm firm;
    private PostalCode postalCode;
    private String type;
    private DependentThoroughfares dependentThoroughfares;
    private String dependentThoroughfaresIndicator;
    private String dependentThoroughfaresConnector;
    private String dependentThoroughfaresType;

    public DefaultThoroughfare(){
        this.addressLines = EMPTY_LIST;
        this.thoroughfareNumbers = EMPTY_LIST;
        this.thoroughfareNumberPrefixes = EMPTY_LIST;
        this.thoroughfareNumberSuffixes = EMPTY_LIST;
        this.thoroughfareNames = EMPTY_LIST;
    }

    /**
     *
     * @param addressLines
     * @param thoroughfareNumbers
     * @param thoroughfareNumberPrefixes
     * @param thoroughfareNumberSuffixes
     * @param thoroughfarePreDirection
     * @param thoroughfareLeadingType
     * @param thoroughfareNames
     * @param thoroughfareTrailingType
     * @param thoroughfarePostDirection
     * @param dependentThoroughfare
     * @param location
     * @param type
     * @param dependentThoroughfares
     * @param dependentThoroughfaresIndicator
     * @param dependentThoroughfaresConnector
     * @param dependentThoroughfaresType
     * @throws XalException
     */
    public DefaultThoroughfare(List<GenericTypedGrPostal> addressLines, List<Object> thoroughfareNumbers,
            List<ThoroughfareNumberPrefix> thoroughfareNumberPrefixes,
            List<ThoroughfareNumberSuffix> thoroughfareNumberSuffixes,
            GenericTypedGrPostal thoroughfarePreDirection,
            GenericTypedGrPostal thoroughfareLeadingType,
            List<GenericTypedGrPostal> thoroughfareNames,
            GenericTypedGrPostal thoroughfareTrailingType,
            GenericTypedGrPostal thoroughfarPostDirection,
            DependentThoroughfare dependentThoroughfare,
            Object location,
            String type, DependentThoroughfares dependentThoroughfares, String dependentThoroughfaresIndicator,
            String dependentThoroughfaresConnector, String dependentThoroughfaresType) throws XalException{
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.thoroughfareNumbers = (thoroughfareNumbers == null) ? EMPTY_LIST : this.verifThoroughfareNumbers(thoroughfareNumbers);
        this.thoroughfareNumberPrefixes = (thoroughfareNumberPrefixes == null) ? EMPTY_LIST : thoroughfareNumberPrefixes;
        this.thoroughfareNumberSuffixes = (thoroughfareNumberSuffixes == null) ? EMPTY_LIST : thoroughfareNumberSuffixes;
        this.thoroughfarePreDirection = thoroughfarePreDirection;
        this.thoroughfareLeadingType = thoroughfareLeadingType;
        this.thoroughfareNames = (thoroughfareNames == null) ? EMPTY_LIST : thoroughfareNames;
        this.thoroughfareTrailingType = thoroughfareTrailingType;
        this.thoroughfarePostDirection = thoroughfarPostDirection;
        this.dependentThoroughfare = dependentThoroughfare;
        if (location instanceof DependentLocality){
            this.dependentLocality = (DependentLocality) location;
        } else if (location instanceof Premise){
            this.premise = (Premise) location;
        } else if (location instanceof Firm){
            this.firm = (Firm) location;
        } else if (location instanceof PostalCode){
            this.postalCode = (PostalCode) location;
        } else if (location != null){
            throw new XalException("This kind of location ("+location.getClass()+") is not allowed here : "+this.getClass());
        }
        this.type = type;
        this.dependentThoroughfares = dependentThoroughfares;
        this.dependentThoroughfaresIndicator = dependentThoroughfaresIndicator;
        this.dependentThoroughfaresConnector = dependentThoroughfaresConnector;
        this.dependentThoroughfaresType = dependentThoroughfaresType;
    }

    /**
     *
     * @param thoroughfareNumbers
     * @return
     * @throws XalException
     */
    private List<Object> verifThoroughfareNumbers(List<Object> thoroughfareNumbers) throws XalException{
        for (Object thoroughfareNumber : thoroughfareNumbers){
            if(!(thoroughfareNumber instanceof ThoroughfareNumber) && !(thoroughfareNumber instanceof ThoroughfareNumberRange))
                throw new XalException("This kind of thoroughfareNumber ("+thoroughfareNumber.getClass()+") is not allowed here : "+this.getClass());
        }
        return thoroughfareNumbers;
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
    public List<Object> getThoroughfareNumbers() {return this.thoroughfareNumbers;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<ThoroughfareNumberPrefix> getThoroughfareNumberPrefixes() {return this.thoroughfareNumberPrefixes;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<ThoroughfareNumberSuffix> getThoroughfareNumberSuffixes() {return this.thoroughfareNumberSuffixes;}

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
    public DependentThoroughfare getDependentThoroughfare() {return this.dependentThoroughfare;}

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
    public Premise getPremise() {return this.premise;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Firm getFirm() {return this.firm;}

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
    public DependentThoroughfares getDependentThoroughfares() {return this.dependentThoroughfares;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getDependentThoroughfaresIndicator() {return this.dependentThoroughfaresIndicator;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getDependentThoroughfaresConnector() {return this.dependentThoroughfaresConnector;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getDependentThoroughfaresType() {return this.dependentThoroughfaresType;}

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
    public void setThoroughfareNumbers(List<Object> thoroughfareNumbers) {
        this.thoroughfareNumbers = (thoroughfareNumbers == null) ? EMPTY_LIST : thoroughfareNumbers;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setThoroughfareNumberPrefixes(List<ThoroughfareNumberPrefix> thoroughfareNumberPrefixes) {
        this.thoroughfareNumberPrefixes = (thoroughfareNumberPrefixes == null) ? EMPTY_LIST : thoroughfareNumberPrefixes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setThoroughfareNumberSuffixes(List<ThoroughfareNumberSuffix> thoroughfareNumberSuffixes) {
        this.thoroughfareNumberSuffixes = (thoroughfareNumberSuffixes == null) ? EMPTY_LIST : thoroughfareNumberSuffixes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setThoroughfarePreDirection(GenericTypedGrPostal thoroughfarePreDirection) {
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
     * @{@inheritDoc }
     */
    @Override
    public void setDependentThoroughfare(DependentThoroughfare dependentThoroughfare) {
        this.dependentThoroughfare = dependentThoroughfare;
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
    public void setPremise(Premise premise) {
        this.premise = premise;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setFirm(Firm firm) {
        this.firm = firm;
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
    public void setDependentThoroughfares(DependentThoroughfares dependentThoroughfares) {
        this.dependentThoroughfares = dependentThoroughfares;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDependentThoroughfaresIndicator(String dependentThoroughfaresIndicator) {
        this.dependentThoroughfaresIndicator = dependentThoroughfaresIndicator;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDependentThoroughfaresConnector(String dependentThoroughfaresConnector) {
        this.dependentThoroughfaresConnector = dependentThoroughfaresConnector;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDependentThoroughfaresType(String dependentThoroughfaresType) {
        this.dependentThoroughfaresType = dependentThoroughfaresType;
    }

}
