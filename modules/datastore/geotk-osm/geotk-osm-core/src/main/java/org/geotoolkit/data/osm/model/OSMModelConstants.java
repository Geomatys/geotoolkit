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

import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;

import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.FeatureTypeFactory;

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

    static final AttributeDescriptor DESC_USER;
    static final AttributeDescriptor DESC_TAG;
    static final AttributeDescriptor DESC_RELATION_MEMBER;
    static final AttributeDescriptor DESC_IDENTIFIED;
    static final AttributeDescriptor DESC_NODE;
    static final AttributeDescriptor DESC_WAY;
    static final AttributeDescriptor DESC_RELATION;

    static {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final FeatureTypeFactory ftf = ftb.getFeatureTypeFactory();

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
        ftb.add(new DefaultName(OSM_NAMESPACE, "nodes"),Long.class,0,Integer.MAX_VALUE,true,null);
        TYPE_WAY = ftb.buildFeatureType();

        //------------------- RELATION MEMBER TYPE -----------------------------
        ftb.reset();
        ftb.setName(OSM_NAMESPACE, "Member");
        ftb.add(new DefaultName(OSM_NAMESPACE, "role"), String.class);
        ftb.add(new DefaultName(OSM_NAMESPACE, "type"), MemberType.class);
        ftb.add(new DefaultName(OSM_NAMESPACE, "ref"),Long.class,1,1,false,null);
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

        DESC_USER = ftf.createAttributeDescriptor( TYPE_USER, TYPE_USER.getName(), 1, 1, true, null);
        DESC_TAG = ftf.createAttributeDescriptor( TYPE_TAG, TYPE_TAG.getName(), 1, 1, true, null);
        DESC_RELATION_MEMBER = ftf.createAttributeDescriptor( TYPE_RELATION_MEMBER, TYPE_RELATION_MEMBER.getName(), 1, 1, true, null);
        DESC_IDENTIFIED = ftf.createAttributeDescriptor( TYPE_IDENTIFIED, TYPE_IDENTIFIED.getName(), 1, 1, true, null);
        DESC_NODE = ftf.createAttributeDescriptor( TYPE_NODE, TYPE_NODE.getName(), 1, 1, true, null);
        DESC_WAY = ftf.createAttributeDescriptor( TYPE_WAY, TYPE_WAY.getName(), 1, 1, true, null);
        DESC_RELATION = ftf.createAttributeDescriptor( TYPE_RELATION, TYPE_RELATION.getName(), 1, 1, true, null);

    }

    private OSMModelConstants(){}

}
