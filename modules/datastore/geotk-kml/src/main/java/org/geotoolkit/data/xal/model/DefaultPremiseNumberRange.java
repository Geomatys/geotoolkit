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

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPremiseNumberRange implements PremiseNumberRange {

    private final PremiseNumberRangeFrom premiseNumberRangeFrom;
    private final PremiseNumberRangeTo premiseNumberRangeTo;
    private final String rangeType;
    private final String indicator;
    private final String separator;
    private final String type;
    private final AfterBeforeEnum indicatorOccurrence;
    private final AfterBeforeTypeNameEnum numberRangeOccurrence;

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

}
