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
public class DefaultThoroughfareNumber implements ThoroughfareNumber {

    private final SingleRangeEnum numberType;
    private final String type;
    private final String indicator;
    private final AfterBeforeEnum indicatorOccurence;
    private final AfterBeforeTypeNameEnum numberOccurrence;
    private final GrPostal grPostal;
    private final String content;

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
        this.numberType = numberType;
        this.type = type;
        this.indicator = indicator;
        this.indicatorOccurence = indicatorOccurence;
        this.numberOccurrence = numberOccurrence;
        this.grPostal = grPostal;
        this.content = content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getContent() {return this.content;}

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
    public String getType() {return this.type;}

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
    public AfterBeforeEnum getIndicatorOccurence() {return this.indicatorOccurence;}

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
    public GrPostal getGrPostal() {return this.grPostal;}

}
