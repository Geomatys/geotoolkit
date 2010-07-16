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
 * @deprecated
 */
public class DefaultUrl extends DefaultLink implements Url {

    /**
     * 
     * @param link
     * @deprecated
     */
    @Deprecated
    public DefaultUrl(Link link){
        super(link.extensions().simples(Extensions.Names.OBJECT),
                link.getIdAttributes(), link.getHref(),
                link.extensions().simples(Extensions.Names.BASIC_LINK),
                link.extensions().complexes(Extensions.Names.BASIC_LINK),
                link.getRefreshMode(), link.getRefreshInterval(),
                link.getViewRefreshMode(), link.getViewRefreshTime(),
                link.getViewBoundScale(), link.getViewFormat(), link.getHttpQuery(),
                link.extensions().simples(Extensions.Names.LINK),
                link.extensions().complexes(Extensions.Names.LINK));
    }
}
