/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.data.osm.client.v060;

import org.apache.sis.internal.feature.AttributeConvention;
import org.geotoolkit.data.osm.client.AbstractChangeElement;
import org.geotoolkit.data.osm.client.OpenStreetMapClient;
import org.geotoolkit.data.osm.model.OSMModelConstants;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ChangeElement060 extends AbstractChangeElement{

    public ChangeElement060(final OpenStreetMapClient server, final Type type){
        super(server,"",type);
    }

    @Override
    protected String getSubPath() {
        final StringBuilder sb = new StringBuilder("/api/0.6/");
        if(element.equals(OSMModelConstants.TYPE_NODE)){
            sb.append("node");
        }else if(element.equals(OSMModelConstants.TYPE_WAY)){
            sb.append("way");
        }else if(element.equals(OSMModelConstants.TYPE_RELATION)){
            sb.append("relation");
        }else{
            throw new IllegalArgumentException("Unexpected type (allowed types are Node/Way/Relation) : " + element);
        }

        switch(type){
            case CREATE : sb.append("/create");break;
            case UPDATE : sb.append('/').append(element.getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()));break;
            case DELETE : sb.append('/').append(element.getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()));break;
        }

        return sb.toString();
    }



}
