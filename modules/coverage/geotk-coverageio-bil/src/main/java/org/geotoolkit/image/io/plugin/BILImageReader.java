/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.image.io.plugin;

import com.sun.media.imageio.stream.RawImageInputStream;

import java.awt.Dimension;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Map;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;

import javax.imageio.stream.ImageInputStream;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.metadata.bil.HDRAccessor;

/**
 * Reader for the <cite>BIL</cite> format. 
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class BILImageReader extends WorldFileImageReader {

    public BILImageReader(final Spi provider) throws IOException {
        super(provider);
    }

    @Override
    protected Object createInput(final String readerID) throws IOException {        
        
        if(!"main".equals(readerID)){
            return super.createInput(readerID);
        }
        
        final ImageInputStream iis = (ImageInputStream) super.createInput(readerID);
                
        final Object hdrfile = IOUtilities.changeExtension(input, "hdr");
        final Map<String,String> parameters = HDRAccessor.read(hdrfile);        

        final int[] off = new int[]{0};
        final int w = Integer.valueOf(parameters.get(HDRAccessor.NCOLS));
        final int h = Integer.valueOf(parameters.get(HDRAccessor.NROWS));
        final int nbbit = Integer.valueOf(parameters.get(HDRAccessor.NBITS));
        final boolean signed = parameters.get(HDRAccessor.PIXELTYPE).contains("SIGNED");
        final boolean littleEndian = "I".equalsIgnoreCase(parameters.get(HDRAccessor.BYTEORDER));
        
        iis.setByteOrder((littleEndian)?ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
        
        final int transferType;
        if(nbbit <= 8){
            transferType = DataBuffer.TYPE_BYTE;
        }else if(nbbit <= 16){
            //todo use the signed value
            transferType = DataBuffer.TYPE_USHORT;
        }else{
            throw new IOException("Unsupported number of bits : " + nbbit);
        }
        
        final SampleModel sm = new ComponentSampleModel(transferType, w, h, 1, w, off);
        final long[] offsets = new long[]{0};
        final Dimension[] dimension = new Dimension[]{new Dimension(w, h)};
        final RawImageInputStream input = new RawImageInputStream(iis, sm, offsets, dimension);
        return input;
    }
    
    public static class Spi extends WorldFileImageReader.Spi {
        
        public Spi() throws IllegalArgumentException {
            super(Formats.getReaderByFormatName("RAW", Spi.class));
            this.suffixes = new String[]{"BIL","bil"};
            this.names = new String[]{"BIL","bil"};
        }
        
        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new BILImageReader(this);
        }

        @Override
        public boolean canDecodeInput(final Object source) throws IOException {
            //check if file has extension .bil and .hdr file is present
            
            final String extension = IOUtilities.extension(source);
            if(!"bil".equalsIgnoreCase(extension)){
                return false;
            }
            
            final Object hdrpath = IOUtilities.changeExtension(source, "hdr");
            final Object hdrfile = IOUtilities.tryToFile(hdrpath);
            if(hdrfile instanceof File){
                final File f = (File) hdrfile;
                return f.exists();
            }
            
            return false;
        }
        
        public static void registerDefaults(IIORegistry registry) {
            if (registry == null) {
                registry = IIORegistry.getDefaultInstance();
            }            
            final Spi provider = new Spi();
            registry.registerServiceProvider(provider, ImageReaderSpi.class);
            registry.setOrdering(ImageReaderSpi.class, provider, provider.main);
        }

        public static void unregisterDefaults(IIORegistry registry) {
            if (registry == null) {
                registry = IIORegistry.getDefaultInstance();
            }
            final Class<? extends Spi> type = Spi.class;
            final Spi provider = registry.getServiceProviderByClass(type);
            if (provider != null) {
                registry.deregisterServiceProvider(provider, ImageReaderSpi.class);
            }
        }
        
    }
    
}
