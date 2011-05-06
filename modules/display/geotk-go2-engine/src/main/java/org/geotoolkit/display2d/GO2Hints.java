/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Geomatys
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
import java.awt.image.ColorModel;

import org.geotoolkit.display.canvas.HintKey;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.lang.Static;

/**
 * Set of hints used by the Go2 Renderer
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class GO2Hints extends Static {

    private GO2Hints(){}

    private static class NamedKey extends Hints.Key{

        private final String name;

        private NamedKey(final Class c){
            this(c,null);
        }

        private NamedKey(final Class c, final String name){
            super(c);
            this.name = name;
        }

        @Override
        public String toString() {
            if(name != null){
                return name;
            }else{
                return super.toString();
            }
        }



    }

    /**
     * Configure the multithreading support. This usually makes the canvas
     * build several buffered images to render each layer separately.
     * This raises memory concumption.
     *
     * WARNING : experimental
     */
    public static final Key KEY_MULTI_THREAD = new NamedKey(Boolean.class, "GO2 - MultiThread");

    /**
     * Configure the generalization, false by default in stateless mode, true
     * in statefull mode.
     */
    public static final Key KEY_GENERALIZE = new NamedKey(Boolean.class, "GO2 - Generalize");

    /**
     * Configure the generalize effect, in display unit (pixel most of the time).
     * Values near 1 have the most efficient performance/quality ratio.
     */
    public static final Key KEY_GENERALIZE_FACTOR = new NamedKey(Number.class, "GO2 - Generalize factor");

    /**
     * Configure the go2 engine to use JTS or ISO geometries.
     * Default is ISO.
     */
    public static final Key KEY_GEOMETRY_BINDING = new NamedKey(String.class, "GO2 - Geometry binding");

    /**
     * Configure the go2 engine to render in order symbolizer then feature.
     * This hint usualy given much better rendered image but is more costly to
     * produce in stateless mode.
     * Default value is False : Feature priority
     */
    public static final Key KEY_SYMBOL_RENDERING_ORDER = new NamedKey(Boolean.class, "GO2 - Symbol rendering order");

    /**
     * Configure the label renderer used.
     * The default label renderer is Straight forward and doesn't make any overlaping check or anything,
     * it fallows exactly the Symbology encoding specification.
     * If you need a better implementation using a different placement/rendering algorithm
     * then you can provide the label renderer with this hint.
     * The given class must be an instance of org.geotoolkit.display2d.style.labeling.LabelRenderer
     */
    public static final Key KEY_LABEL_RENDERER_CLASS = new NamedKey(Class.class, "GO2 - Label Renderer");

     /**
     * Configure the go2 engine use the given DPI.
     * Default dpi is 90.
     */
    public static final Key KEY_DPI = new HintKey(7, Float.class);

    /**
     * Force the canvas to use the given color model.
     * This only works with the J2DBufferedCanvas.
     */
    public static final Key KEY_COLOR_MODEL = new NamedKey(ColorModel.class, "GO2 - ColorModel");

    /**
     * Ask the portrayal service to use the grid coverage writer when possible.
     * This can significantly improve performances when there is only one coverage layer
     * and no special parameters.
     * This is used only when asked to write directly in an output.
     *
     * Default value is false.
     */
    public static final Key KEY_COVERAGE_WRITER = new NamedKey(Boolean.class, "GO2 - Coverage writer");

    /**
     * When the symbol rendering order is actuvated it requieres several painting
     * pathes to make the image. this can be reduced by used a buffered image for
     * each symbol, this consume N time more memory but can have significant performance
     * benefit.
     *
     * Default value is false.
     */
    public static final Key KEY_PARALLAL_BUFFER = new NamedKey(Boolean.class, "GO2 - Parallal Buffer");

    public static final Boolean MULTI_THREAD_ON = Boolean.TRUE;
    public static final Boolean MULTI_THREAD_OFF = Boolean.FALSE;
    public static final Boolean GENERALIZE_ON = Boolean.TRUE;
    public static final Boolean GENERALIZE_OFF = Boolean.FALSE;
    public static final Boolean SYMBOL_RENDERING_PRIME = Boolean.TRUE;
    public static final Boolean SYMBOL_RENDERING_SECOND = Boolean.FALSE;
    public static final Boolean COVERAGE_WRITER_ON = Boolean.TRUE;
    public static final Boolean COVERAGE_WRITER_OFF = Boolean.FALSE;
    public static final Boolean PARALLAL_BUFFER_ON = Boolean.TRUE;
    public static final Boolean PARALLAL_BUFFER_OFF = Boolean.FALSE;

    /**
     * A value of 1.3 looks like the best average generalisation.
     * Values between 1 and 2.5 give a fair rendering.
     * Going under 1 doesnt bring much more details
     * going above 2.5 makes small geometries disapear or look sharp.
     *
     * Factor is adjusted using the DPI value.
     * (90/DPI) * GENERALIZE FACTOR
     */
    public static final Number GENERALIZE_FACTOR_DEFAULT = Float.valueOf(1.3f);
}
