/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.display2d.ext.isoline.symbolizer;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.style.*;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.geotoolkit.process.coverage.isoline2.IsolineDescriptor2;
import org.geotoolkit.style.function.Categorize;
import org.geotoolkit.style.function.Interpolate;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Jenks;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.style.ColorMap;
import org.opengis.style.RasterSymbolizer;
import org.opengis.util.NoSuchIdentifierException;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class CachedIsolineSymbolizer extends CachedSymbolizer<IsolineSymbolizer> {

    private static final Logger LOGGER = Logging.getLogger(CachedIsolineSymbolizer.class);

    private CachedRasterSymbolizer cachedRS;
    private ProcessDescriptor isolineDesc;
    private double[] steps = null;
    private boolean dynamicColorMap = false;

    public CachedIsolineSymbolizer(IsolineSymbolizer symbol, SymbolizerRendererService service) {
        super(symbol, service);

        RasterSymbolizer rasterSymbolizer = styleElement.getRasterSymbolizer();
        if (rasterSymbolizer != null) {
            this.cachedRS = (CachedRasterSymbolizer) GO2Utilities.getCached(rasterSymbolizer, null);
            this.steps = extractSteps(rasterSymbolizer);
        } else {
            this.cachedRS = null;
        }

        try {
            this.isolineDesc = ProcessFinder.getProcessDescriptor(CoverageProcessingRegistry.NAME, IsolineDescriptor2.NAME);
        } catch (NoSuchIdentifierException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getMargin(Object candidate, float coeff) {
        return cachedRS.getMargin(candidate, coeff);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void evaluate() {
        if(!isNotEvaluated) return;

        if (cachedRS != null) {
            cachedRS.evaluate();
        }

        isNotEvaluated = false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isStatic(){
        return cachedRS.isStatic();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public VisibilityState isStaticVisible(){
        return cachedRS.isStaticVisible();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isVisible(Object candidate) {
        return cachedRS.isVisible(candidate);
    }

    public CachedRasterSymbolizer getCachedRasterSymbolizer() {
        return cachedRS;
    }

    public ProcessDescriptor getIsolineDesc() {
        return isolineDesc;
    }

    public double[] getSteps() {
        if (!dynamicColorMap && steps != null) {
            return steps;
        } else {
            if (getCachedRasterSymbolizer() != null) {
                return extractSteps(getCachedRasterSymbolizer().getSource());
            }
        }
        return new double[0];
    }

    /**
     * Extract isolines steps from RasterSymbolizer ColorMap.
     * @param rasterSymbolizer
     * @return
     */
    private double[] extractSteps(RasterSymbolizer rasterSymbolizer) {

        Set<Double> steps = new HashSet<Double>();

        if (rasterSymbolizer != null && rasterSymbolizer.getColorMap() != null) {
            ColorMap colorMap = rasterSymbolizer.getColorMap();
            Function function = colorMap.getFunction();

            if (function instanceof Interpolate) {
                Interpolate interpolate = (Interpolate) function;
                List<InterpolationPoint> points = interpolate.getInterpolationPoints();
                for (InterpolationPoint point : points) {
                    steps.add(point.getData().doubleValue());
                }
                dynamicColorMap = false;

            } else if (function instanceof Categorize) {
                Categorize categorize = (Categorize) function;
                Map<Expression, Expression> thresholds = categorize.getThresholds();
                for (Map.Entry<Expression, Expression> entry : thresholds.entrySet()) {
                    Expression key = entry.getKey();
                    Double step = key.evaluate(null, Double.class);
                    if (step!= null && !step.isNaN() && !step.isInfinite()) {
                        steps.add(step);
                    }
                }
                dynamicColorMap = false;

            } else if (function instanceof Jenks) {
                Jenks jenks = (Jenks) function;
                Map<Double, Color> jenksColorMap = jenks.getColorMap();
                if (jenksColorMap != null) {
                    for (Double jenksStep : jenksColorMap.keySet()) {
                        if (jenksStep != null && !jenksStep.isNaN()) {
                            steps.add(jenksStep);
                        }
                    }
                }
                dynamicColorMap = true;
            }
        }

        int i = 0;
        Iterator<Double> iterator = steps.iterator();
        double[] stepsArray = new double[steps.size()];
        while (iterator.hasNext()) {
            stepsArray[i++] = iterator.next();
        }

        return stepsArray;
    }
}
