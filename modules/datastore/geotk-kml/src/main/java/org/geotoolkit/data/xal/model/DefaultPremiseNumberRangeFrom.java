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
public class DefaultPremiseNumberRangeFrom implements PremiseNumberRangeFrom {

    private final List<GenericTypedGrPostal> addressLines;
    private final List<PremiseNumberPrefix> premiseNumberPrefixes;
    private final List<PremiseNumber> premiseNumbers;
    private final List<PremiseNumberSuffix> premiseNumberSuffixes;

    /**
     * 
     * @param addressLines
     * @param premiseNumberPrefixes
     * @param premiseNumbers
     * @param premiseNumberSuffixes
     */
    public DefaultPremiseNumberRangeFrom(List<GenericTypedGrPostal> addressLines,
            List<PremiseNumberPrefix> premiseNumberPrefixes,
            List<PremiseNumber> premiseNumbers,
            List<PremiseNumberSuffix> premiseNumberSuffixes){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.premiseNumberPrefixes = (premiseNumberPrefixes == null) ? EMPTY_LIST : premiseNumberPrefixes;
        this.premiseNumbers = (premiseNumbers == null) ? EMPTY_LIST : premiseNumbers;
        this.premiseNumberSuffixes = (premiseNumberSuffixes == null) ? EMPTY_LIST : premiseNumberSuffixes;
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
    public List<PremiseNumberPrefix> getPremiseNumberPrefixes() {return this.premiseNumberPrefixes;}

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
    public List<PremiseNumberSuffix> getPremiseNumberSuffixes() {return this.premiseNumberSuffixes;}

}
