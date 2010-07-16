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
public class DefaultPostBoxNumberSuffix implements PostBoxNumberSuffix {

    private final String content;
    private final String numberSuffixSeparator;
    private final GrPostal grPostal;

    /**
     *
     * @param numberPrefixSeparator
     * @param grPostal
     * @param content
     */
    public DefaultPostBoxNumberSuffix(String numberSuffixSeparator, GrPostal grPostal, String content){
        this.content = content;
        this.numberSuffixSeparator = numberSuffixSeparator;
        this.grPostal = grPostal;
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
    public String getNumberSuffixSeparator() {return this.numberSuffixSeparator;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}

}
