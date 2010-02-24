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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.data.osm.model.Member;
import org.geotoolkit.data.osm.model.Node;
import org.geotoolkit.data.osm.model.Relation;
import org.geotoolkit.data.osm.model.User;
import org.geotoolkit.data.osm.model.Way;

import org.geotoolkit.util.collection.UnSynchronizedCache;

/**
 * This class is made for creation and manipulation of an OSM database in postgres.
 *
 *
 * Not thread safe.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMPostgresDB {

    private final Connection cnx;

    private final Set<Integer> usersAdded = new HashSet<Integer>();

    private final Statements nodeInsert = new Statements(Statements.NODE);
    private final Statements nodeTagInsert = new Statements(Statements.NODE_TAG);
    private final Statements wayInsert = new Statements(Statements.WAY);
    private final Statements wayTagInsert = new Statements(Statements.WAY_TAG);
    private final Statements wayMemberInsert = new Statements(Statements.WAY_MEMBER);
    private final Statements relationInsert = new Statements(Statements.RELATION);
    private final Statements relationTagInsert = new Statements(Statements.RELATION_TAG);
    private final Statements relationMemberInsert = new Statements(Statements.RELATION_MEMBER);
    private final Statements userInsert = new Statements(Statements.USER);

    private Savepoint savePoint = null;

    public OSMPostgresDB(String host, String database,String username, String password) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        cnx = DriverManager.getConnection("jdbc:postgresql://"+host+"/"+database,username,password);
        cnx.setAutoCommit(false);
        cnx.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        savePoint = cnx.setSavepoint();
    }

    public void commit() throws SQLException{
        cnx.commit();
        savePoint = cnx.setSavepoint();
    }

    public void rollBack() throws SQLException{
        if(savePoint != null){
            cnx.rollback(savePoint);
        }else{
            cnx.rollback();
        }
    }

    public void checkUserIds() throws SQLException{
        final Statement stmt = cnx.createStatement();

        usersAdded.clear();

        ResultSet rs = stmt.executeQuery("SELECT id FROM \"User\"");
        while(rs.next()){
            usersAdded.add(rs.getInt(1));
        }

        rs.close();
        stmt.close();
    }

    public int[] countAll() throws SQLException{
        final int[] counts = new int[9];

        final Statement stmt = cnx.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT count(id) FROM \"Node\"");        
        rs.next();
        counts[0] = rs.getInt(1);
        rs.close();

        rs = stmt.executeQuery("SELECT count(\"nodeId\") FROM \"NodeTag\"");        
        rs.next();
        counts[1] = rs.getInt(1);
        rs.close();

        rs = stmt.executeQuery("SELECT count(id) FROM \"Way\"");        
        rs.next();
        counts[2] = rs.getInt(1);
        rs.close();

        rs = stmt.executeQuery("SELECT count(\"wayId\") FROM \"WayTag\"");        
        rs.next();
        counts[3] = rs.getInt(1);
        rs.close();

        rs = stmt.executeQuery("SELECT count(\"wayId\") FROM \"WayMember\"");        
        rs.next();
        counts[4] = rs.getInt(1);
        rs.close();

        rs = stmt.executeQuery("SELECT count(id) FROM \"Relation\"");        
        rs.next();
        counts[5] = rs.getInt(1);
        rs.close();

        rs = stmt.executeQuery("SELECT count(\"relationId\") FROM \"RelationTag\"");        
        rs.next();
        counts[6] = rs.getInt(1);
        rs.close();
        
        rs = stmt.executeQuery("SELECT count(\"relationId\") FROM \"RelationMember\"");        
        rs.next();
        counts[7] = rs.getInt(1);
        rs.close();
        
        rs = stmt.executeQuery("SELECT count(id) FROM \"User\"");        
        rs.next();
        counts[8] = rs.getInt(1);
        rs.close();
        

        stmt.close();

        return counts;
    }


    /**
     * Drop all tables if they exists.
     * 
     * @throws SQLException
     */
    public void clearDataBase() throws SQLException{
        final Statement stmt = cnx.createStatement();

        stmt.execute(OSMPostgresQueries.DROP_RELATION_TAG_TABLE);
        stmt.execute(OSMPostgresQueries.DROP_RELATION_MEMBER_TABLE);
        stmt.execute(OSMPostgresQueries.DROP_WAY_TAG_TABLE);
        stmt.execute(OSMPostgresQueries.DROP_WAY_MEMBER_TABLE);
        stmt.execute(OSMPostgresQueries.DROP_NODE_TAG_TABLE);

        stmt.execute(OSMPostgresQueries.DROP_RELATION_TABLE);
        stmt.execute(OSMPostgresQueries.DROP_WAY_TABLE);
        stmt.execute(OSMPostgresQueries.DROP_NODE_TABLE);
        stmt.execute(OSMPostgresQueries.DROP_USER_TABLE);

        stmt.close();
    }

    /**
     * Create tables but without primary key, foreign keys or indexes.
     *
     * @throws SQLException
     */
    public void createTables() throws SQLException{
        final Statement stmt = cnx.createStatement();

        stmt.execute(OSMPostgresQueries.CREATE_NODE_TABLE);
        stmt.execute(OSMPostgresQueries.CREATE_NODE_TAG_TABLE);

        stmt.execute(OSMPostgresQueries.CREATE_WAY_TABLE);
        stmt.execute(OSMPostgresQueries.CREATE_WAY_TAG_TABLE);
        stmt.execute(OSMPostgresQueries.CREATE_WAY_MEMBER_TABLE);

        stmt.execute(OSMPostgresQueries.CREATE_RELATION_TABLE);
        stmt.execute(OSMPostgresQueries.CREATE_RELATION_TAG_TABLE);
        stmt.execute(OSMPostgresQueries.CREATE_RELATION_MEMBER_TABLE);

        stmt.execute(OSMPostgresQueries.CREATE_USER_TABLE);

        stmt.close();
    }

    /**
     * Create table primary keys.
     *
     * @throws SQLException
     */
    public void createPrimaryKeys() throws SQLException{
        final Statement stmt = cnx.createStatement();

        stmt.execute(OSMPostgresQueries.CREATE_NODE_PK);
        stmt.execute(OSMPostgresQueries.CREATE_NODE_TAG_PK);

        stmt.execute(OSMPostgresQueries.CREATE_WAY_PK);
        stmt.execute(OSMPostgresQueries.CREATE_WAY_TAG_PK);
        stmt.execute(OSMPostgresQueries.CREATE_WAY_MEMBER_PK);

        stmt.execute(OSMPostgresQueries.CREATE_RELATION_PK);
        stmt.execute(OSMPostgresQueries.CREATE_RELATION_TAG_PK);
        stmt.execute(OSMPostgresQueries.CREATE_RELATION_MEMBER_PK);

        stmt.execute(OSMPostgresQueries.CREATE_USER_PK);

        stmt.close();
    }

    /**
     * Create table foreign keys.
     *
     * @throws SQLException
     */
    public void createForeignKeys() throws SQLException{
        final Statement stmt = cnx.createStatement();

        stmt.execute(OSMPostgresQueries.CREATE_NODE_FK);
        stmt.execute(OSMPostgresQueries.CREATE_NODE_TAG_FK);

        stmt.execute(OSMPostgresQueries.CREATE_WAY_FK);
        stmt.execute(OSMPostgresQueries.CREATE_WAY_TAG_FK);
        stmt.execute(OSMPostgresQueries.CREATE_WAY_MEMBER_FK1);
        stmt.execute(OSMPostgresQueries.CREATE_WAY_MEMBER_FK2);

        stmt.execute(OSMPostgresQueries.CREATE_RELATION_FK);
        stmt.execute(OSMPostgresQueries.CREATE_RELATION_TAG_FK);
        stmt.execute(OSMPostgresQueries.CREATE_RELATION_MEMBER_FK);
        
        stmt.close();
    }

    public void createGeometryFields() throws SQLException{
        final Statement stmt = cnx.createStatement();

        stmt.execute(OSMPostgresQueries.CREATE_NODE_GEOMETRY_FIELD);
        stmt.execute(OSMPostgresQueries.CREATE_WAY_GEOMETRY_FIELD);

        stmt.close();
    }

    public void generateGeometries() throws SQLException{
        final Statement stmt = cnx.createStatement();

        stmt.execute(OSMPostgresQueries.GENERATE_NODE_GEOMETRY);
        stmt.execute(OSMPostgresQueries.GENERATE_WAY_GEOMETRY);

        stmt.close();
    }

    public void insertNode(Node node) throws SQLException{
    }

    /**
     *
     *
     * @param nodes
     * @return int array : first one is the inserted number of nodes, second is tags number
     * @throws SQLException
     */
    public int[] insertNode(List<Node> nodes) throws SQLException{
        final int wantedSize = nodes.size();

        if(wantedSize == 0) return new int[]{0,0};

        final PreparedStatement stmt = nodeInsert.get(wantedSize);

        int nbUser = 0;
        int nbTag = 0;
        int i=1;
        for(Node node : nodes){
            stmt.setLong(i++, node.getId());
            stmt.setInt(i++, node.getVersion());
            stmt.setInt(i++, node.getUser().getId());
            stmt.setTimestamp(i++, new Timestamp(node.getTimestamp()));
            stmt.setInt(i++, node.getChangeset());
            stmt.setDouble(i++, node.getLatitude());
            stmt.setDouble(i++, node.getLongitude());

            nbTag += insertNodeTag(node.getTags(), node.getId());
            nbUser += insertUser(node.getUser());
        }

        stmt.executeUpdate();

        return new int[]{wantedSize, nbTag, nbUser};
    }

    public int insertNodeTag(Map<String,String> map, long entityId) throws SQLException{
        final int wantedSize = map.size();

        if(wantedSize == 0) return 0;

        final PreparedStatement stmt = nodeTagInsert.get(wantedSize);

        int i=1;
        for(Map.Entry<String,String> entry : map.entrySet()){
            stmt.setLong(i++, entityId);
            stmt.setString(i++, entry.getKey());
            stmt.setString(i++, entry.getValue());
        }

        stmt.executeUpdate();
        return wantedSize;
    }


    public int[] insertWay(List<Way> ways) throws SQLException{
        final int wantedSize = ways.size();

        if(wantedSize == 0) return new int[]{0,0,0};

        final PreparedStatement stmt = wayInsert.get(wantedSize);

        int nbUser=0;
        int nbMember=0;
        int nbTag=0;
        int i=1;
        for(Way way : ways){
            stmt.setLong(i++, way.getId());
            stmt.setInt(i++, way.getVersion());
            stmt.setInt(i++, way.getUser().getId());
            stmt.setTimestamp(i++, new Timestamp(way.getTimestamp()));
            stmt.setInt(i++, way.getChangeset());

            nbTag += insertWayTag(way.getTags(), way.getId());
            nbMember += insertWayMember(way.getNodesIds(), way.getId());
            nbUser += insertUser(way.getUser());
        }

        stmt.executeUpdate();

        return new int[]{wantedSize, nbTag, nbMember,nbUser};
    }

    public int insertWayTag(Map<String,String> map, long entityId) throws SQLException{
        final int wantedSize = map.size();

        if(wantedSize == 0) return 0;

        final PreparedStatement stmt = wayTagInsert.get(wantedSize);

        int i=1;
        for(Map.Entry<String,String> entry : map.entrySet()){
            stmt.setLong(i++, entityId);
            stmt.setString(i++, entry.getKey());
            stmt.setString(i++, entry.getValue());
        }

        stmt.executeUpdate();
        return wantedSize;
    }

    public int insertWayMember(List<Long> members, long wayId) throws SQLException{
        final int wantedSize = members.size();

        if(wantedSize == 0) return 0;

        final PreparedStatement stmt = wayMemberInsert.get(wantedSize);

        int i=1;
        for(int k=0; k<wantedSize; k++){
            stmt.setLong(i++, wayId);
            stmt.setLong(i++, members.get(k));
            stmt.setInt(i++, k);
        }

        stmt.executeUpdate();
        return wantedSize;
    }

    public int[] insertRelation(List<Relation> relations) throws SQLException{
        final int wantedSize = relations.size();

        if(wantedSize == 0) return new int[]{0,0,0};

        final PreparedStatement stmt = relationInsert.get(wantedSize);

        int nbUser=0;
        int nbMember=0;
        int nbTag=0;
        int i=1;
        for(Relation relation : relations){
            stmt.setLong(i++, relation.getId());
            stmt.setInt(i++, relation.getVersion());
            stmt.setInt(i++, relation.getUser().getId());
            stmt.setTimestamp(i++, new Timestamp(relation.getTimestamp()));
            stmt.setInt(i++, relation.getChangeset());

            nbTag += insertRelationTag(relation.getTags(), relation.getId());
            nbMember += insertRelationMember(relation.getMembers(), relation.getId());
            nbUser += insertUser(relation.getUser());
        }

        stmt.executeUpdate();

        return new int[]{wantedSize,nbTag,nbMember,nbUser};
    }

    public int insertRelationTag(Map<String,String> map, long entityId) throws SQLException{
        final int wantedSize = map.size();

        if(wantedSize == 0) return 0;

        final PreparedStatement stmt = relationTagInsert.get(wantedSize);

        int i=1;
        for(Map.Entry<String,String> entry : map.entrySet()){
            stmt.setLong(i++, entityId);
            stmt.setString(i++, entry.getKey());
            stmt.setString(i++, entry.getValue());
        }

        stmt.executeUpdate();
        return wantedSize;
    }

    public int insertRelationMember(List<Member> members, long relationId) throws SQLException{
        final int wantedSize = members.size();

        if(wantedSize == 0) return 0;

        final PreparedStatement stmt = relationMemberInsert.get(wantedSize);

        int i=1;
        for(int k=0; k<wantedSize; k++){
            final Member member = members.get(k);

            stmt.setLong(i++, relationId);
            stmt.setLong(i++, member.getReference());
            stmt.setString(i++, ""+member.getType().charValue());
            stmt.setString(i++, member.getRole());
            stmt.setInt(i++,k);
        }

        stmt.executeUpdate();
        return wantedSize;
    }

    private int insertUser(User user) throws SQLException{
        if(usersAdded.contains(user.getId())) return 0;

        final PreparedStatement stmt = userInsert.get(1);

        stmt.setInt(1, user.getId());
        stmt.setString(2, user.getName());

        stmt.executeUpdate();
        commit();
        usersAdded.add(user.getId());
        return 1;
    }

    public void eraseDuplicates() throws SQLException {

        final Statement stmt = cnx.createStatement();

        stmt.execute(OSMPostgresQueries.DELETE_NODE_DUPLICATE);
        commit();
        stmt.execute(OSMPostgresQueries.DELETE_NODE_TAG_DUPLICATE);
        commit();
        stmt.execute(OSMPostgresQueries.DELETE_WAY_DUPLICATE);
        commit();
        stmt.execute(OSMPostgresQueries.DELETE_WAY_TAG_DUPLICATE);
        commit();
        stmt.execute(OSMPostgresQueries.DELETE_WAY_MEMBER_DUPLICATE);
        commit();
        stmt.execute(OSMPostgresQueries.DELETE_RELATION_DUPLICATE);
        commit();
        stmt.execute(OSMPostgresQueries.DELETE_RELATION_TAG_DUPLICATE);
        commit();
        stmt.execute(OSMPostgresQueries.DELETE_RELATION_MEMBER_DUPLICATE);
        commit();
        stmt.execute(OSMPostgresQueries.DELETE_USER_DUPLICATE);
        commit();

        stmt.close();
    }

    private final class Statements extends UnSynchronizedCache<Integer, PreparedStatement>{

        public static final int NODE = 0;
        public static final int NODE_TAG = 1;
        public static final int WAY = 2;
        public static final int WAY_TAG = 3;
        public static final int WAY_MEMBER = 4;
        public static final int RELATION = 5;
        public static final int RELATION_TAG = 6;
        public static final int RELATION_MEMBER = 7;
        public static final int USER = 8;

        private final int type;

        private Statements(int type){
            super(20);
            this.type = type;
        }

        public PreparedStatement get(int nb) throws SQLException {
            PreparedStatement stmt = get((Integer)nb);

            if(stmt == null){
                final String query;

                switch(type){
                    case NODE: query = OSMPostgresQueries.createInsertNode(nb); break;
                    case NODE_TAG: query = OSMPostgresQueries.createInsertNodeTag(nb); break;
                    case WAY: query = OSMPostgresQueries.createInsertWay(nb); break;
                    case WAY_TAG: query = OSMPostgresQueries.createInsertWayTag(nb); break;
                    case WAY_MEMBER: query = OSMPostgresQueries.createInsertWayMember(nb); break;
                    case RELATION: query = OSMPostgresQueries.createInsertRelation(nb); break;
                    case RELATION_TAG: query = OSMPostgresQueries.createInsertRelationTag(nb); break;
                    case RELATION_MEMBER: query = OSMPostgresQueries.createInsertRelationMember(nb); break;
                    case USER: query = OSMPostgresQueries.createInsertUser(nb); break;
                    default : throw new IllegalArgumentException("unknowend type : "+ nb);
                }
                stmt = cnx.prepareStatement(query);

                put((Integer)nb, stmt);
            }

            return stmt;
        }

    }

}
