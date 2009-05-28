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

/**
 * Set of hints used by the Go2 Renderer
 *
 * @author Johann Sorel (Geomatys)
 */
public class GO2Hints {

    public static final Key KEY_MULTI_THREAD           = new HintKey(1, Boolean.class);
    public static final Key KEY_GENERALIZE             = new HintKey(2, Boolean.class);
    public static final Key KEY_GENERALIZE_FACTOR      = new HintKey(3, Number.class);

    /**
     * Configure the go2 engine to render in order symbolizer then feature.
     * This hint usualy given much better rendered image but is more costly to
     * produce in stateless mode.
     * Default value is False : Feature priority
     */
    public static final Key KEY_SYMBOL_RENDERING_ORDER = new HintKey(5, Boolean.class);

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
     */
    public static final Number GENERALIZE_FACTOR_DEFAULT = new Float(1.3f);
}
