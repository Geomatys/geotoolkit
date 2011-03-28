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
public class DefaultLargeMailUserName implements LargeMailUserName{

    private String content;
    private String type;
    private String code;

    public DefaultLargeMailUserName(){}

    /**
     * 
     * @param type
     * @param code
     * @param content
     */
    public DefaultLargeMailUserName(String type, String code, String content){
        this.content = content;
        this.type = type;
        this.code = code;
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
    public String getType() {return this.type;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getCode() {return this.code;}

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
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setCode(String code) {
        this.code = code;
    }

}
