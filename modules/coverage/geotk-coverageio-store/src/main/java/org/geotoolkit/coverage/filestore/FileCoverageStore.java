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
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageReaderSpi;
import org.geotoolkit.coverage.AbstractCoverageStore;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.XArrays;
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
    private final String format;
    private final URL rootPath;
    private final Map<Name,FileCoverageReference> names = new HashMap<Name, FileCoverageReference>();
    
    FileCoverageStore(ParameterValueGroup params) throws URISyntaxException{
        super(params);
        rootPath = (URL) params.parameter(FileCoverageStoreFactory.PATH.getName().getCode()).getValue();
        root = new File(rootPath.toURI());
        format = (String) params.parameter(FileCoverageStoreFactory.TYPE.getName().getCode()).getValue();
        visit(root);
    }

    @Override
    public CoverageStoreFactory getFactory() {
        return CoverageStoreFinder.getFactoryById(FileCoverageStoreFactory.NAME);
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
            final ImageReader reader = createReader(candidate);
            reader.dispose();

            final String fullName = candidate.getName();
            final int idx = fullName.lastIndexOf('.');
            final String filename = fullName.substring(0, idx);
            final String nmsp = getDefaultNamespace();
            final Name name = new DefaultName(nmsp,filename);
            final FileCoverageReference previous = names.put(
                    name, 
                    new FileCoverageReference(this,name,candidate));
            
            if(previous != null){
                getLogger().log(Level.WARNING, "Several files with name : "+name+" exist in folder :" + root.getPath());
            }
            

        } catch (Exception ex) {
            //Exception type is not specified cause we can get IOException as IllegalArgumentException.
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
    
    /**
     * Create a reader for the given file.
     * Detect automaticaly the spi if type is set to 'AUTO'.
     * 
     * @param candidate
     * @return ImageReader, never null
     * @throws IOException if fail to create a reader.
     */
    ImageReader createReader(final File candidate) throws IOException{
        final ImageReader reader;
        if("AUTO".equals(format)){
            reader = XImageIO.getReaderBySuffix(candidate, Boolean.TRUE, Boolean.TRUE);
        }else{
            final ImageReaderSpi spi = XImageIO.getReaderSpiByFormatName(format);
            reader = spi.createReaderInstance();
            
            if(spi.canDecodeInput(candidate)){
                reader.setInput(candidate);
            }else{
                final Object data = ImageIO.createImageInputStream(candidate);
                reader.setInput(data);
            }
        }
        
        return reader;
    }
    
    /**
     * Create a writer for the given file.
     * Detect automaticaly the spi if type is set to 'AUTO'.
     * 
     * @param candidate
     * @return ImageWriter, never null
     * @throws IOException if fail to create a writer.
     */
    ImageWriter createWriter(final File candidate) throws IOException{
        final ImageReaderSpi readerSpi = createReader(candidate).getOriginatingProvider();
        final String[] writerSpiNames = readerSpi.getImageWriterSpiNames();
        if(writerSpiNames == null || writerSpiNames.length == 0){
            throw new IOException("No writer for this format.");
        }
        
        return XImageIO.getWriterByFormatName(readerSpi.getFormatNames()[0], candidate, null);
    }
    
    
}
