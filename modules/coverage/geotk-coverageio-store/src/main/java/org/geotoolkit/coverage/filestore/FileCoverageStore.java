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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageReaderSpi;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.AbstractCoverageStore;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.coverage.CoverageType;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.image.io.NamedImageStore;
import org.geotoolkit.image.io.XImageIO;
import org.apache.sis.internal.storage.IOUtilities;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.util.ImageIOUtilities;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Coverage Store which rely on standard java readers and writers.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FileCoverageStore extends AbstractCoverageStore{

    private static final Logger LOGGER = Logging.getLogger(FileCoverageStore.class);
    private final File root;
    private final String format;
    private final URL rootPath;
    private final Map<Name,FileCoverageReference> names = new HashMap<Name, FileCoverageReference>();
    //default spi
    final ImageReaderSpi spi;

    FileCoverageStore(ParameterValueGroup params) throws URISyntaxException{
        super(params);
        rootPath = (URL) params.parameter(FileCoverageStoreFactory.PATH.getName().getCode()).getValue();
        root = new File(rootPath.toURI());
        format = (String) params.parameter(FileCoverageStoreFactory.TYPE.getName().getCode()).getValue();
        visit(root);

        if("AUTO".equals(format)){
            spi = null;
        }else{
            spi = XImageIO.getReaderSpiByFormatName(format);
        }

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
    private void test(final File candidate) {
        if(!candidate.isFile()){
            return;
        }

        ImageReader reader = null;
        try {
            //don't comment this block, This raise an error if no reader for the file can be found
            //this way we are sure that the file is an image.
            reader = createReader(candidate,spi);
            final String fullName = candidate.getName();
            final int idx = fullName.lastIndexOf('.');
            final String filename = fullName.substring(0, idx);
            final String nmsp = getDefaultNamespace();

            final int nbImage = reader.getNumImages(true);

            if(reader instanceof NamedImageStore){
                //try to find a proper name for each image
                final NamedImageStore nis = (NamedImageStore) reader;

                final List<String> imageNames = nis.getImageNames();
                for(int i=0,n=imageNames.size();i<n;i++){
                    final String in = imageNames.get(i);
                    final Name name = new DefaultName(nmsp,filename+"."+in);
                    final FileCoverageReference previous = names.put(
                            name,
                            new FileCoverageReference(this,name,candidate,i));

                    if(previous != null){
                        getLogger().log(Level.WARNING, "Several files with name : "+name+" exist in folder :" + root.getPath());
                    }
                }

            }else{
                for(int i=0;i<nbImage;i++){
                    final Name name;
                    if(nbImage == 1){
                        //don't number it if there is only one
                        name = new DefaultName(nmsp,filename);
                    }else{
                        name = new DefaultName(nmsp,filename+"."+i);
                    }

                    final FileCoverageReference previous = names.put(
                            name,
                            new FileCoverageReference(this,name,candidate,i));

                    if(previous != null){
                        getLogger().log(Level.WARNING, "Several files with name : "+name+" exist in folder :" + root.getPath());
                    }
                }
            }


        } catch (Exception ex) {
            //Exception type is not specified cause we can get IOException as IllegalArgumentException.
            LOGGER.log(Level.WARNING, "Error for file {0} : {1}", new Object[]{candidate.getName(), ex.getMessage()});
        } finally {
            ImageIOUtilities.releaseReader(reader);
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
    ImageReader createReader(final File candidate, ImageReaderSpi spi) throws IOException{
        final ImageReader reader;
        if(spi == null){
            if (!IOUtilities.extension(candidate).isEmpty()) {
                reader = XImageIO.getReaderBySuffix(candidate, Boolean.FALSE, Boolean.FALSE);
            } else {
                reader = XImageIO.getReader(candidate,Boolean.FALSE,Boolean.FALSE);
            }
        }else{
            reader = spi.createReaderInstance();
            Object in = ImageIOUtilities.toSupportedInput(spi, candidate);
            reader.setInput(in);
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
        final ImageReaderSpi readerSpi = createReader(candidate,spi).getOriginatingProvider();
        final String[] writerSpiNames = readerSpi.getImageWriterSpiNames();
        if(writerSpiNames == null || writerSpiNames.length == 0){
            throw new IOException("No writer for this format.");
        }

        return XImageIO.getWriterByFormatName(readerSpi.getFormatNames()[0], candidate, null);
    }

    @Override
    public CoverageType getType() {
        return CoverageType.GRID;
    }
}
