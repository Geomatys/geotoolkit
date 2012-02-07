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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.imageio.ImageReader;
import org.geotoolkit.coverage.AbstractCoverageStore;
import org.geotoolkit.coverage.AbstractCoverageStoreFactory;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Coverage Store which rely on standard java readers and writers.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FileCoverageStore extends AbstractCoverageStore{

    private final File root;
    private final URL rootPath;
    private final Map<Name,FileCoverageReference> names = new HashMap<Name, FileCoverageReference>();
    
    FileCoverageStore(ParameterValueGroup params) throws URISyntaxException{
        super(Parameters.value(AbstractCoverageStoreFactory.NAMESPACE, params));
        rootPath = (URL) params.parameter(XMLCoverageStoreFactory.PATH.getName().getCode()).getValue();
        root = new File(rootPath.toURI());
        visit(root);
    }
    
    /**
     * Visit all files and directories contained in the directory specified.
     *
     * @param file 
     */
    private void visit(final File file) {

        if (file.isDirectory()) {
            final File[] list = file.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    visit(list[i]);
                }
            }
        } else {
            test(file);
        }
    }

    /**
     *
     * @param candidate Candidate to be a image file.
     */
    private void test(final File candidate){
        if(!candidate.isFile()){
            return;
        }
        
        try {
            //don't comment this block, This raise an error if no reader for the file can be found
            //this way we are sure that the file is an image.
            final ImageReader reader = XImageIO.getReaderBySuffix(candidate, Boolean.TRUE, Boolean.TRUE);
            reader.dispose();

            final String fullName = candidate.getName();
            final int idx = fullName.lastIndexOf('.');
            final String name = fullName.substring(0, idx);
            final String nmsp = getDefaultNamespace();
            final FileCoverageReference previous = names.put(
                    new DefaultName(nmsp,name), 
                    new FileCoverageReference(candidate));
            
            if(previous != null){
                getLogger().log(Level.WARNING, "Several files with name : "+name+" exist in folder :" + root.getPath());
            }
            

        } catch (IOException ex) {
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
    
}
