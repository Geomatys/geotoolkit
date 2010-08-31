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
public class DefaultCountry implements Country {

    private List<GenericTypedGrPostal> addressLines;
    private List<CountryNameCode> countryNameCodes;
    private List<GenericTypedGrPostal> countryNames;
    private AdministrativeArea administrativeArea;
    private Locality locality;
    private Thoroughfare thoroughfare;

    public DefaultCountry(){
        this.addressLines = EMPTY_LIST;
        this.countryNameCodes = EMPTY_LIST;
        this.countryNames = EMPTY_LIST;
    }

    /**
     *
     * @param addressLines
     * @param countryNameCodes
     * @param countryNames
     * @param localisation
     * @throws XalException
     */
    public DefaultCountry(List<GenericTypedGrPostal> addressLines,
            List<CountryNameCode> countryNameCodes, List<GenericTypedGrPostal> countryNames, Object localisation) throws XalException{
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.countryNameCodes = (countryNameCodes == null) ? EMPTY_LIST : countryNameCodes;
        this.countryNames = (countryNames == null) ? EMPTY_LIST : countryNames;
        if (localisation instanceof AdministrativeArea){
            this.administrativeArea = (AdministrativeArea) localisation;
        } else if (localisation instanceof Locality){
            this.locality = (Locality) localisation;
        } else if (localisation instanceof Thoroughfare){
            this.thoroughfare = (Thoroughfare) localisation;
        } else if (localisation != null) {
            throw new XalException("This kind of localisation is not allowed here.");
        }
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
    public List<CountryNameCode> getCountryNameCodes() {return this.countryNameCodes;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getCountryNames() {return this.countryNames;}

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
    public void setAddressLines(List<GenericTypedGrPostal> addressLines) {
        this.addressLines = addressLines;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setCountryNamesCodes(List<CountryNameCode> countryNameCodes) {
        this.countryNameCodes = countryNameCodes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setCountryNames(List<GenericTypedGrPostal> countryNames) {
        this.countryNames = countryNames;
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
    public void setThoroughfare(Thoroughfare thoroughfare) {
        this.thoroughfare = thoroughfare;
    }
}
