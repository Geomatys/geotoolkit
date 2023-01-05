/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.storage.memory;


import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;

/**
 * Image reader for BufferedImage.
 * Just a wrapper class.
 */
public final class IImageReader extends ImageReader {

    public IImageReader(ImageReaderSpi spi) {
        super(spi);
    }

    private RenderedImage getImage() throws IOException {
        if (input instanceof RenderedImage) {
            return (RenderedImage) input;
        } else {
            throw new IOException("Input is not a RenderedImage : " + input);
        }
    }

    @Override
    public int getNumImages(boolean allowSearch) throws IOException {
        return 1;
    }

    @Override
    public int getWidth(int imageIndex) throws IOException {
        return getImage().getWidth();
    }

    @Override
    public int getHeight(int imageIndex) throws IOException {
        return getImage().getHeight();
    }

    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex) throws IOException {
        ImageTypeSpecifier spec = new ImageTypeSpecifier(getImage());
        return Collections.singleton(spec).iterator();
    }

    @Override
    public IIOMetadata getStreamMetadata() throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
        //defensive copy
        final RenderedImage image = getImage();
        final WritableRaster rastercp = image.copyData(null);
        return new BufferedImage(image.getColorModel(), rastercp, image.getColorModel().isAlphaPremultiplied(), null);
    }

    public static final class IISpi extends ImageReaderSpi{
        public static final IISpi INSTANCE = new IISpi();

        public IISpi() {
            inputTypes = new Class[]{BufferedImage.class};
        }

        @Override
        public boolean canDecodeInput(Object source) throws IOException {
            return source instanceof BufferedImage;
        }

        @Override
        public ImageReader createReaderInstance(Object extension) throws IOException {
            return new IImageReader(this);
        }

        @Override
        public String getDescription(Locale locale) {
            return "Java Image Reader";
        }

    }
}
