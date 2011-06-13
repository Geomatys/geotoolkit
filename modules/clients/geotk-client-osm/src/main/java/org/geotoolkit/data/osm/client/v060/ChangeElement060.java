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

import org.geotoolkit.data.osm.client.AbstractChangeElement;
import org.geotoolkit.data.osm.client.OpenStreetMapServer;
import org.geotoolkit.data.osm.model.Node;
import org.geotoolkit.data.osm.model.Relation;
import org.geotoolkit.data.osm.model.Way;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ChangeElement060 extends AbstractChangeElement{

    public ChangeElement060(final OpenStreetMapServer server, final Type type){
        super(server,"",type);
    }

    @Override
    protected String getSubPath() {
        final StringBuilder sb = new StringBuilder("/api/0.6/");
        if(element instanceof Node){
            sb.append("node");
        }else if(element instanceof Way){
            sb.append("way");
        }else if(element instanceof Relation){
            sb.append("relation");
        }else{
            throw new IllegalArgumentException("Unexpected type (allowed types are Node/Way/Relation) : " + element);
        }

        switch(type){
            case CREATE : sb.append("/create");break;
            case UPDATE : sb.append('/').append(element.getId());break;
            case DELETE : sb.append('/').append(element.getId());break;
        }

        return sb.toString();
    }



}
