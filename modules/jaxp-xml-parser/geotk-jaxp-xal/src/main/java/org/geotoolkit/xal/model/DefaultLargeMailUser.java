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
public class DefaultLargeMailUser implements LargeMailUser {

    private List<GenericTypedGrPostal> addressLines;
    private List<LargeMailUserName> largeMailUserNames;
    private LargeMailUserIdentifier largeMailUserIdentifier;
    private List<BuildingName> buildingNames;
    private Department department;
    private PostBox postBox;
    private Thoroughfare thoroughfare;
    private PostalCode postalCode;
    private String type;

    public DefaultLargeMailUser(){
        this.addressLines = EMPTY_LIST;
        this.largeMailUserNames = EMPTY_LIST;
        this.buildingNames = EMPTY_LIST;
    }

    /**
     * 
     * @param addressLines
     * @param largeMailUserNames
     * @param largeMailUserIdentifier
     * @param buildingNames
     * @param department
     * @param postBox
     * @param thoroughfare
     * @param postalCode
     * @param type
     */
    public DefaultLargeMailUser(List<GenericTypedGrPostal> addressLines,
            List<LargeMailUserName> largeMailUserNames, LargeMailUserIdentifier largeMailUserIdentifier,
            List<BuildingName> buildingNames, Department department, PostBox postBox,
            Thoroughfare thoroughfare, PostalCode postalCode, String type){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.largeMailUserNames = (largeMailUserNames == null) ? EMPTY_LIST : largeMailUserNames;
        this.largeMailUserIdentifier = largeMailUserIdentifier;
        this.buildingNames = (buildingNames == null) ? EMPTY_LIST : buildingNames;
        this.department = department;
        this.postBox = postBox;
        this.thoroughfare = thoroughfare;
        this.postalCode = postalCode;
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
    public List<LargeMailUserName> getLargeMailUserNames() {return this.largeMailUserNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LargeMailUserIdentifier getLargeMailUserIdentifier() {return this.largeMailUserIdentifier;}

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
    public Department getDepartment() {return this.department;}

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
    public Thoroughfare getThoroughfare() {return this.thoroughfare;}

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
    public void setAddressLines(List<GenericTypedGrPostal> addressLines) {
        this.addressLines = addressLines;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLargeMailUserNames(List<LargeMailUserName> largeMailUserNames) {
        this.largeMailUserNames = (largeMailUserNames == null) ? EMPTY_LIST : largeMailUserNames;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLargeMailUserIdentifier(LargeMailUserIdentifier largeMailUserIdentifier) {
        this.largeMailUserIdentifier = largeMailUserIdentifier;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBuildingNames(List<BuildingName> buildingNames) {
        this.buildingNames = (buildingNames == null) ? EMPTY_LIST : buildingNames;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDepartment(Department department) {
        this.department = department;
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
    public void setThoroughfare(Thoroughfare thoroughfare) {
        this.thoroughfare = thoroughfare;
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

}
