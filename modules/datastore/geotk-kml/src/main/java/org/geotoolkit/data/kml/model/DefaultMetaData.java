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

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
@Deprecated
public class DefaultMetaData implements Metadata {

    private List<Object> content;

    /**
     *
     * @deprecated
     */
    @Deprecated
    public DefaultMetaData(){
        this.content = EMPTY_LIST;
    }

    /**
     * 
     * @param content
     * @deprecated
     */
    @Deprecated
    public DefaultMetaData(List<Object> content){
        this.content = (content == null) ? EMPTY_LIST : content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getContent() {
        return this.content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setContent(List<Object> content) {
        this.content = (content == null) ? EMPTY_LIST : content;
    }

}
