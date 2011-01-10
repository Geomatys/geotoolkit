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

import org.geotoolkit.data.osm.client.AbstractReadElements;
import org.geotoolkit.data.osm.model.Node;
import org.geotoolkit.data.osm.model.Relation;
import org.geotoolkit.data.osm.model.Way;
import org.geotoolkit.util.StringUtilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ReadElements060 extends AbstractReadElements{

    public ReadElements060(final String serveruURL){
        super(serveruURL,"/api/0.6/");
    }

    @Override
    protected String getSubPath() {

        final String strType;
        if(Node.class.equals(type)){
            strType = "nodes";
        }else if(Way.class.equals(type)){
            strType = "ways";
        }else if(Relation.class.equals(type)){
            strType = "relations";
        }else{
            throw new IllegalArgumentException("Type expected can be : Node,Way,Relation, found = " + type);
        }

        return super.getSubPath() + strType;
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        if(ids == null || ids.isEmpty()){
            throw new IllegalArgumentException("ids has not been defined or is empty");
        }
        requestParameters.clear();

        final String strType;
        if(Node.class.equals(type)){
            strType = "nodes";
        }else if(Way.class.equals(type)){
            strType = "ways";
        }else if(Relation.class.equals(type)){
            strType = "relations";
        }else{
            throw new IllegalArgumentException("Type expected can be : Node,Way,Relation, found = " + type);
        }

        final String csv = StringUtilities.toCommaSeparatedValues(ids);
        requestParameters.put(strType, csv);
    }

}
