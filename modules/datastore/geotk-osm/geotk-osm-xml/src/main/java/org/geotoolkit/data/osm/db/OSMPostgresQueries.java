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

package org.geotoolkit.data.osm.db;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMPostgresQueries {

    private OSMPostgresQueries(){}

    ////////////////////////////////////////////////////////////////////////////
    // NODE TABLE AND TAG //////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static final String DROP_NODE_TABLE =
            "DROP TABLE IF EXISTS \"Node\"";
    public static final String DROP_NODE_TAG_TABLE =
            "DROP TABLE IF EXISTS \"NodeTag\"";

    public static final String CREATE_NODE_TABLE = 
            "CREATE TABLE \"Node\" (" +
            "id bigint NOT NULL," +
            "version integer NOT NULL," +
            "\"userId\" integer NOT NULL," +
            "timestamp timestamp without time zone NOT NULL," +
            "changeset bigint NOT NULL," +
            "lat double precision NOT NULL," +
            "lon double precision NOT NULL" +
            ")";
    public static final String CREATE_NODE_TAG_TABLE =
            "CREATE TABLE \"NodeTag\"(" +
            "\"nodeId\" bigint NOT NULL," +
            "k text NOT NULL," +
            "v text" +
            ")";

    public static final String CREATE_NODE_PK =
            "ALTER TABLE \"Node\" ADD PRIMARY KEY (id)";
    public static final String CREATE_NODE_FK =
            "ALTER TABLE \"Node\" ADD FOREIGN KEY (\"userId\") REFERENCES \"User\"(id)";

    public static final String CREATE_NODE_TAG_PK =
            "ALTER TABLE \"NodeTag\" ADD PRIMARY KEY (\"nodeId\",k)";
    public static final String CREATE_NODE_TAG_FK =
            "ALTER TABLE \"NodeTag\" ADD FOREIGN KEY (\"nodeId\") REFERENCES \"Node\"(id)";
    
    public static final String DELETE_NODE_DUPLICATE =
            "DELETE FROM \"Node\" " +
            "WHERE ctid IN " +
            "(SELECT MIN(ctid) " +
            "FROM \"Node\" " +
            "GROUP BY id " +
            "HAVING count(id) > 1)";

    public static final String DELETE_NODE_TAG_DUPLICATE =
            "DELETE FROM \"NodeTag\" " +
            "WHERE ctid IN " +
            "(SELECT MIN(ctid) " +
            "FROM \"NodeTag\" " +
            "GROUP BY \"nodeId\",k " +
            "HAVING count(\"nodeId\") > 1)";


    ////////////////////////////////////////////////////////////////////////////
    // WAY TABLE AND TAG ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static final String DROP_WAY_TABLE =
            "DROP TABLE IF EXISTS \"Way\"";
    public static final String DROP_WAY_MEMBER_TABLE =
            "DROP TABLE IF EXISTS \"WayMember\"";
    public static final String DROP_WAY_TAG_TABLE =
            "DROP TABLE IF EXISTS \"WayTag\"";

    public static final String CREATE_WAY_TABLE =
            "CREATE TABLE \"Way\"(" +
            "id bigint NOT NULL," +
            "version integer NOT NULL," +
            "\"userId\" integer NOT NULL," +
            "timestamp timestamp without time zone NOT NULL," +
            "changeset bigint NOT NULL" +
            ")";
    public static final String CREATE_WAY_MEMBER_TABLE =
            "CREATE TABLE \"WayMember\"(" +
            "\"wayId\" bigint NOT NULL," +
            "\"nodeId\" bigint NOT NULL," +
            "index integer NOT NULL" +
            ")";
    public static final String CREATE_WAY_TAG_TABLE = 
            "CREATE TABLE \"WayTag\"(" +
            "\"wayId\" bigint NOT NULL," +
            "k text NOT NULL," +
            "v text" +
            ")";

    public static final String CREATE_WAY_PK =
            "ALTER TABLE \"Way\" ADD PRIMARY KEY (id)";
    public static final String CREATE_WAY_FK =
            "ALTER TABLE \"Way\" ADD FOREIGN KEY (\"userId\") REFERENCES \"User\"(id)";

    public static final String CREATE_WAY_MEMBER_PK =
            "ALTER TABLE \"WayMember\" ADD PRIMARY KEY (\"wayId\",index)";
    public static final String CREATE_WAY_MEMBER_FK1 =
            "ALTER TABLE \"WayMember\" ADD FOREIGN KEY (\"wayId\") REFERENCES \"Way\"(id)";
    public static final String CREATE_WAY_MEMBER_FK2 =
            "ALTER TABLE \"WayMember\" ADD FOREIGN KEY (\"nodeId\") REFERENCES \"Node\"(id)";
    
    public static final String CREATE_WAY_TAG_PK =
            "ALTER TABLE \"WayTag\" ADD PRIMARY KEY (\"wayId\",k)";
    public static final String CREATE_WAY_TAG_FK =
            "ALTER TABLE \"WayTag\" ADD FOREIGN KEY (\"wayId\") REFERENCES \"Way\"(id)";

    public static final String DELETE_WAY_DUPLICATE =
            "DELETE FROM \"Way\" " +
            "WHERE ctid IN " +
            "(SELECT MIN(ctid) " +
            "FROM \"Way\" " +
            "GROUP BY id " +
            "HAVING count(id) > 1)";
    public static final String DELETE_WAY_TAG_DUPLICATE =
            "DELETE FROM \"WayTag\" " +
            "WHERE ctid IN " +
            "(SELECT MIN(ctid) " +
            "FROM \"WayTag\" " +
            "GROUP BY \"wayId\",k " +
            "HAVING count(\"wayId\") > 1)";
    public static final String DELETE_WAY_MEMBER_DUPLICATE =
            "DELETE FROM \"WayMember\" " +
            "WHERE ctid IN " +
            "(SELECT MIN(ctid) " +
            "FROM \"WayMember\" " +
            "GROUP BY \"wayId\",\"nodeId\",index " +
            "HAVING count(\"wayId\") > 1)";


    ////////////////////////////////////////////////////////////////////////////
    // RELATION TABLE AND TAG //////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static final String DROP_RELATION_TABLE =
            "DROP TABLE IF EXISTS \"Relation\"";
    public static final String DROP_RELATION_MEMBER_TABLE =
            "DROP TABLE IF EXISTS \"RelationMember\"";
    public static final String DROP_RELATION_TAG_TABLE =
            "DROP TABLE IF EXISTS \"RelationTag\"";

    public static final String CREATE_RELATION_TABLE =
            "CREATE TABLE \"Relation\"(" +
            "id bigint NOT NULL," +
            "version integer NOT NULL," +
            "\"userId\" integer NOT NULL," +
            "timestamp timestamp without time zone NOT NULL," +
            "changeset bigint NOT NULL" +
            ")";
    public static final String CREATE_RELATION_MEMBER_TABLE =
            "CREATE TABLE \"RelationMember\"(" +
            "\"relationId\" bigint NOT NULL," +
            "\"memberId\" bigint NOT NULL," +
            "type character(1) NOT NULL," +
            "role text NOT NULL," +
            "index integer NOT NULL" +
            ")";
    public static final String CREATE_RELATION_TAG_TABLE =
            "CREATE TABLE \"RelationTag\"(" +
            "\"relationId\" bigint NOT NULL," +
            "k text NOT NULL," +
            "v text" +
            ")";

    public static final String CREATE_RELATION_PK =
            "ALTER TABLE \"Relation\" ADD PRIMARY KEY (id)";
    public static final String CREATE_RELATION_FK =
            "ALTER TABLE \"Relation\" ADD FOREIGN KEY (\"userId\") REFERENCES \"User\"(id)";

    public static final String CREATE_RELATION_MEMBER_PK =
            "ALTER TABLE \"RelationMember\" ADD PRIMARY KEY (\"relationId\",index)";
    public static final String CREATE_RELATION_MEMBER_FK =
            "ALTER TABLE \"RelationMember\" ADD FOREIGN KEY (\"relationId\") REFERENCES \"Relation\"(id)";

    public static final String CREATE_RELATION_TAG_PK =
            "ALTER TABLE \"RelationTag\" ADD PRIMARY KEY (\"relationId\",k)";
    public static final String CREATE_RELATION_TAG_FK =
            "ALTER TABLE \"RelationTag\" ADD FOREIGN KEY (\"relationId\") REFERENCES \"Relation\"(id)";

    public static final String DELETE_RELATION_DUPLICATE =
            "DELETE FROM \"Relation\" " +
            "WHERE ctid IN " +
            "(SELECT MIN(ctid) " +
            "FROM \"Relation\" " +
            "GROUP BY id " +
            "HAVING count(id) > 1)";
    public static final String DELETE_RELATION_TAG_DUPLICATE =
            "DELETE FROM \"RelationTag\" " +
            "WHERE ctid IN " +
            "(SELECT MIN(ctid) " +
            "FROM \"RelationTag\" " +
            "GROUP BY \"relationId\",k " +
            "HAVING count(\"relationId\") > 1)";
    public static final String DELETE_RELATION_MEMBER_DUPLICATE =
            "DELETE FROM \"RelationMember\" " +
            "WHERE ctid IN " +
            "(SELECT MIN(ctid) " +
            "FROM \"RelationMember\" " +
            "GROUP BY \"relationId\",\"memberId\",index " +
            "HAVING count(\"relationId\") > 1)";


    ////////////////////////////////////////////////////////////////////////////
    // USER TABLE //////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static final String DROP_USER_TABLE =
            "DROP TABLE IF EXISTS \"User\"";

    public static final String CREATE_USER_TABLE =
            "CREATE TABLE \"User\"(" +
            "id integer NOT NULL," +
            "name text NOT NULL" +
            ")";

    public static final String CREATE_USER_PK =
            "ALTER TABLE \"User\" ADD PRIMARY KEY (id)";

    public static final String DELETE_USER_DUPLICATE =
            "DELETE FROM \"User\" " +
            "WHERE ctid NOT IN " +
            "(SELECT MIN(ctid) " +
            "FROM \"User\" " +
            "GROUP BY id " +
            "HAVING count(id) > 1)";
    


    ////////////////////////////////////////////////////////////////////////////
    // INSERT STATEMENTS ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static String createInsertNode(int count){
        final StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO \"Node\" ");
        sb.append("(id, version, \"userId\", timestamp, changeset, lat, lon)");
        sb.append("VALUES ");

        String oneVal = "(?, ?, ?, ?, ?, ?, ?)";
        for(int i=0;i<count-1;i++){
            sb.append(oneVal).append(',');
        }
        sb.append(oneVal);

        return sb.toString();
    }

    public static String createInsertNodeTag(int count){
        final StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO \"NodeTag\" ");
        sb.append("(\"nodeId\", k, v)");
        sb.append("VALUES ");

        String oneVal = "(?, ?, ?)";
        for(int i=0;i<count-1;i++){
            sb.append(oneVal).append(',');
        }
        sb.append(oneVal);

        return sb.toString();
    }

    public static String createInsertWay(int count){
        final StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO \"Way\" ");
        sb.append("(id, version, \"userId\", timestamp, changeset)");
        sb.append("VALUES ");

        String oneVal = "(?, ?, ?, ?, ?)";
        for(int i=0;i<count-1;i++){
            sb.append(oneVal).append(',');
        }
        sb.append(oneVal);

        return sb.toString();
    }

    public static String createInsertWayTag(int count){
        final StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO \"WayTag\" ");
        sb.append("(\"wayId\", k, v)");
        sb.append("VALUES ");

        String oneVal = "(?, ?, ?)";
        for(int i=0;i<count-1;i++){
            sb.append(oneVal).append(',');
        }
        sb.append(oneVal);

        return sb.toString();
    }

    public static String createInsertWayMember(int count){
        final StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO \"WayMember\" ");
        sb.append("(\"wayId\",\"nodeId\", index)");
        sb.append("VALUES ");

        String oneVal = "(?, ?, ?)";
        for(int i=0;i<count-1;i++){
            sb.append(oneVal).append(',');
        }
        sb.append(oneVal);

        return sb.toString();
    }

    public static String createInsertRelation(int count){
        final StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO \"Relation\" ");
        sb.append("(id, version, \"userId\", timestamp, changeset)");
        sb.append("VALUES ");

        String oneVal = "(?, ?, ?, ?, ?)";
        for(int i=0;i<count-1;i++){
            sb.append(oneVal).append(',');
        }
        sb.append(oneVal);

        return sb.toString();
    }

    public static String createInsertRelationTag(int count){
        final StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO \"RelationTag\" ");
        sb.append("(\"relationId\", k, v)");
        sb.append("VALUES ");

        String oneVal = "(?, ?, ?)";
        for(int i=0;i<count-1;i++){
            sb.append(oneVal).append(',');
        }
        sb.append(oneVal);

        return sb.toString();
    }

    public static String createInsertRelationMember(int count){
        final StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO \"RelationMember\" ");
        sb.append("(\"relationId\",\"memberId\", type, role, index)");
        sb.append("VALUES ");

        String oneVal = "(?, ?, ?, ?, ?)";
        for(int i=0;i<count-1;i++){
            sb.append(oneVal).append(',');
        }
        sb.append(oneVal);

        return sb.toString();
    }

    public static String createInsertUser(int count) {
        final StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO \"User\" ");
        sb.append("(id, name)");
        sb.append("VALUES ");

        String oneVal = "(?, ?)";
        for(int i=0;i<count-1;i++){
            sb.append(oneVal).append(',');
        }
        sb.append(oneVal);

        return sb.toString();
    }
}
