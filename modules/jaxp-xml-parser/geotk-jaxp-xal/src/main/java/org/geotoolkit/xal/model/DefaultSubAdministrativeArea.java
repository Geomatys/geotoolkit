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
public class DefaultSubAdministrativeArea implements SubAdministrativeArea {

    private final List<GenericTypedGrPostal> addressLines;
    private final List<GenericTypedGrPostal> subAdministrativeAreaNames;
    private Locality locality;
    private PostOffice postOffice;
    private PostalCode postalCode;
    private final String type;
    private final String usageType;
    private final String indicator;

    /**
     * 
     * @param addressLines
     * @param subAdministrativeAreaNames
     * @param localisation
     * @param type
     * @param usageType
     * @param indicator
     * @throws XalException
     */
    public DefaultSubAdministrativeArea(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> subAdministrativeAreaNames,
            Object localisation, String type, String usageType, String indicator) throws XalException{
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.subAdministrativeAreaNames = (subAdministrativeAreaNames == null) ? EMPTY_LIST : subAdministrativeAreaNames;
        if (localisation instanceof Locality){
            this.locality = (Locality) localisation;
        } else if (localisation instanceof PostOffice){
            this.postOffice = (PostOffice) localisation;
        } else if (localisation instanceof PostalCode){
            this.postalCode = (PostalCode) localisation;
        } else if (localisation != null){
            throw new XalException("This kind of type is not allowed here.");
        }
        this.type = type;
        this.usageType = usageType;
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
    public List<GenericTypedGrPostal> getSubAdministrativeAreaNames() {return this.subAdministrativeAreaNames;}

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
    public PostOffice getPostOffice() {return this.postOffice;}

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
    public String getIndicator() {return this.indicator;}

}
