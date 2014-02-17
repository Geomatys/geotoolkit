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
 * @module pending
 */
public class DefaultCountryNameCode implements CountryNameCode {

    private String content;
    private String scheme;
    private GrPostal grPostal;

    public DefaultCountryNameCode(){}

    /**
     *
     * @param sheme
     * @param grPostal
     * @param content
     */
    public DefaultCountryNameCode(String sheme, GrPostal grPostal, String content){
        this.scheme = sheme;
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
    public String getScheme() {return this.scheme;}

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
    public void setContent(String content) {
        this.content = content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setScheme(String scheme) {
        this.scheme = scheme;
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
