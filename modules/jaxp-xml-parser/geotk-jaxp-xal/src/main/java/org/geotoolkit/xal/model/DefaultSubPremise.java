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
public class DefaultSubPremise implements SubPremise {

    private List<GenericTypedGrPostal> addressLines;
    private List<SubPremiseName> subPremiseNames;
    private SubPremiseLocation subPremiseLocation;
    private List<SubPremiseNumber> subPremiseNumbers;
    private List<SubPremiseNumberPrefix> subPremiseNumberPrefixes;
    private List<SubPremiseNumberSuffix> subPremiseNumberSuffixes;
    private List<BuildingName> buildingNames;
    private Firm firm;
    private MailStop mailStop;
    private PostalCode postalCode;
    private SubPremise subPremise;
    private String type;

    public DefaultSubPremise() {
        this.addressLines = EMPTY_LIST;
        this.subPremiseNames = EMPTY_LIST;
        this.subPremiseNumbers = EMPTY_LIST;
        this.subPremiseNumberPrefixes = EMPTY_LIST;
        this.subPremiseNumberSuffixes = EMPTY_LIST;
        this.buildingNames = EMPTY_LIST;
    }

    /**
     *
     * @param addressLines
     * @param subPremiseNames
     * @param location
     * @param subPremiseNumberPrefixes
     * @param subPremiseNumberSuffixes
     * @param buildingNames
     * @param firm
     * @param mailStop
     * @param postalCode
     * @param subPremise
     * @param type
     * @throws XalException
     */
    public DefaultSubPremise(List<GenericTypedGrPostal> addressLines,
            List<SubPremiseName> subPremiseNames, Object location,
            List<SubPremiseNumberPrefix> subPremiseNumberPrefixes,
            List<SubPremiseNumberSuffix> subPremiseNumberSuffixes,
            List<BuildingName> buildingNames, Firm firm, MailStop mailStop,
            PostalCode postalCode, SubPremise subPremise, String type) throws XalException {
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.subPremiseNames = (subPremiseNames == null) ? EMPTY_LIST : subPremiseNames;
        if (location instanceof SubPremiseLocation) {
            this.subPremiseLocation = (SubPremiseLocation) location;
            this.subPremiseNumbers = EMPTY_LIST;
        } else if (location instanceof List) {
            try {
                this.subPremiseNumbers = (List<SubPremiseNumber>) location;
            } catch (ClassCastException e) {
                throw new XalException("This kind of location (" + location.getClass() + ") is not allowed here : " + this.getClass());
            }
        } else if (location != null) {
            throw new XalException("This kind of location (" + location.getClass() + ") is not allowed here : " + this.getClass());
        } else {
            this.subPremiseNumbers = EMPTY_LIST;
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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getAddressLines() {
        return this.addressLines;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SubPremiseName> getSubPremiseNames() {
        return this.subPremiseNames;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SubPremiseLocation getSubPremiseLocation() {
        return this.subPremiseLocation;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SubPremiseNumber> getSubPremiseNumbers() {
        return this.subPremiseNumbers;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SubPremiseNumberPrefix> getSubPremiseNumberPrefixes() {
        return this.subPremiseNumberPrefixes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SubPremiseNumberSuffix> getSubPremiseNumberSuffixes() {
        return this.subPremiseNumberSuffixes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<BuildingName> getBuildingNames() {
        return this.buildingNames;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Firm getFirm() {
        return this.firm;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public MailStop getMailStop() {
        return this.mailStop;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalCode getPostalCode() {
        return this.postalCode;
    }

    @Override
    public SubPremise getSubPremise() {
        return this.subPremise;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {
        return this.type;
    }

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
    public void setSubPremiseNames(List<SubPremiseName> subPremiseNames) {
        this.subPremiseNames = subPremiseNames;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSubPremiseLocation(SubPremiseLocation subPremiseLocation) {
        this.subPremiseLocation = subPremiseLocation;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSubPremiseNumbers(List<SubPremiseNumber> subPremiseNumbers) {
        this.subPremiseNumbers = subPremiseNumbers;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSubPremiseNumberPrefixes(List<SubPremiseNumberPrefix> subPremiseNumberPrefixes) {
        this.subPremiseNumberPrefixes = subPremiseNumberPrefixes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSubPremiseNumberSuffixes(List<SubPremiseNumberSuffix> subPremiseNumberSuffixes) {
        this.subPremiseNumberSuffixes = subPremiseNumberSuffixes;
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
    public void setSubPremise(SubPremise subPremise) {
        this.subPremise = subPremise;
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
