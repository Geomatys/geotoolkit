/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.filestore;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;

import org.geotoolkit.coverage.AbstractCoverageStore;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.coverage.CoverageType;
import org.geotoolkit.feature.DefaultName;
import org.apache.sis.storage.DataStoreException;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Coverage store relying on an xml file.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class XMLCoverageStore extends AbstractCoverageStore{

    private final File root;
    private final URL rootPath;
    private String format;
    private final Map<Name,XMLCoverageReference> names = new HashMap<>();

    XMLCoverageStore(ParameterValueGroup params) throws URISyntaxException{
        super(params);
        rootPath = (URL) params.parameter(XMLCoverageStoreFactory.PATH.getName().getCode()).getValue();
        root = new File(rootPath.toURI());
        format = (String) params.parameter(FileCoverageStoreFactory.TYPE.getName().getCode()).getValue();
        if(format.equals("AUTO")) format = "PNG";
        explore();
    }

    @Override
    public CoverageStoreFactory getFactory() {
        return CoverageStoreFinder.getFactoryById(XMLCoverageStoreFactory.NAME);
    }

    /**
     * Search all xml files in the folder which define a pyramid model.
     */
    private void explore(){

        if(!root.exists()){
            root.mkdirs();
        }

        final File[] childs = root.listFiles();
        if(childs != null){
            for(File f : childs){
                if(f.isDirectory() || !f.getName().toLowerCase().endsWith(".xml")){
                    continue;
                }

                //try to parse the file
                try {
                    final XMLPyramidSet set = XMLPyramidSet.read(f);
                    final Name name = new DefaultName(getDefaultNamespace(), set.getId());
                    final XMLCoverageReference ref = new XMLCoverageReference(this,name,set);
                    names.put(name, ref);
                } catch (JAXBException ex) {
                    getLogger().log(Level.FINE, "file is not a pyramid : {0}", f.getPath());
                }
            }
        }
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        return names.keySet();
    }

    @Override
    public CoverageReference getCoverageReference(Name name) throws DataStoreException {
        typeCheck(name);
        return names.get(name);
    }

    @Override
    public void dispose() {
    }

    @Override
    public CoverageReference create(Name name) throws DataStoreException {
        name = new DefaultName(getDefaultNamespace(), name.getLocalPart());
        if(names.containsKey(name)){
            throw new DataStoreException("Name already used in store : " + name.getLocalPart());
        }

        final XMLPyramidSet set = new XMLPyramidSet(format);
        set.initialize(new File(root, name.getLocalPart()+".xml"));
        final XMLCoverageReference ref = new XMLCoverageReference(this,name,set);
        names.put(name, ref);
        ref.save();
        return ref;
    }

	@Override
	public CoverageType getType() {
		return CoverageType.PYRAMID;
	}
}
