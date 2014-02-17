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
public class DefaultThoroughfareNumberRange implements ThoroughfareNumberRange {

    private List<GenericTypedGrPostal> addressLines;
    private ThoroughfareNumberFrom thoroughfareNumberFrom;
    private ThoroughfareNumberTo thoroughfareNumberTo;
    private OddEvenEnum rangeType;
    private String indicator;
    private String separator;
    private String type;
    private AfterBeforeEnum indicatorOccurrence;
    private AfterBeforeTypeNameEnum numberRangeOccurrence;
    private GrPostal grPostal;

    public DefaultThoroughfareNumberRange(){}

    /**
     *
     * @param addressLines
     * @param thoroughfareNumberFrom
     * @param thoroughfareNumberTo
     * @param rangeType
     * @param indicator
     * @param separator
     * @param type
     * @param indicatorOccurrence
     * @param numberRangeOccurrence
     */
    public DefaultThoroughfareNumberRange(List<GenericTypedGrPostal> addressLines,
            ThoroughfareNumberFrom thoroughfareNumberFrom,
            ThoroughfareNumberTo thoroughfareNumberTo,
            OddEvenEnum rangeType, String indicator, String separator, String type,
            AfterBeforeEnum indicatorOccurrence, AfterBeforeTypeNameEnum numberRangeOccurrence, GrPostal grPostal){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.thoroughfareNumberFrom = thoroughfareNumberFrom;
        this.thoroughfareNumberTo = thoroughfareNumberTo;
        this.rangeType = rangeType;
        this.indicator = indicator;
        this.separator = separator;
        this.type = type;
        this.indicatorOccurrence = indicatorOccurrence;
        this.numberRangeOccurrence = numberRangeOccurrence;
        this.grPostal = grPostal;
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
    public ThoroughfareNumberFrom getThoroughfareNumberFrom() {return this.thoroughfareNumberFrom;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ThoroughfareNumberTo getThoroughfareNumberTo() {return this.thoroughfareNumberTo;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public OddEvenEnum getRangeType() {return this.rangeType;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getIndicator() {return this.indicator;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getSeparator() {return this.separator;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AfterBeforeEnum getIndicatorOccurence() {return this.indicatorOccurrence;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AfterBeforeTypeNameEnum getNumberRangeOccurence() {return this.numberRangeOccurrence;}

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
    public GrPostal getGrPostal() {return this.grPostal;}

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
    public void setThoroughfareNumberFrom(ThoroughfareNumberFrom thoroughfareNumberFrom) {
        this.thoroughfareNumberFrom = thoroughfareNumberFrom;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setThoroughfareNumberTo(ThoroughfareNumberTo thoroughfareNumberTo) {
        this.thoroughfareNumberTo = thoroughfareNumberTo;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRangeType(OddEvenEnum rangeType) {
        this.rangeType = rangeType;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIndicatorOccurrence(AfterBeforeEnum indicatorOccurrence) {
        this.indicatorOccurrence = indicatorOccurrence;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setNumberRangeOccurrence(AfterBeforeTypeNameEnum numberRangeOccurrence) {
        this.numberRangeOccurrence = numberRangeOccurrence;
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
    public void setGrPostal(GrPostal grPostal) {
        this.grPostal = grPostal;
    }

}
