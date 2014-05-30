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

package org.geotoolkit.data.osm.model;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.io.TableAppender;

import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.AttributeDescriptor;

import static org.geotoolkit.data.osm.model.OSMModelConstants.*;

/**
 * OSM way element. a Way is composed of nodes and can represent "anything"
 * exemple : a road, a building, city limits, ... the type can be defined by
 * analyzing the different tags of the way.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Way extends IdentifiedElement{

    //todo, not pretty compact
    private final List<Long> nodes;

    public Way(final List<Long> nodes,
            final long id, final int version, final int changeset, final User user,
            final long timestamp, final Map<String,String> tags) {
        super(OSMModelConstants.DESC_WAY,id,version,changeset,user,timestamp,tags);

        if(nodes == null || nodes.isEmpty()){
            this.nodes = Collections.EMPTY_LIST;
        }else{
            this.nodes = UnmodifiableArrayList.wrap(nodes.toArray(new Long[nodes.size()]));
        }
    }

    /**
     * @return Ordered List of node ids that compose this way.
     */
    public List<Long> getNodesIds() {
        return nodes;
    }

    @Override
    public Collection<Property> getProperties() {
        final Collection<Property> props = new ArrayList<Property>();
        props.add(FF.createAttribute(id,            ATT_ID,null));
        props.add(FF.createAttribute(version,       ATT_VERSION,null));
        props.add(FF.createAttribute(changeset,     ATT_CHANGESET,null));
        props.add(FF.createAttribute(user,          ATT_USER,null));
        props.add(FF.createAttribute(timestamp,     ATT_TIMESTAMP,null));
        props.addAll(tags);

        final AttributeDescriptor nodeDesc = ATT_WAY_NODES;
        for(final Long l : nodes){
            props.add(FF.createAttribute(l, nodeDesc, null));
        }
        return props;
    }

    @Override
    public String toString() {
        final StringWriter writer = new StringWriter();
        final TableAppender tablewriter = new TableAppender(writer);

        tablewriter.appendHorizontalSeparator();
        tablewriter.append("OSM-WAY\t \n");
        tablewriter.appendHorizontalSeparator();
        tablewriter.append("nodes count\t"+nodes.size()+"\n");
        for(int i=0;i<nodes.size();i++){
            tablewriter.append("node " +i +"\t"+nodes.get(i)+"\n");
        }
        tablewriter.appendHorizontalSeparator();
        tablewriter.append("ID\t"+getId()+"\n");
        tablewriter.append("ChangeSet\t"+getChangeset()+"\n");
        tablewriter.append("User\t"+getUser()+"\n");
        tablewriter.append("TimeStamp\t"+new Date(getTimestamp())+"\n");
        tablewriter.append("version\t"+getVersion()+"\n");
        tablewriter.appendHorizontalSeparator();
        for(Tag t : getTags()){
            tablewriter.append(t.getK()+"\t"+t.getV()+"\n");
        }
        tablewriter.appendHorizontalSeparator();

        try {
            tablewriter.flush();
            writer.flush();
        } catch (IOException ex) {
            //will never happen is this case
            ex.printStackTrace();
        }

        return writer.getBuffer().toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.nodes != null ? this.nodes.hashCode() : 0);
        return hash + super.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Way other = (Way) obj;
        if (this.nodes != other.nodes && (this.nodes == null || !this.nodes.equals(other.nodes))) {
            return false;
        }
        return super.equals(obj);
    }

}
