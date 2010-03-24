/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.data.osm.model;

import java.util.ArrayList;
import java.util.Collection;

import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.type.DefaultFeatureTypeFactory;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.feature.type.ComplexType;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.feature.type.PropertyDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class OSMModelConstants {

    public static final String OSM_NAMESPACE = "http://openstreetmap.org";

    public static final ComplexType TYPE_USER;
    public static final ComplexType TYPE_TAG;
    public static final ComplexType TYPE_RELATION_MEMBER;
    
    public static final FeatureType TYPE_IDENTIFIED;
    public static final FeatureType TYPE_NODE;
    public static final FeatureType TYPE_WAY;
    public static final FeatureType TYPE_RELATION;

    static {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();


        //------------------- USER TYPE ----------------------------------------
        ftb.reset();
        ftb.setName(OSM_NAMESPACE, "User");
        ftb.add(new DefaultName(OSM_NAMESPACE, "id"), Integer.class);
        ftb.add(new DefaultName(OSM_NAMESPACE, "name"), String.class);
        TYPE_USER = ftb.buildType();

        //------------------- TAG TYPE -----------------------------------------
        ftb.reset();
        ftb.setName(OSM_NAMESPACE, "Tag");
        ftb.add(new DefaultName(OSM_NAMESPACE, "k"), String.class);
        ftb.add(new DefaultName(OSM_NAMESPACE, "v"), String.class);
        TYPE_TAG = ftb.buildType();

        //------------------- IDENTIFIED TYPE ----------------------------------
        ftb.reset();
        ftb.setName(OSM_NAMESPACE, "Identified");
        ftb.add(new DefaultName(OSM_NAMESPACE, "id"), Long.class);
        ftb.add(new DefaultName(OSM_NAMESPACE, "version"), Integer.class);
        ftb.add(new DefaultName(OSM_NAMESPACE, "changeset"), Integer.class);
        ftb.add(TYPE_USER,new DefaultName(OSM_NAMESPACE, "user"),null,1,1,false,null);
        ftb.add(new DefaultName(OSM_NAMESPACE, "timestamp"), Integer.class);
        ftb.add(TYPE_TAG,new DefaultName(OSM_NAMESPACE, "tags"),null,0,Integer.MAX_VALUE,true,null);
        TYPE_IDENTIFIED = ftb.buildFeatureType();

        //------------------- NODE TYPE ----------------------------------------
        ftb.reset();
        ftb.setSuperType(TYPE_IDENTIFIED);
        ftb.setName(OSM_NAMESPACE, "Node");
        ftb.add(new DefaultName(OSM_NAMESPACE, "id"), Long.class);
        ftb.add(new DefaultName(OSM_NAMESPACE, "version"), Integer.class);
        ftb.add(new DefaultName(OSM_NAMESPACE, "changeset"), Integer.class);
        ftb.add(TYPE_USER,new DefaultName(OSM_NAMESPACE, "user"),null,1,1,false,null);
        ftb.add(new DefaultName(OSM_NAMESPACE, "timestamp"), Integer.class);
        ftb.add(TYPE_TAG,new DefaultName(OSM_NAMESPACE, "tags"),null,0,Integer.MAX_VALUE,true,null);
        TYPE_NODE = ftb.buildFeatureType();
        

        //------------------- WAY TYPE -----------------------------------------
        ftb.reset();
        ftb.setSuperType(TYPE_IDENTIFIED);
        ftb.setName(OSM_NAMESPACE, "Way");
        ftb.add(new DefaultName(OSM_NAMESPACE, "id"), Long.class);
        ftb.add(new DefaultName(OSM_NAMESPACE, "version"), Integer.class);
        ftb.add(new DefaultName(OSM_NAMESPACE, "changeset"), Integer.class);
        ftb.add(TYPE_USER,new DefaultName(OSM_NAMESPACE, "user"),null,1,1,false,null);
        ftb.add(new DefaultName(OSM_NAMESPACE, "timestamp"), Integer.class);
        ftb.add(TYPE_TAG,new DefaultName(OSM_NAMESPACE, "tags"),null,0,Integer.MAX_VALUE,true,null);
        ftb.add(TYPE_NODE,new DefaultName(OSM_NAMESPACE, "nodes"),null,0,Integer.MAX_VALUE,true,null);
        TYPE_WAY = ftb.buildFeatureType();

        //------------------- RELATION MEMBER TYPE -----------------------------
        ftb.reset();
        ftb.setName(OSM_NAMESPACE, "Member");
        ftb.add(new DefaultName(OSM_NAMESPACE, "role"), String.class);
        ftb.add(TYPE_IDENTIFIED,new DefaultName(OSM_NAMESPACE, "ref"),null,1,1,false,null);
        TYPE_RELATION_MEMBER = ftb.buildType();

        //------------------- RELATION TYPE ------------------------------------
        ftb.reset();
        ftb.setSuperType(TYPE_IDENTIFIED);
        ftb.setName(OSM_NAMESPACE, "Relation");
        ftb.add(new DefaultName(OSM_NAMESPACE, "id"), Long.class);
        ftb.add(new DefaultName(OSM_NAMESPACE, "version"), Integer.class);
        ftb.add(new DefaultName(OSM_NAMESPACE, "changeset"), Integer.class);
        ftb.add(TYPE_USER,new DefaultName(OSM_NAMESPACE, "user"),null,1,1,false,null);
        ftb.add(new DefaultName(OSM_NAMESPACE, "timestamp"), Integer.class);
        ftb.add(TYPE_TAG,new DefaultName(OSM_NAMESPACE, "tags"),null,0,Integer.MAX_VALUE,true,null);
        ftb.add(TYPE_RELATION_MEMBER,new DefaultName(OSM_NAMESPACE, "members"),null,0,Integer.MAX_VALUE,true,null);
        TYPE_RELATION = ftb.buildFeatureType();
    }

    private OSMModelConstants(){}

}
