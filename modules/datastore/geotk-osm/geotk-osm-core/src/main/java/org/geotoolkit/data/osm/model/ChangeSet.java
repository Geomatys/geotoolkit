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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.geotoolkit.io.TableWriter;

import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.opengis.geometry.Envelope;

/**
 * OSM Changeset object.
 *
 * @author Johann sorel (Geomatys)
 * @module pending
 */
public class ChangeSet {

    private final Integer id;
    private final User user;
    private final Long timestamp;
    private final Boolean open;
    private final Envelope env;
    private final List<Tag> tags;

    public ChangeSet(Integer id, User user, Long timestamp, Boolean open, Envelope env, Map<String,String> tags) {
        this.id = id;
        this.user = (user == null) ? User.NONE : user;
        this.timestamp = timestamp;
        this.open = open;
        this.env = env;

        if(tags == null || tags.isEmpty()){
            this.tags = Collections.EMPTY_LIST;
        }else{
            final Tag[] array = new Tag[tags.size()];
            int i=0;
            for(Map.Entry<String,String> entry : tags.entrySet()){
                array[i] = new Tag(entry.getKey(), entry.getValue());
                i++;
            }
            this.tags = UnmodifiableArrayList.wrap(array);
        }
    }

    public Integer getId() {
        return id;
    }

    public Boolean getOpen() {
        return open;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public User getUser() {
        return user;
    }

    public Envelope getEnv() {
        return env;
    }

    /**
     * Get all tags related to this osm element.
     * @return List of tags, never null but can be empty.
     */
    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        final StringWriter writer = new StringWriter();
        final TableWriter tablewriter = new TableWriter(writer);

        tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        tablewriter.write("OSM-CHANGESET\t \n");
        tablewriter.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        tablewriter.write("ID\t"+getId()+"\n");
        tablewriter.write("User\t"+getUser()+"\n");
        tablewriter.write("Open\t"+getOpen()+"\n");
        tablewriter.write("Created at\t"+new Date(getTimestamp())+"\n");
        tablewriter.write("Envelope\t"+getEnv()+"\n");
        tablewriter.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        for(Tag t : getTags()){
            tablewriter.write(t.getK()+"\t"+t.getV()+"\n");
        }
        tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);

        try {
            tablewriter.flush();
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
        final ChangeSet other = (ChangeSet) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.user != other.user && (this.user == null || !this.user.equals(other.user))) {
            return false;
        }
        if (this.timestamp != other.timestamp && (this.timestamp == null || !this.timestamp.equals(other.timestamp))) {
            return false;
        }
        if (this.open != other.open && (this.open == null || !this.open.equals(other.open))) {
            return false;
        }
        if (this.env != other.env && (this.env == null || !this.env.equals(other.env))) {
            return false;
        }
        if (this.tags != other.tags && (this.tags == null || !this.tags.equals(other.tags))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 19 * hash + (this.user != null ? this.user.hashCode() : 0);
        hash = 19 * hash + (this.timestamp != null ? this.timestamp.hashCode() : 0);
        hash = 19 * hash + (this.open != null ? this.open.hashCode() : 0);
        hash = 19 * hash + (this.env != null ? this.env.hashCode() : 0);
        return hash;
    }

}
