/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.geotoolkit.coverage.CoverageReference;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.collection.TreeTable;
import org.geotoolkit.storage.DataNode;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractCoverageClient extends AbstractClient {

    public AbstractCoverageClient(ParameterValueGroup params) {
        super(params);
    }

    public abstract DataNode getRootNode() throws DataStoreException;

    public final Set<Name> getNames() throws DataStoreException {
        final Map<Name,CoverageReference> map = listReferences(getRootNode(), new HashMap<Name, CoverageReference>());
        return map.keySet();
    }

    public final CoverageReference getCoverageReference(Name name) throws DataStoreException {
        final Map<Name,CoverageReference> map = listReferences(getRootNode(), new HashMap<Name, CoverageReference>());
        final CoverageReference ref = map.get(name);
        if(ref==null){
            final StringBuilder sb = new StringBuilder("Type name : ");
            sb.append(name);
            sb.append(" do not exist in this datastore, available names are : ");
            for(final Name n : map.keySet()){
                sb.append(n).append(", ");
            }
            throw new DataStoreException(sb.toString());
        }
        return ref;
    }

    private Map<Name,CoverageReference> listReferences(TreeTable.Node node, Map<Name,CoverageReference> map){

        if(node instanceof CoverageReference){
            final CoverageReference cr = (CoverageReference) node;
            map.put(cr.getName(), cr);
        }

        for(TreeTable.Node child : node.getChildren()){
            listReferences(child, map);
        }

        return map;
    }

    ////////////////////////////////////////////////////////////////////////////
    // versioning methods : handle nothing by default                         //
    ////////////////////////////////////////////////////////////////////////////

    public boolean handleVersioning() {
        return false;
    }

    public VersionControl getVersioning(Name typeName) throws VersioningException {
        throw new VersioningException("Versioning not supported");
    }

    public CoverageReference getCoverageReference(Name name, Version version) throws DataStoreException {
        throw new DataStoreException("Versioning not supported");
    }

}
