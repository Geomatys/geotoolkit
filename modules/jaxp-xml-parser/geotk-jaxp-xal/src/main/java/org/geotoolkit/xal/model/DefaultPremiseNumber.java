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
public class DefaultPremiseNumber extends DefaultGenericTypedGrPostal implements PremiseNumber {

    private SingleRangeEnum numberType;
    private String indicator;
    private AfterBeforeEnum indicatorOccurrence;
    private AfterBeforeEnum numberTypeOccurrence;

    public DefaultPremiseNumber(){}
    
    /**
     *
     * @param numberType
     * @param type
     * @param indicator
     * @param indicatorOccurrence
     * @param numberTypeOccurrence
     * @param grPostal
     * @param content
     */
    public DefaultPremiseNumber(SingleRangeEnum numberType, String type, String indicator,
            AfterBeforeEnum indicatorOccurrence, AfterBeforeEnum numberTypeOccurrence,
            GrPostal grPostal, String content){
        super(type, grPostal, content);
        this.numberType = numberType;
        this.indicator = indicator;
        this.indicatorOccurrence = indicatorOccurrence;
        this.numberTypeOccurrence = numberTypeOccurrence;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SingleRangeEnum getNumberType() {return this.numberType;}

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
    public AfterBeforeEnum getIndicatorOccurrence() {return this.indicatorOccurrence;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AfterBeforeEnum getNumberTypeOccurrence() {return this.numberTypeOccurrence;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setNumberType(SingleRangeEnum numberType) {
        this.numberType = numberType;
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
    public void setIndicatorOccurrence(AfterBeforeEnum indicatorOccurrence) {
        this.indicatorOccurrence = indicatorOccurrence;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setNumberTypeOccurrence(AfterBeforeEnum numberTypeOccurrence) {
        this.numberTypeOccurrence = numberTypeOccurrence;
    }

}
