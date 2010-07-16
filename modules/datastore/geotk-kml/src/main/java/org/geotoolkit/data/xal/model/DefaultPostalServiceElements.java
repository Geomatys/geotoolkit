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
public class DefaultPostalServiceElements implements PostalServiceElements{

    private final List<AddressIdentifier> addressIdentifiers;
    private final GenericTypedGrPostal endorsementLineCode;
    private final GenericTypedGrPostal keyLineCode;
    private final GenericTypedGrPostal barCode;
    private final SortingCode sortingCode;
    private final GenericTypedGrPostal addressLatitude;
    private final GenericTypedGrPostal addressLatitudeDirection;
    private final GenericTypedGrPostal addressLongitude;
    private final GenericTypedGrPostal addressLongitudeDirection;
    private final List<GenericTypedGrPostal> supplementaryPostalServiceData;
    private final String type;

    public DefaultPostalServiceElements(List<AddressIdentifier> addressIdentifiers, GenericTypedGrPostal endorsementLineCode,
            GenericTypedGrPostal keyLineCode, GenericTypedGrPostal barCode, SortingCode sortingCode, GenericTypedGrPostal addressLatitude,
            GenericTypedGrPostal addressLatitudeDirection, GenericTypedGrPostal addressLongitude, GenericTypedGrPostal addressLongitudeDirection,
            List<GenericTypedGrPostal> supplementaryPostalServiceData, String type){
        this.addressIdentifiers = (addressIdentifiers == null) ? EMPTY_LIST : addressIdentifiers;
        this.endorsementLineCode = endorsementLineCode;
        this.keyLineCode = keyLineCode;
        this.barCode = barCode;
        this.sortingCode = sortingCode;
        this.addressLatitude = addressLatitude;
        this.addressLatitudeDirection = addressLatitudeDirection;
        this.addressLongitude = addressLongitude;
        this.addressLongitudeDirection = addressLongitudeDirection;
        this.supplementaryPostalServiceData = (supplementaryPostalServiceData == null) ? EMPTY_LIST : supplementaryPostalServiceData;
        this.type = type;
    }

    @Override
    public List<AddressIdentifier> getAddressIdentifiers() {return this.addressIdentifiers;}

    @Override
    public GenericTypedGrPostal getEndorsementLineCode() {return this.endorsementLineCode;}

    @Override
    public GenericTypedGrPostal getKeyLineCode() {return this.keyLineCode;}

    @Override
    public GenericTypedGrPostal getBarcode() {return this.barCode;}

    @Override
    public SortingCode getSortingCode() {return this.sortingCode;}

    @Override
    public GenericTypedGrPostal getAddressLatitude() {return this.addressLatitude;}

    @Override
    public GenericTypedGrPostal getAddressLatitudeDirection() {return this.addressLatitudeDirection;}

    @Override
    public GenericTypedGrPostal getAddressLongitude() {return this.addressLongitude;}

    @Override
    public GenericTypedGrPostal getAddressLongitudeDirection() {return this.addressLongitudeDirection;}

    @Override
    public List<GenericTypedGrPostal> getSupplementaryPostalServiceData() {return this.supplementaryPostalServiceData;}

    @Override
    public String getType() {return this.type;}

}
