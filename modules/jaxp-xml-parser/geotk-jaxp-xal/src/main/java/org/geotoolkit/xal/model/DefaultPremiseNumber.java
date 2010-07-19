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
public class DefaultPremiseNumber implements PremiseNumber {

    private final SingleRangeEnum numberType;
    private final String type;
    private final String indicator;
    private final AfterBeforeEnum indicatorOccurrence;
    private final AfterBeforeEnum numberTypeOccurrence;
    private final GrPostal grPostal;
    private final String content;

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
        this.numberType = numberType;
        this.type = type;
        this.indicator = indicator;
        this.indicatorOccurrence = indicatorOccurrence;
        this.numberTypeOccurrence = numberTypeOccurrence;
        this.grPostal = grPostal;
        this.content = content;
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
    public String getContent() {return this.content;}

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

}
