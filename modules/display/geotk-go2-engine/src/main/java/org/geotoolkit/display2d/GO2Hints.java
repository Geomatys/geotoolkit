/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d;

import java.awt.RenderingHints.Key;
import org.geotoolkit.display.canvas.HintKey;
import org.geotoolkit.lang.Static;

/**
 * Set of hints used by the Go2 Renderer
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@Static
public final class GO2Hints {

    private GO2Hints(){}

    /**
     * Configure the multithreading support. This usually makes the canvas
     * build several buffered images to render each layer separately.
     * This raises memory concumption.
     * WARNING, not implemented yet in the Go2 engine.
     */
    public static final Key KEY_MULTI_THREAD = new HintKey(1, Boolean.class);

    /**
     * Configure the generalization, false by default in stateless mode, true
     * in statefull mode.
     */
    public static final Key KEY_GENERALIZE = new HintKey(2, Boolean.class);

    /**
     * Configure the generalize effect, in display unit (pixel most of the time).
     * Values near 1 have the most efficient performance/quality ratio.
     */
    public static final Key KEY_GENERALIZE_FACTOR = new HintKey(3, Number.class);

    /**
     * Configure the go2 engine to use JTS or ISO geometries.
     * Default is ISO.
     */
    public static final Key KEY_GEOMETRY_BINDING = new HintKey(4, String.class);

    /**
     * Configure the go2 engine to render in order symbolizer then feature.
     * This hint usualy given much better rendered image but is more costly to
     * produce in stateless mode.
     * Default value is False : Feature priority
     */
    public static final Key KEY_SYMBOL_RENDERING_ORDER = new HintKey(5, Boolean.class);

    /**
     * Configure the label renderer used.
     * The default label renderer is Straight forward and doesn't make any overlaping check or anything,
     * it fallows exactly the Symbology encoding specification.
     * If you need a better implementation using a different placement/rendering algorithm
     * then you can provide the label renderer with this hint.
     * The given class must be an instance of org.geotoolkit.display2d.style.labeling.LabelRenderer
     */
    public static final Key KEY_LABEL_RENDERER_CLASS = new HintKey(6, Class.class);

    /**
     * Configure the go2 engine use the given DPI.
     * Default dpi is 90.
     */
    public static final Key KEY_DPI = new HintKey(7, Float.class);

    public static final Boolean MULTI_THREAD_ON = Boolean.TRUE;
    public static final Boolean MULTI_THREAD_OFF = Boolean.FALSE;
    public static final Boolean GENERALIZE_ON = Boolean.TRUE;
    public static final Boolean GENERALIZE_OFF = Boolean.FALSE;
    public static final Boolean SYMBOL_RENDERING_PRIME = Boolean.TRUE;
    public static final Boolean SYMBOL_RENDERING_SECOND = Boolean.FALSE;

    /**
     * A value of 1.3 looks like the best average generalisation.
     * Values between 1 and 2.5 give a fair rendering.
     * Going under 1 doesnt bring much more details 
     * going above 2.5 makes small geometries disapear or look sharp.
     *
     * Factor is adjusted using the DPI value.
     * (90/DPI) * GENERALIZE FACTOR
     */
    public static final Number GENERALIZE_FACTOR_DEFAULT = new Float(1.3f);
}
