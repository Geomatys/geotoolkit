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
package org.geotoolkit.data.xal.model;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultSubPremise implements SubPremise {

    private final List<GenericTypedGrPostal> addressLines;
    private final List<SubPremiseName> subPremiseNames;
    private SubPremiseLocation subPremiseLocation;
    private List<SubPremiseNumber> subPremiseNumbers;
    private final List<SubPremiseNumberPrefix> subPremiseNumberPrefixes;
    private final List<SubPremiseNumberSuffix> subPremiseNumberSuffixes;
    private final List<BuildingName> buildingNames;
    private final Firm firm;
    private final MailStop mailStop;
    private final PostalCode postalCode;
    private final SubPremise subPremise;
    private final String type;

    public DefaultSubPremise(List<GenericTypedGrPostal> addressLines,
            List<SubPremiseName> subPremiseNames, Object location,
            List<SubPremiseNumberPrefix> subPremiseNumberPrefixes,
            List<SubPremiseNumberSuffix> subPremiseNumberSuffixes, 
            List<BuildingName> buildingNames, Firm firm, MailStop mailStop,
            PostalCode postalCode, SubPremise subPremise, String type) throws XalException{
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.subPremiseNames = (subPremiseNames == null) ? EMPTY_LIST : subPremiseNames;
        if (location instanceof SubPremiseLocation){
            this.subPremiseLocation = (SubPremiseLocation) location;
        } else if (location instanceof List){
            try {
                this.subPremiseNumbers = (List<SubPremiseNumber>) location;
            } catch (ClassCastException e){
                throw new XalException("This kind of location ("+location.getClass()+") is not allowed here : "+this.getClass());
            }
        } else if (location != null){
            throw new XalException("This kind of location ("+location.getClass()+") is not allowed here : "+this.getClass());
        }
        this.subPremiseNumberPrefixes = (subPremiseNumberPrefixes == null) ? EMPTY_LIST : subPremiseNumberPrefixes;
        this.subPremiseNumberSuffixes = (subPremiseNumberSuffixes == null) ? EMPTY_LIST : subPremiseNumberSuffixes;
        this.buildingNames = (buildingNames == null) ? EMPTY_LIST : buildingNames;
        this.firm = firm;
        this.mailStop = mailStop;
        this.postalCode = postalCode;
        this.subPremise = subPremise;
        this.type = type;
    }

    @Override
    public List<GenericTypedGrPostal> getAddressLines() {return this.addressLines;}

    @Override
    public List<SubPremiseName> getSubPremiseNames() {return this.subPremiseNames;}

    @Override
    public SubPremiseLocation getSubPremiseLocation() {return this.subPremiseLocation;}

    @Override
    public List<SubPremiseNumber> getSubPremiseNumbers() {return this.subPremiseNumbers;}

    @Override
    public List<SubPremiseNumberPrefix> getSubPremiseNumberPrefixes() {return this.subPremiseNumberPrefixes;}

    @Override
    public List<SubPremiseNumberSuffix> getSubPremiseNumberSuffixes() {return this.subPremiseNumberSuffixes;}

    @Override
    public List<BuildingName> getBuildingNames() {return this.buildingNames;}

    @Override
    public Firm getFirm() {return this.firm;}

    @Override
    public MailStop getMailStop() {return this.mailStop;}

    @Override
    public PostalCode getPostalCode() {return this.postalCode;}

    @Override
    public SubPremise getSubPremise() {return this.subPremise;}

    @Override
    public String getType() {return this.type;}

}
