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
package org.geotoolkit.coverage.xmlstore;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;

import org.geotoolkit.coverage.AbstractCoverageStore;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.coverage.CoverageType;
import org.geotoolkit.feature.type.DefaultName;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataNode;
import org.geotoolkit.storage.DefaultDataNode;
import org.geotoolkit.feature.type.Name;
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
    private final DataNode rootNode = new DefaultDataNode();

    public XMLCoverageStore(File root) throws URISyntaxException, MalformedURLException{
        this(toParameters(root));
    }
    
    public XMLCoverageStore(URL rootPath) throws URISyntaxException{
        this(toParameters(rootPath));
    }
        
    public XMLCoverageStore(ParameterValueGroup params) throws URISyntaxException{
        super(params);
        rootPath = (URL) params.parameter(XMLCoverageStoreFactory.PATH.getName().getCode()).getValue();
        root = new File(rootPath.toURI());
        explore();
    }

    private static ParameterValueGroup toParameters(File root) throws MalformedURLException{
        final ParameterValueGroup params = XMLCoverageStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(XMLCoverageStoreFactory.PATH, params).setValue(root.toURI().toURL());
        return params;
    }
    
    private static ParameterValueGroup toParameters(URL rootPath) {
        final ParameterValueGroup params = XMLCoverageStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(XMLCoverageStoreFactory.PATH, params).setValue(rootPath);
        return params;
    }
    
    @Override
    public CoverageStoreFactory getFactory() {
        return CoverageStoreFinder.getFactoryById(XMLCoverageStoreFactory.NAME);
    }

    @Override
    public DataNode getRootNode() {
        return rootNode;
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
                    //TODO useless copy here
                    final XMLCoverageReference set = XMLCoverageReference.read(f);
                    final Name name = new DefaultName(getDefaultNamespace(), set.getId());
                    final XMLCoverageReference ref = new XMLCoverageReference(this,name,set.getPyramidSet());
                    ref.copy(set);
                    rootNode.getChildren().add(ref);
                } catch (JAXBException ex) {
                    getLogger().log(Level.INFO, "file is not a pyramid : {0}", f.getPath());
                }
            }
        }
    }

    @Override
    public void close() {
    }

    @Override
    public CoverageReference create(Name name) throws DataStoreException {
        name = new DefaultName(getDefaultNamespace(), name.getLocalPart());
        final Set<Name> names = getNames();
        if(names.contains(name)){
            throw new DataStoreException("Name already used in store : " + name.getLocalPart());
        }

        final XMLPyramidSet set = new XMLPyramidSet();
        final XMLCoverageReference ref = new XMLCoverageReference(this,name,set);
        ref.initialize(new File(root, name.getLocalPart()+".xml"));
        rootNode.getChildren().add(ref);
        ref.save();
        return ref;
    }

    @Override
    public CoverageType getType() {
        return CoverageType.PYRAMID;
    }
}
