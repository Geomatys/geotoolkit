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

import java.net.URI;
import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultUpdate implements Update {

    private URI targetHref;
    private List<Object> updates;
    private List<Object> updateOpExtensions;
    private List<Object> updateExtensions;

    public DefaultUpdate(){
        this.updates = EMPTY_LIST;
        this.updateOpExtensions = EMPTY_LIST;
        this.updateExtensions = EMPTY_LIST;
    }

    /**
     * 
     * @param targetHref
     * @param updates
     * @param updateOpExtensions
     * @param updateExtensions
     */
    public DefaultUpdate(URI targetHref, List<Object> updates,
            List<Object> updateOpExtensions, List<Object> updateExtensions) {
        this.targetHref = targetHref;
        this.updates = (updates == null) ? EMPTY_LIST : updates;
        this.updateOpExtensions = (updateOpExtensions == null) ? EMPTY_LIST : updateOpExtensions;
        this.updateExtensions = (updateExtensions == null) ? EMPTY_LIST : updateExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public URI getTargetHref() {
        return this.targetHref;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getUpdates() {
        return this.updates;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getUpdateOpExtensions() {
        return this.updateOpExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getUpdateExtensions() {
        return this.updateExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTargetHref(URI targetHref) {
        this.targetHref = targetHref;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setUpdates(List<Object> updates) {
        this.updates = (updates == null) ? EMPTY_LIST : updates;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setUpdateOpExtensions(List<Object> updateOpEXtensions) {
        this.updateOpExtensions = (updateOpExtensions == null) ? EMPTY_LIST : updateOpExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setUpdateExtensions(List<Object> updateExtensions) {
        this.updateExtensions = (updateExtensions == null) ? EMPTY_LIST : updateExtensions;
    }

}
