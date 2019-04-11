/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.coverage.grid;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ConvertedGridCoverage extends org.apache.sis.coverage.grid.GridCoverage {

    public static org.apache.sis.coverage.grid.GridCoverage convert(org.apache.sis.coverage.grid.GridCoverage coverage) {
        final List<SampleDimension> sds = coverage.getSampleDimensions();
        final List<SampleDimension> cfs = new ArrayList<>(sds.size());
        for (SampleDimension sd : sds) {
            cfs.add(sd.forConvertedValues(true));
        }
        return new ConvertedGridCoverage(coverage, cfs);
    }

    private final org.apache.sis.coverage.grid.GridCoverage coverage;

    private ConvertedGridCoverage(org.apache.sis.coverage.grid.GridCoverage base, List<SampleDimension> sampleDims) {
        super(base.getGridGeometry(), sampleDims);
        this.coverage = base;
    }

    @Override
    public RenderedImage render(GridExtent sliceExtent) throws CannotEvaluateException {
        final BufferedImage render = (BufferedImage) coverage.render(sliceExtent);
        final List<SampleDimension> sampleDimensions = getSampleDimensions();
        final int numBands = sampleDimensions.size();
        final MathTransform1D[] transforms = new MathTransform1D[numBands];
        final MathTransform1D[] ivtransforms = new MathTransform1D[numBands];
        boolean isIdentity = true;
        for (int i=0; i<numBands; i++) {
            MathTransform1D transform = sampleDimensions.get(i).forConvertedValues(false).getTransferFunction().orElse(null);
            if (transform == null) transform = (MathTransform1D) MathTransforms.linear(1.0, 0.0);
            transforms[i] = transform;
            try {
                ivtransforms[i] = transform.inverse();
            } catch (NoninvertibleTransformException ex) {
                ivtransforms[i] = (MathTransform1D) MathTransforms.linear(Double.NaN, 0.0);
            }
            isIdentity &= transform.isIdentity();
        }
        if (isIdentity) {
            return render;
        }

        final WritableRaster raster = render.getRaster();
        final SampleModel baseSm = raster.getSampleModel();
        final DataBuffer dataBuffer = raster.getDataBuffer();
        final ConvertedSampleModel convSm = new ConvertedSampleModel(baseSm, transforms, ivtransforms);

        final WritableRaster convRaster = WritableRaster.createWritableRaster(convSm, dataBuffer, new Point(0, 0));
        //default color models have a lot of constraints
        final ColorModel cm = new ConvertedColorModel(32, sampleDimensions.get(0).getSampleRange().get());
        //final WritableRaster convRaster = RasterFactory.createWritableRaster(convSm, dataBuffer, new Point(0, 0));
        //final ColorModel colors = ColorModelFactory.createColorModel(sampleDimensions.toArray(new SampleDimension[0]), 0, convRaster.getTransferType(), ColorModelFactory.GRAYSCALE);

        return new BufferedImage(cm, convRaster, false, null);
    }

    public org.apache.sis.coverage.grid.GridCoverage forConvertedValues(boolean converted) {
        return converted ? this : coverage;
    }

    static final class ConvertedSampleModel extends SampleModel {

        private final SampleModel base;
        private final int baseDataType;
        private final MathTransform1D[] bandTransforms;
        private final MathTransform1D[] bandIvtransforms;
        private final MathTransform pixelTransform;
        private final MathTransform pixelIvTransform;

        public ConvertedSampleModel(SampleModel base, MathTransform1D[] transforms, MathTransform1D[] ivtransforms) {
            super(DataBuffer.TYPE_FLOAT, base.getWidth(), base.getHeight(), base.getNumBands());
            this.base = base;
            this.baseDataType = base.getDataType();
            this.bandTransforms = transforms;
            this.bandIvtransforms = ivtransforms;
            this.pixelTransform = MathTransforms.compound(bandTransforms);
            this.pixelIvTransform = MathTransforms.compound(bandIvtransforms);
        }

        @Override
        public int getNumDataElements() {
            return base.getNumDataElements();
        }

        @Override
        public Object getDataElements(int x, int y, Object obj, DataBuffer data) {
            Object buffer = base.getDataElements(x, y, null, data);
            float[] pixel;
            if (obj == null) {
                pixel = new float[numBands];
            } else if (!(obj instanceof float[])) {
                throw new ClassCastException("Unsupported array type, expecting a float array.");
            } else {
                pixel = (float[]) obj;
            }

            switch (baseDataType) {
                case DataBuffer.TYPE_BYTE : {
                    final byte[] b = (byte[]) buffer;
                    for(int i=0;i<b.length;i++) pixel[i] = b[i];
                    } break;
                case DataBuffer.TYPE_SHORT : {
                    final short[] b = (short[]) buffer;
                    for(int i=0;i<b.length;i++) pixel[i] = b[i];
                    } break;
                case DataBuffer.TYPE_USHORT : {
                    final short[] b = (short[]) buffer;
                    for(int i=0;i<b.length;i++) pixel[i] = b[i] & 0xFFFF;
                    } break;
                case DataBuffer.TYPE_INT : {
                    final int[] b = (int[]) buffer;
                    for(int i=0;i<b.length;i++) pixel[i] = b[i];
                    } break;
                case DataBuffer.TYPE_FLOAT : {
                    final float[] b = (float[]) buffer;
                    for(int i=0;i<b.length;i++) pixel[i] = b[i];
                    } break;
                case DataBuffer.TYPE_DOUBLE : {
                    final double[] b = (double[]) buffer;
                    for(int i=0;i<b.length;i++) pixel[i] = (float) b[i];
                    } break;
                default: {
                    throw new ClassCastException("Unsupported base array type.");
                    }
            }

            try {
                pixelTransform.transform(pixel, 0, pixel, 0, 1);
            } catch (TransformException ex) {
                Arrays.fill(pixel, Float.NaN);
            }
            return pixel;
        }

        @Override
        public void setDataElements(int x, int y, Object obj, DataBuffer data) {
            float[] pixel;
            if (obj == null) {
                throw new ClassCastException("Null array values");
            } else if (!(obj instanceof float[])) {
                throw new ClassCastException("Unsupported array type, expecting a float array.");
            } else {
                pixel = (float[]) obj;
            }

            try {
                pixelIvTransform.transform(pixel, 0, pixel, 0, 1);
            } catch (TransformException ex) {
                Arrays.fill(pixel, Float.NaN);
            }

            switch (baseDataType) {
                case DataBuffer.TYPE_BYTE : {
                    final byte[] b = new byte[pixel.length];
                    for(int i=0;i<b.length;i++) b[i] = (byte) pixel[i];
                    base.setDataElements(x, y, b, data);
                    } break;
                case DataBuffer.TYPE_SHORT : {
                    final short[] b = new short[pixel.length];
                    for(int i=0;i<b.length;i++) b[i] = (short) pixel[i];
                    base.setDataElements(x, y, b, data);
                    } break;
                case DataBuffer.TYPE_USHORT : {
                    final short[] b = new short[pixel.length];
                    for(int i=0;i<b.length;i++) b[i] = (short) pixel[i];
                    base.setDataElements(x, y, b, data);
                    } break;
                case DataBuffer.TYPE_INT : {
                    final int[] b = new int[pixel.length];
                    for(int i=0;i<b.length;i++) b[i] = (int) pixel[i];
                    base.setDataElements(x, y, b, data);
                    } break;
                case DataBuffer.TYPE_FLOAT : {
                    base.setDataElements(x, y, pixel, data);
                    } break;
                case DataBuffer.TYPE_DOUBLE : {
                    final double[] b = new double[pixel.length];
                    for(int i=0;i<b.length;i++) b[i] = pixel[i];
                    base.setDataElements(x, y, b, data);
                    } break;
                default: {
                    throw new ClassCastException("Unsupported base array type.");
                    }
            }
        }

        @Override
        public int getSample(int x, int y, int b, DataBuffer data) {
            return (int) getSampleDouble(x, y, b, data);
        }

        @Override
        public float getSampleFloat(int x, int y, int b, DataBuffer data) {
            try {
                return (float) bandTransforms[b].transform(base.getSampleFloat(x, y, b, data));
            } catch (TransformException ex) {
                return Float.NaN;
            }
        }

        @Override
        public double getSampleDouble(int x, int y, int b, DataBuffer data) {
            try {
                return bandTransforms[b].transform(base.getSampleDouble(x, y, b, data));
            } catch (TransformException ex) {
                return Double.NaN;
            }
        }

        @Override
        public void setSample(int x, int y, int b, int s, DataBuffer data) {
            setSample(x,y,b, (double)s, data);
        }

        @Override
        public void setSample(int x, int y, int b, double s, DataBuffer data) {
            try {
                s = bandIvtransforms[b].transform(s);
            } catch (TransformException ex) {
                s = Double.NaN;
            }
            base.setSample(x, y, b, s, data);
        }

        @Override
        public void setSample(int x, int y, int b, float s, DataBuffer data) {
            setSample(x, y, b, (double)s, data);
        }

        @Override
        public SampleModel createCompatibleSampleModel(int w, int h) {
            final SampleModel cp = base.createCompatibleSampleModel(w, h);
            return new ConvertedSampleModel(cp, bandTransforms, bandIvtransforms);
        }

        @Override
        public SampleModel createSubsetSampleModel(int[] bands) {
            final SampleModel cp = base.createSubsetSampleModel(bands);
            final MathTransform1D[] trs = new MathTransform1D[bands.length];
            final MathTransform1D[] ivtrs = new MathTransform1D[bands.length];
            for (int i=0; i<bands.length;i++) {
                trs[i] = bandTransforms[bands[i]];
                ivtrs[i] = bandIvtransforms[bands[i]];
            }
            return new ConvertedSampleModel(cp, trs, ivtrs);
        }

        @Override
        public DataBuffer createDataBuffer() {
            return base.createDataBuffer();
        }

        @Override
        public int[] getSampleSize() {
            final int[] sizes = new int[numBands];
            Arrays.fill(sizes, 32);
            return sizes;
        }

        @Override
        public int getSampleSize(int band) {
            return 32;
        }

    }

    static final class ConvertedColorModel extends ColorModel {

        private final float scale;
        private final float offset;

        /**
         * @param nbbits
         * @param fct : Interpolate or Categorize function
         */
        public ConvertedColorModel(final int nbbits, final NumberRange range){
            super(nbbits);
            final double scale  = (255.0) / (range.getMaxDouble() - range.getMinDouble());
            this.scale  = (float) scale;
            this.offset = (float) (range.getMinDouble() / scale);
        }

        @Override
        public boolean isCompatibleRaster(Raster raster) {
            return true;
        }

        @Override
        public boolean isCompatibleSampleModel(SampleModel sm) {
            return true;
        }

        @Override
        public int getRGB(Object inData) {
            float value;
            // Most used cases. Compatible color model is designed for cases where indexColorModel cannot do the job (float or int samples).
            if (inData instanceof float[]) {
                value = ((float[]) inData)[0];
            } else if (inData instanceof int[]) {
                value = ((int[]) inData)[0];
            } else if (inData instanceof double[]) {
                value = (float) ((double[]) inData)[0];
            } else if (inData instanceof byte[]) {
                value = ((byte[]) inData)[0];
            } else if (inData instanceof short[]) {
                value = ((short[]) inData)[0];
            } else if (inData instanceof long[]) {
                value = ((long[]) inData)[0];
            } else if (inData instanceof Number[]) {
                value = ((Number[]) inData)[0].floatValue();
            } else if (inData instanceof Byte[]) {
                value = ((Byte[]) inData)[0];
            } else {
                value = 0.0f;
            }

            int c = (int) ((value - offset) * scale);
            if (c < 0) c = 0;
            else if (c > 255) c = 255;

            return (255 << 24) | (c << 16) | (c << 8) | c;
        }

        @Override
        public int getRed(int pixel) {
            final int argb = getRGB((Object)pixel);
            return 0xFF & (argb >> 16);
        }

        @Override
        public int getGreen(int pixel) {
            final int argb = getRGB((Object)pixel);
            return 0xFF & ( argb >> 8);
        }

        @Override
        public int getBlue(int pixel) {
            final int argb = getRGB((Object)pixel);
            return 0xFF & ( argb >> 0);
        }

        @Override
        public int getAlpha(int pixel) {
            final int argb = getRGB((Object)pixel);
            return 0xFF & ( argb >> 24);
        }

        @Override
        public int getRed(Object pixel) {
            final int argb = getRGB((Object)pixel);
            return 0xFF & (argb >> 16);
        }

        @Override
        public int getGreen(Object pixel) {
            final int argb = getRGB((Object)pixel);
            return 0xFF & ( argb >> 8);
        }

        @Override
        public int getBlue(Object pixel) {
            final int argb = getRGB((Object)pixel);
            return 0xFF & ( argb >> 0);
        }

        @Override
        public int getAlpha(Object pixel) {
            final int argb = getRGB((Object)pixel);
            return 0xFF & ( argb >> 24);
        }

        @Override
        public WritableRaster createCompatibleWritableRaster(int w, int h) {
            return Raster.createPackedRaster(new DataBufferInt(w*h),w,h,16,null);
        }

    }

}
