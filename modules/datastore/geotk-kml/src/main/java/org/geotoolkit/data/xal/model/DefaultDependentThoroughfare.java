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
public class DefaultDependentThoroughfare implements DependentThoroughfare {

    private final List<GenericTypedGrPostal> addressLines;
    private final GenericTypedGrPostal thoroughfarePreDirection;
    private final GenericTypedGrPostal thoroughfareLeadingType;
    private final List<GenericTypedGrPostal> thoroughfareNames;
    private final GenericTypedGrPostal thoroughfareTrailingType;
    private final GenericTypedGrPostal thoroughfarePostDirection;
    private final String type;

    /**
     *
     * @param addressLines
     * @param thoroughfarePreDirection
     * @param thoroughfareLeadingType
     * @param thoroughfareNames
     * @param thoroughfareTrailingType
     * @param thoroughfarePostDirection
     * @param type
     */
    public DefaultDependentThoroughfare(List<GenericTypedGrPostal> addressLines,
            GenericTypedGrPostal thoroughfarePreDirection, GenericTypedGrPostal thoroughfareLeadingType,
            List<GenericTypedGrPostal> thoroughfareNames, GenericTypedGrPostal thoroughfareTrailingType,
            GenericTypedGrPostal thoroughfarePostDirection, String type){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.thoroughfarePreDirection = thoroughfarePreDirection;
        this.thoroughfareLeadingType = thoroughfareLeadingType;
        this.thoroughfareNames = (thoroughfareNames == null) ? EMPTY_LIST : thoroughfareNames;
        this.thoroughfareTrailingType = thoroughfareTrailingType;
        this.thoroughfarePostDirection = thoroughfarePostDirection;
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
    public String getType() {return this.type;}

}
