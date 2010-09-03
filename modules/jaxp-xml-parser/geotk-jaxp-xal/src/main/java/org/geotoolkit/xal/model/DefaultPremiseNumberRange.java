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

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPremiseNumberRange implements PremiseNumberRange {

    private PremiseNumberRangeFrom premiseNumberRangeFrom;
    private PremiseNumberRangeTo premiseNumberRangeTo;
    private String rangeType;
    private String indicator;
    private String separator;
    private String type;
    private AfterBeforeEnum indicatorOccurrence;
    private AfterBeforeTypeNameEnum numberRangeOccurrence;

    public DefaultPremiseNumberRange(){}

    /**
     *
     * @param premiseNumberRangeFrom
     * @param premiseNumberRangeTo
     * @param rangeType
     * @param indicator
     * @param separator
     * @param type
     * @param indicatorOccurrence
     * @param numberRangeOccurrence
     */
    public DefaultPremiseNumberRange(PremiseNumberRangeFrom premiseNumberRangeFrom,
            PremiseNumberRangeTo premiseNumberRangeTo, String rangeType,
            String indicator, String separator, String type,
            AfterBeforeEnum indicatorOccurrence, AfterBeforeTypeNameEnum numberRangeOccurrence){
        this.premiseNumberRangeFrom = premiseNumberRangeFrom;
        this.premiseNumberRangeTo = premiseNumberRangeTo;
        this.rangeType = rangeType;
        this.indicator = indicator;
        this.separator = separator;
        this.type = type;
        this.indicatorOccurrence = indicatorOccurrence;
        this.numberRangeOccurrence = numberRangeOccurrence;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseNumberRangeFrom getPremiseNumberRangeFrom() {return premiseNumberRangeFrom;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseNumberRangeTo getPremiseNumberRangeTo() {return premiseNumberRangeTo;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getRangeType() {return this.rangeType;}

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
    public String getType() {return this.type;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AfterBeforeEnum getIndicatorOccurrence() {return this.indicatorOccurrence;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AfterBeforeTypeNameEnum getNumberRangeOccurrence() {return this.numberRangeOccurrence;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPremiseNumberRangeFrom(PremiseNumberRangeFrom premiseNumberRangeFrom) {
        this.premiseNumberRangeFrom = premiseNumberRangeFrom;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPremiseNumberRangeTo(PremiseNumberRangeTo premiseNumberRangeTo) {
        this.premiseNumberRangeTo = premiseNumberRangeTo;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRangeType(String rangeType) {
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
    public void getSeparator(String separator) {
        this.separator = separator;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void getType(String type) {
        this.type = type;
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

}
