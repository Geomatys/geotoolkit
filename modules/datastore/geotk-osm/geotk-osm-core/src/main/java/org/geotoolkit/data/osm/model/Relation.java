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

import org.geotoolkit.feature.DefaultProperty;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

import org.opengis.feature.Property;
import org.opengis.feature.type.PropertyDescriptor;

/**
 * OSM Relation, Open Street Map relations can define "anything".
 * A building can be expressed by a relation with the border as a Way
 * and nodes for entrance and emergency exits.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Relation extends IdentifiedElement{

    private final List<Member> members;

    public Relation(List<Member> members,
            long id, int version, int changeset, User user,
            long timestamp, Map<String,String> tags) {
        super(OSMModelConstants.DESC_RELATION,id,version,changeset,user,timestamp,tags);

        if(members == null || members.isEmpty()){
            this.members = Collections.EMPTY_LIST;
        }else{
            this.members = UnmodifiableArrayList.wrap(members.toArray(new Member[members.size()]));
        }
    }

    /**
     * @return List of this relation member, those are ordered.
     */
    public List<Member> getMembers() {
        return members;
    }

    @Override
    public Collection<Property> getProperties() {
        final Collection<Property> props = new ArrayList<Property>();
        props.add(new DefaultProperty(id, getType().getDescriptor("id")));
        props.add(new DefaultProperty(version, getType().getDescriptor("version")));
        props.add(new DefaultProperty(changeset, getType().getDescriptor("changeset")));
        props.add(new DefaultProperty(user, getType().getDescriptor("user")));
        props.add(new DefaultProperty(timestamp, getType().getDescriptor("timestamp")));

        final PropertyDescriptor tagDesc = getType().getDescriptor("tags");
        for(final Tag t : tags){
            props.add(new DefaultProperty(t, tagDesc));
        }

        final PropertyDescriptor memDesc = getType().getDescriptor("members");
        for(final Member m : members){
            props.add(new DefaultProperty(m, memDesc));
        }

        return props;
    }

    @Override
    public String toString() {
        final StringWriter writer = new StringWriter();
        final TableWriter tablewriter = new TableWriter(writer);

        tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        tablewriter.write("OSM-RELATION\t \n");
        tablewriter.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        tablewriter.write("members count\t"+members.size()+"\n");
        for(int i=0;i<members.size();i++){
            tablewriter.write("member " +i +"\t"+members.get(i)+"\n");
        }
        tablewriter.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        tablewriter.write("ID\t"+getId()+"\n");
        tablewriter.write("ChangeSet\t"+getChangeset()+"\n");
        tablewriter.write("User\t"+getUser()+"\n");
        tablewriter.write("TimeStamp\t"+new Date(getTimestamp())+"\n");
        tablewriter.write("version\t"+getVersion()+"\n");
        tablewriter.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        for(Tag t : getTags()){
            tablewriter.write(t.getK()+"\t"+t.getV()+"\n");
        }
        tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);

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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Relation other = (Relation) obj;
        if (this.members != other.members && (this.members == null || !this.members.equals(other.members))) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.members != null ? this.members.hashCode() : 0);
        return hash + super.hashCode();
    }

}
