/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.internal.coverage;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.parameter.Parameters;
import org.geotoolkit.coverage.AbstractCoverage;
import org.geotoolkit.coverage.Coverage;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.PointOutsideCoverageException;
import org.apache.sis.coverage.SampleDimension;
import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Coverage combining sample dimensions of two coverages.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CompoundCoverage extends AbstractCoverage {

    private final Coverage coverage1;
    private final Coverage coverage2;
    private final int nbSamples1;
    private final SampleDimension[] sampleDimensions;

    /**
     * Constructs a coverage combining sample dimensions from provided coverages.
     *
     * @param name
     *          The coverage name, or {@code null} if none.
     * @param coverages
     *          compound coverages
     * @param properties
     *          The set of properties for this coverage, or {@code null} if there is none.
     *          Keys are {@link String} objects ({@link javax.media.jai.util.CaselessStringKey}
     *          are accepted as well), while values may be any {@link Object}.
     * @return created coverage collection
     */
    public static Coverage create(CharSequence name, Map<?,?> properties, Coverage ... coverages) {
        if (coverages.length < 2) {
            throw new IllegalArgumentException("Must provide at least 2 coverage to make a compound");
        }
        Coverage cmpd = new CompoundCoverage(name, coverages[0], coverages[1], properties);
        for (int i=2;i<coverages.length;i++) {
            cmpd = new CompoundCoverage(name, cmpd, coverages[i], properties);
        }
        return cmpd;
    }

    private CompoundCoverage(CharSequence name, Coverage coverage1, Coverage coverage2, Map<?,?> properties) {
        super(name, coverage1.getCoordinateReferenceSystem(), null, properties);
        this.coverage1 = coverage1;
        this.coverage2 = coverage2;

        final List<? extends SampleDimension> samples1 = coverage1.getSampleDimensions();
        final List<? extends SampleDimension> samples2 = coverage2.getSampleDimensions();

        nbSamples1 = samples1.size();
        final int nbSamples2 = samples2.size();
        sampleDimensions = new SampleDimension[nbSamples1 + nbSamples2];
        for (int i=0; i<nbSamples1; i++) sampleDimensions[i] = samples1.get(i);
        for (int i=nbSamples1,k=0; i<sampleDimensions.length; i++,k++) sampleDimensions[i] = samples2.get(k);
    }

    @Override
    public Object evaluate(DirectPosition point) throws PointOutsideCoverageException, CannotEvaluateException {
        final double[] values = new double[sampleDimensions.length];
        Arrays.fill(values, Double.NaN);

        int nbFail = 0;
        try {
            final Object array = coverage1.evaluate(point);
            for (int i=0; i<nbSamples1; i++) {
                values[i] = Array.getDouble(array, i);
            }
        } catch (CannotEvaluateException ex) {
            nbFail++;
        }
        try {
            final Object array = coverage2.evaluate(point);
            for (int i=nbSamples1,k=0; i<values.length; i++,k++) {
                values[i] = Array.getDouble(array, k);
            }
        } catch (CannotEvaluateException ex) {
            nbFail++;
        }
        if (nbFail == 2) {
            throw new PointOutsideCoverageException();
        }

        return values;
    }

    @Override
    public double[] evaluate(DirectPosition coord, double[] dest) throws PointOutsideCoverageException, CannotEvaluateException {
        if (dest == null) dest = new double[sampleDimensions.length];
        Arrays.fill(dest, Double.NaN);

        int nbFail = 0;
        try {
            final double[] array = coverage1.evaluate(coord, (double[])null);
            for (int i=0; i<nbSamples1; i++) {
                dest[i] = array[i];
            }
        } catch (CannotEvaluateException ex) {
            nbFail++;
        }
        try {
            final double[] array = coverage2.evaluate(coord, (double[])null);
            for (int i=nbSamples1,k=0; i<dest.length; i++,k++) {
                dest[i] = array[k];
            }
        } catch (CannotEvaluateException ex) {
            nbFail++;
        }
        if (nbFail == 2) {
            throw new PointOutsideCoverageException();
        }
        return dest;
    }

    @Override
    public float[] evaluate(DirectPosition coord, float[] dest) throws PointOutsideCoverageException, CannotEvaluateException {
        if (dest == null) dest = new float[sampleDimensions.length];
        Arrays.fill(dest, Float.NaN);

        int nbFail = 0;
        try {
            final float[] array = coverage1.evaluate(coord, (float[])null);
            for (int i=0; i<nbSamples1; i++) {
                dest[i] = array[i];
            }
        } catch (CannotEvaluateException ex) {
            nbFail++;
        }
        try {
            final float[] array = coverage2.evaluate(coord, (float[])null);
            for (int i=nbSamples1,k=0; i<dest.length; i++,k++) {
                dest[i] = array[k];
            }
        } catch (CannotEvaluateException ex) {
            nbFail++;
        }
        if (nbFail == 2) {
            throw new PointOutsideCoverageException();
        }
        return dest;
    }

    @Override
    public List<? extends SampleDimension> getSampleDimensions() {
        return UnmodifiableArrayList.wrap(sampleDimensions);
    }

    @Override
    public RenderableImage getRenderableImage(int xAxis, int yAxis) {
        return new Renderable(xAxis, yAxis);
    }

    private class Renderable extends AbstractCoverage.Renderable {

        private final RenderableImage ri1;
        private final RenderableImage ri2;

        private Renderable(int xAxis, int yAxis) {
            super(xAxis,yAxis);
            ri1 = coverage1.getRenderableImage(xAxis, yAxis);
            ri2 = coverage2.getRenderableImage(xAxis, yAxis);
        }

        @Override
        public Vector<RenderableImage> getSources() {
            return new Vector(Arrays.asList(ri1,ri2));
        }

        @Override
        public RenderedImage createRendering(RenderContext renderContext) {
            final RenderedImage re1 = ri1.createRendering(renderContext);
            final RenderedImage re2 = ri2.createRendering(renderContext);

            try {
                final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("geotoolkit", "image:bandcombine");
                final Parameters input = Parameters.castOrWrap(desc.getInputDescriptor().createValue());
                input.parameter("images").setValue(new RenderedImage[]{re1,re2});
                final Process process = desc.createProcess(input);
                final ParameterValueGroup output = process.call();
                return (RenderedImage) output.parameter("result").getValue();

            }catch(Exception ex) {
                throw new RuntimeException(ex.getMessage(),ex);
            }
        }
    }

}
