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
public class DefaultPremise implements Premise{

    private List<GenericTypedGrPostal> addressLines;
    private List<PremiseName> premiseNames;
    private PremiseLocation premiseLocation;
    private List<PremiseNumber> premiseNumbers;
    private PremiseNumberRange premiseNumberRange;
    private List<PremiseNumberPrefix> premiseNumberPrefixes;
    private List<PremiseNumberSuffix> premiseNumberSuffixes;
    private List<BuildingName> buildingNames;
    private List<SubPremise> subPremises;
    private Firm firm;
    private MailStop mailStop;
    private PostalCode postalCode;
    private Premise premise;
    private String type;
    private String premiseDependency;
    private String premiseDependencyType;
    private String premiseThoroughfareConnector;

    public DefaultPremise(){
        this.addressLines = EMPTY_LIST;
        this.premiseNames = EMPTY_LIST;
        this.premiseNumbers = EMPTY_LIST;
        this.premiseNumberPrefixes = EMPTY_LIST;
        this.premiseNumberSuffixes = EMPTY_LIST;
        this.buildingNames = EMPTY_LIST;
        this.subPremises =  EMPTY_LIST;
    }

    /**
     * 
     * @param addressLines
     * @param premiseNames
     * @param localisation
     * @param premiseNumberPrefixes
     * @param premiseNumberSuffixes
     * @param buildingNames
     * @param sub
     * @param mailStop
     * @param postalCode
     * @param premise
     * @param type
     * @param premiseDependency
     * @param premiseDependencyType
     * @param premiseThoroughfareConnector
     * @throws XalException
     */
    public DefaultPremise(List<GenericTypedGrPostal> addressLines, List<PremiseName> premiseNames,
            Object location,
            List<PremiseNumberPrefix> premiseNumberPrefixes,
            List<PremiseNumberSuffix> premiseNumberSuffixes,
            List<BuildingName> buildingNames,
            Object sub,
            MailStop mailStop, PostalCode postalCode, Premise premise,
            String type, String premiseDependency, String premiseDependencyType,
            String premiseThoroughfareConnector) throws XalException{
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.premiseNames = (premiseNames == null) ? EMPTY_LIST : premiseNames;
        if (location instanceof PremiseLocation){
            premiseLocation = (PremiseLocation) location;
            premiseNumbers = EMPTY_LIST;
        } else if (location instanceof List){
            try {
                premiseNumbers = (List<PremiseNumber>) location;
            } catch (ClassCastException e){
                throw new XalException("This kind of location ("+location.getClass()+") is not allowed here : "+this.getClass());
            }
        } else if (location instanceof PremiseNumberRange){
            premiseNumberRange = (PremiseNumberRange) location;
            premiseNumbers = EMPTY_LIST;
        } else if (location != null){
            throw new XalException("This kind of location ("+location.getClass()+") is not allowed here : "+this.getClass());
        } else {
            premiseNumbers = EMPTY_LIST;
        }
        this.premiseNumberPrefixes = (premiseNumberPrefixes == null) ? EMPTY_LIST : premiseNumberPrefixes;
        this.premiseNumberSuffixes = (premiseNumberSuffixes == null) ? EMPTY_LIST : premiseNumberSuffixes;
        this.buildingNames = (buildingNames == null) ? EMPTY_LIST : buildingNames;
        if (sub instanceof List){
            try {
                subPremises =  (List<SubPremise>) sub;
            } catch (ClassCastException e){
                throw new XalException("This kind of class ("+sub.getClass()+") is not allowed here : "+this.getClass());
            }
        } else if (sub instanceof Firm){
            firm =  (Firm) sub;
            subPremises = EMPTY_LIST;
        } else if (sub != null){
            throw new XalException("This kind of class ("+sub.getClass()+") is not allowed here : "+this.getClass());
        } else {
            subPremises = EMPTY_LIST;
        }
        this.mailStop = mailStop;
        this.postalCode = postalCode;
        this.premise = premise;
        this.type = type;
        this.premiseDependency = premiseDependency;
        this.premiseDependencyType = premiseDependencyType;
        this.premiseThoroughfareConnector = premiseThoroughfareConnector;
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
    public List<PremiseName> getPremiseNames() {return this.premiseNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseLocation getPremiseLocation() {return this.premiseLocation;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<PremiseNumber> getPremiseNumbers() {return this.premiseNumbers;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseNumberRange getPremiseNumberRange() {return this.premiseNumberRange;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<PremiseNumberPrefix> getPremiseNumberPrefixes() {return this.premiseNumberPrefixes;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<PremiseNumberSuffix> getPremiseNumberSuffixes() {return this.premiseNumberSuffixes;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<BuildingName> getBuildingNames() {return this.buildingNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SubPremise> getSubPremises() {return this.subPremises;}

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
    public Premise getPremise() {return this.premise;}

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
    public String getPremiseDependency() {return this.premiseDependency;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getPremiseDependencyType() {return this.premiseDependencyType;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getPremiseThoroughfareConnector() {return this.premiseThoroughfareConnector;}

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
    public void setPremiseNames(List<PremiseName> premiseNames) {
        this.premiseNames = (premiseNames == null) ? EMPTY_LIST : premiseNames;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPremiseLocation(PremiseLocation premiseLocation) {
        this.premiseLocation = premiseLocation;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPremiseNumbers(List<PremiseNumber> premiseNumbers) {
        this.premiseNumbers = (premiseNumbers == null) ? EMPTY_LIST : premiseNumbers;
    }
    
    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPremiseNumberRange(PremiseNumberRange premiseNumberRange) {
        this.premiseNumberRange = premiseNumberRange;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPremiseNumberPrefixes(List<PremiseNumberPrefix> premiseNumberPrefixes) {
        this.premiseNumberPrefixes = premiseNumberPrefixes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPremiseNumberSuffixes(List<PremiseNumberSuffix> premiseNumberSuffixes) {
        this.premiseNumberSuffixes = premiseNumberSuffixes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBuildingNames(List<BuildingName> buildingNames) {
        this.buildingNames = buildingNames;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSubPremises(List<SubPremise> subPremises) {
        this.subPremises = subPremises;
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
    public void setPremise(Premise premise) {
        this.premise = premise;
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
    public void setPremiseDependency(String premiseDependency) {
        this.premiseDependency = premiseDependency;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPremiseDependencyType(String premiseDependencyType) {
        this.premiseDependencyType = premiseDependencyType;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPremiseThoroughfareConnector(String premiseThoroughfareConnector) {
        this.premiseThoroughfareConnector = premiseThoroughfareConnector;
    }

}
