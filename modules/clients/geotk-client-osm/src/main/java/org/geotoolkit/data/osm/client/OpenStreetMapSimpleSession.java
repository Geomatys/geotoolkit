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

package org.geotoolkit.data.osm.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.osm.model.ChangeSet;
import org.geotoolkit.data.osm.model.Transaction;
import org.geotoolkit.data.osm.xml.OSMXMLReader;
import org.geotoolkit.security.ByThreadAuthenticator;

/**
 * Simplifies manipulation of an edition session on an osm server.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OpenStreetMapSimpleSession {

    private final OpenStreetMapServer server;
    private final String user;
    private final char[] password;
    private final String generator;
    private final String generatorVersion;
    private int changeSetId = -1;

    public OpenStreetMapSimpleSession(OpenStreetMapServer server, String user, char[] password,
            String generator, String generatorVersion){
        this.server = server;
        this.user = user;
        this.password = password;
        this.generator = generator;
        this.generatorVersion = generatorVersion;
    }

    public OpenStreetMapServer getServer(){
        return server;
    }

    public int getChangeSetId(){
        return changeSetId;
    }

    public int startChangeSet(String creator, String comment) throws IOException{
        if(changeSetId > 0){
            throw new IllegalArgumentException("A changeset is already open.");
        }

        final Map<String,String> tags = new HashMap<String, String>();
        if(creator != null) tags.put("created_by", creator);
        if(comment != null) tags.put("comment", comment);
        final ChangeSet set = new ChangeSet(null, null, null, null, null, tags);

        ByThreadAuthenticator.register();
        ByThreadAuthenticator.setCurrentThreadAuthentication(user, password);
        
        final CreateChangeSetRequest request = server.createCreateChangeSet();
        request.setChangeSet(set);

        final InputStream input = request.getResponseStream();
        final BufferedReader br = new BufferedReader(new InputStreamReader(input));
        final StringBuilder sb = new StringBuilder();
        String line;
        while( (line = br.readLine()) != null){
            sb.append(line);
        }
        br.close();
        input.close();

        changeSetId = Integer.valueOf(sb.toString());
        return changeSetId;
    }

    public ChangeSet getChangeSetDetail() throws IOException, XMLStreamException{
        final GetChangeSetRequest request = server.createGetChangeSet();
        request.setChangeSetID(changeSetId);

        final OSMXMLReader reader = new OSMXMLReader();
        final InputStream input = request.getResponseStream();
        ChangeSet cs = null;
        try{
            reader.setInput(input);
            while(reader.hasNext()){
                cs = (ChangeSet)reader.next();
            }
            reader.dispose();
            input.close();
        }finally{
            reader.dispose();
            input.close();
        }
        return cs;
    }


    public void SendTransactions(List<Transaction> transactions) throws IOException{
        if(changeSetId < 0){
            throw new IllegalArgumentException("No changeset open.");
        }

        ByThreadAuthenticator.register();
        ByThreadAuthenticator.setCurrentThreadAuthentication(user, password);

        final UploadRequest request = server.createUploadChangeSet();
        request.setChangeSetID(changeSetId);
        request.setGenerator(generator);
        request.setVersion(generatorVersion);
        request.transactions().addAll(transactions);
        request.getResponseStream().close();
    }

    public void endChangeset() throws IOException{
        if(changeSetId < 0){
            throw new IllegalArgumentException("No changeset open.");
        }

        ByThreadAuthenticator.register();
        ByThreadAuthenticator.setCurrentThreadAuthentication(user, password);

        final CloseChangeSetRequest request = server.createCloseChangeSet();
        request.setChangeSetID(changeSetId);
        request.getResponseStream().close();

        changeSetId = -1;
    }

}
