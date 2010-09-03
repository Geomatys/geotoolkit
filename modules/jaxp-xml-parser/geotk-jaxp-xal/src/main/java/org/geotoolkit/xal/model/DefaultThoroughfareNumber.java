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
public class DefaultThoroughfareNumber 
        extends DefaultGenericTypedGrPostal
        implements ThoroughfareNumber {

    private SingleRangeEnum numberType;
    private String indicator;
    private AfterBeforeEnum indicatorOccurrence;
    private AfterBeforeTypeNameEnum numberOccurrence;

    public DefaultThoroughfareNumber(){}
    
    /**
     *
     * @param numberType
     * @param type
     * @param indicator
     * @param indicatorOccurence
     * @param numberOccurrence
     * @param content
     */
    public DefaultThoroughfareNumber(SingleRangeEnum numberType,
            String type, String indicator, AfterBeforeEnum indicatorOccurence,
            AfterBeforeTypeNameEnum numberOccurrence, GrPostal grPostal, String content){
        super(type, grPostal, content);
        this.numberType = numberType;
        this.indicator = indicator;
        this.indicatorOccurrence = indicatorOccurence;
        this.numberOccurrence = numberOccurrence;
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
    public AfterBeforeEnum getIndicatorOccurence() {return this.indicatorOccurrence;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AfterBeforeTypeNameEnum getNumberOccurence() {return this.numberOccurrence;}

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
    public void setNumberOccurrence(AfterBeforeTypeNameEnum numberOccurrence) {
        this.numberOccurrence = numberOccurrence;
    }

}
