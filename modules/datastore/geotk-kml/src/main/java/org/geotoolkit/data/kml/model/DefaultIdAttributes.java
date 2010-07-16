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
package org.geotoolkit.data.kml.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultIdAttributes implements IdAttributes {

    private String id;
    private String targetId;

    /**
     * 
     */
    public DefaultIdAttributes() {
    }

    /**
     *
     * @param id
     * @param targetId
     */
    public DefaultIdAttributes(String id, String targetId) {
        this.id = id;
        this.targetId = targetId;
    }

    /**
     *
     * @{@inheritDoc}
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     *
     * @{@inheritDoc}
     */
    @Override
    public String getTargetId() {
        return this.targetId;
    }

    /**
     *
     * @{@inheritDoc}
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @{@inheritDoc}
     */
    @Override
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}
