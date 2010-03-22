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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Way extends IdentifiedElement{

    //todo, not pretty compact
    private final List<Long> nodes;

    public Way(List<Long> nodes,
            long id, int version, int changeset, User user,
            long timestamp, Map<String,String> tags) {
        super(id,version,changeset,user,timestamp,tags);

        if(nodes == null || nodes.isEmpty()){
            this.nodes = Collections.EMPTY_LIST;
        }else{
            this.nodes = UnmodifiableArrayList.wrap(nodes.toArray(new Long[nodes.size()]));
        }
    }

    public List<Long> getNodesIds() {
        return nodes;
    }

    @Override
    public String toString() {
        final StringWriter writer = new StringWriter();
        final TableWriter tablewriter = new TableWriter(writer);

        tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        tablewriter.write("OSM-WAY\t \n");
        tablewriter.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        tablewriter.write("nodes count\t"+nodes.size()+"\n");
        for(int i=0;i<nodes.size();i++){
            tablewriter.write("node " +i +"\t"+nodes.get(i)+"\n");
        }
        tablewriter.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        tablewriter.write("ID\t"+getId()+"\n");
        tablewriter.write("ChangeSet\t"+getChangeset()+"\n");
        tablewriter.write("User\t"+getUser()+"\n");
        tablewriter.write("TimeStamp\t"+getTimestamp()+"\n");
        tablewriter.write("version\t"+getVersion()+"\n");
        tablewriter.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        for(Entry<String,String> entry : getTags().entrySet()){
            tablewriter.write(entry.getKey()+"\t"+entry.getValue()+"\n");
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

}
