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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.geotoolkit.io.TableWriter;

import org.opengis.feature.Property;

import static org.geotoolkit.data.osm.model.OSMModelConstants.*;

/**
 * OSM Node, nodes are the base element that compose ways and relations.
 * Nodes are defined in 2D Lat/Long coordinates (in degrees).
 * The corresponding Coordinate Reference System is EPSG:4326 or CRS:84 depending
 * if you place latitude (EPSG:4326) or longitude (CRS:84) first.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Node extends IdentifiedElement{

    private static final GeometryFactory GF = new GeometryFactory();

    private final double lat;
    private final double lon;

    public Node(final double lat, final double lon,
            final long id, final int version, final int changeset, final User user,
            final long timestamp, final Map<String,String> tags) {
        super(OSMModelConstants.DESC_NODE,id,version,changeset,user,timestamp,tags);
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * @return double : latitude in degree of this node
     */
    public double getLatitude() {
        return lat;
    }

    /**
     * @return double : longitude in degree of this node
     */
    public double getLongitude() {
        return lon;
    }

    // feature/attribut model --------------------------------------------------

    @Override
    public Collection<Property> getValue() {
        final Collection<Property> props = new ArrayList<Property>();
        props.add(FF.createAttribute(id,            ATT_ID, null));
        props.add(FF.createAttribute(version,       ATT_VERSION, null));
        props.add(FF.createAttribute(changeset,     ATT_CHANGESET, null));
        props.add(FF.createAttribute(user,          ATT_USER, null));
        props.add(FF.createAttribute(timestamp,     ATT_TIMESTAMP, null));
        props.add(FF.createGeometryAttribute(GF.createPoint(new Coordinate(lon, lat)), ATT_NODE_POINT, null, null));
        props.addAll(tags);        
        return props;
    }

    @Override
    public String toString() {
        final StringWriter writer = new StringWriter();
        final TableWriter tablewriter = new TableWriter(writer);

        tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        tablewriter.write("OSM-NODE\t \n");
        tablewriter.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        tablewriter.write("lat\t"+getLatitude()+"\n");
        tablewriter.write("lon\t"+getLongitude()+"\n");
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
        } catch (IOException ex) {
            //will never happen is this case
            ex.printStackTrace();
        }

        return writer.getBuffer().toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (this.lat != other.lat) {
            return false;
        }
        if (this.lon != other.lon) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
        return hash + super.hashCode();
    }
    
}
