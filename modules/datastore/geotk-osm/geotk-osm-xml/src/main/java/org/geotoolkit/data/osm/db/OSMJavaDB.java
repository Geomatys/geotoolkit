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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.osm.model.IdentifiedElement;
import org.geotoolkit.data.osm.model.Node;
import org.geotoolkit.data.osm.model.Relation;
import org.geotoolkit.data.osm.model.Way;

import org.geotoolkit.util.FileUtilities;

/**
 * Store osm model objects in JavaDB.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMJavaDB {

    private static final File OSM_CACHE_FOLDER = new File(System.getProperty("java.io.tmpdir") + File.separator + "osm_deflat");

    private final Connection cnx;
    private final PreparedStatement sqlAddNode;
    private final PreparedStatement sqlAddWay;
    private final PreparedStatement sqlAddRelation;

    private final PreparedStatement sqlSelectNode;
    private final PreparedStatement sqlSelectWay;
    private final PreparedStatement sqlSelectRelation;

    public OSMJavaDB(String name) throws SQLException {
        final String folder = getTempFolder(name);
        FileUtilities.deleteDirectory(new File(folder));

        final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        final String conecURL = "jdbc:derby:"+ folder + ";create=true";
        try{
            Class.forName(driver);
        }catch(java.lang.ClassNotFoundException e){
            System.out.println("Erreur de chargement des pilotes");
        }

        cnx = DriverManager.getConnection(conecURL);

        Statement stmt = cnx.createStatement();
        stmt.execute("CREATE TABLE node (" +
                "id BIGINT NOT NULL, " +
                "ser BLOB" +
                ")");

        stmt.execute("CREATE TABLE way (" +
                "id BIGINT NOT NULL, " +
                "ser BLOB" +
                ")");

        stmt.execute("CREATE TABLE relation (" +
                "id BIGINT NOT NULL, " +
                "ser BLOB" +
                ")");

        stmt.close();

        //indexes are created by derby for primary keys.

        sqlAddNode = cnx.prepareStatement("INSERT INTO node (id,ser) values (?,?)");
        sqlAddWay = cnx.prepareStatement("INSERT INTO way (id,ser) values (?,?)");
        sqlAddRelation = cnx.prepareStatement("INSERT INTO relation (id,ser) values (?,?)");

        sqlSelectNode = cnx.prepareStatement("SELECT * FROM node");
        sqlSelectWay = cnx.prepareStatement("SELECT * FROM way");
        sqlSelectRelation = cnx.prepareStatement("SELECT * FROM relation");
    }

    public void createIndexes() throws SQLException{
        cnx.createStatement().execute("ALTER TABLE node ADD PRIMARY KEY (id)");
        cnx.createStatement().execute("ALTER TABLE way ADD PRIMARY KEY (id)");
        cnx.createStatement().execute("ALTER TABLE relation ADD PRIMARY KEY (id)");
    }

    public void append(Node node) throws SQLException, IOException{
        if(node == null) return;
        append(node,sqlAddNode);
    }

    public void append(Way way) throws SQLException, IOException{
        if(way == null) return;
        append(way,sqlAddWay);
    }

    public void append(Relation relation) throws SQLException, IOException{
        if(relation == null) return;
        append(relation,sqlAddRelation);
    }

    private static void append(IdentifiedElement object, PreparedStatement stmt) throws IOException, SQLException{
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final ObjectOutputStream oout = new ObjectOutputStream(output);
        oout.writeObject(object);
        oout.flush();
        final ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());

        stmt.setLong(1, object.getId());
        stmt.setBlob(2, input);
        stmt.executeUpdate();
        input.close();
    }

    public Iterator<Node> getNodeIterator() throws SQLException{
        final ResultSet set = sqlSelectNode.executeQuery();
        return new ResultsetIterator(set);
    }

    public Node getNode(long id){
        throw new IllegalStateException("not yet");
    }

    public Iterator<Way> getWayIterator(){
        throw new IllegalStateException("not yet");
    }

    public Way getWay(long id){
        throw new IllegalStateException("not yet");
    }

    public Iterator<Relation> getRelationIterator(){
        throw new IllegalStateException("not yet");
    }

    public Relation getRelation(long id){
        throw new IllegalStateException("not yet");
    }




    /**
     * Get or create a temp folder to store the deflat osm. the folder is based on the
     * file name.
     */
    private static String getTempFolder(String pathName){

        //we must test both since the slash may change if we are in a jar on windows
        int lastSlash = pathName.lastIndexOf('/');
        int second = pathName.lastIndexOf('\\');
        if(second > lastSlash) lastSlash = second;

        if(lastSlash >= 0){
            pathName = pathName.substring(lastSlash+1, pathName.length());
        }

        final int stop = pathName.lastIndexOf(".");

        final String name;
        if(stop >= 0){
            //remove the extension part
            name = pathName.substring(0, stop);
        }else{
            //no extension? use the full name
            name = pathName;
        }
        final StringBuilder builder = new StringBuilder(OSM_CACHE_FOLDER.getAbsolutePath());
        builder.append(File.separator);
        builder.append(name);

        return builder.toString();
    }

    private static final class ResultsetIterator implements Iterator{

        private final ResultSet rs;
        private Object ser = null;

        public ResultsetIterator(ResultSet rs) {
            this.rs = rs;
        }

        @Override
        public boolean hasNext() {
            if(ser == null){
                check();
            }
            return ser != null;
        }

        @Override
        public Object next() {
            if(ser == null) check();

            Object candidate = ser;
            ser = null;
            return candidate;
        }

        private void check(){
            if(ser == null){
                try {
                    if (rs.next()) {
                        Blob blob = rs.getBlob(2);
                        ObjectInputStream stream = new ObjectInputStream(blob.getBinaryStream());
                        ser = stream.readObject();
                        stream.close();
                    }else{
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(OSMJavaDB.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(OSMJavaDB.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(OSMJavaDB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }


        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }

        @Override
        protected void finalize() throws Throwable {
            rs.close();
            super.finalize();
        }

    }

}
