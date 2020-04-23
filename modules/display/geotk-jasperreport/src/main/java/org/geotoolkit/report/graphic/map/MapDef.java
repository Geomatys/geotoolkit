/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.report.graphic.map;

import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.OutputDef;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.report.RenderableField;
import org.geotoolkit.util.StringUtilities;

/**
 * Regroup all parameters that define a map area on the jasper report template.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class MapDef extends RenderableField {

    private CanvasDef canvasDef;
    private SceneDef sceneDef;
    private OutputDef outputDef;

    public MapDef() {
        this(null,null,null);
    }

    public MapDef(final CanvasDef canvasDef, final SceneDef sceneDef, final OutputDef outputDef) {
        this.canvasDef = (canvasDef!=null)? canvasDef : new CanvasDef();
        this.sceneDef = (sceneDef!=null)? sceneDef : new SceneDef();
        this.outputDef = (outputDef!=null)? outputDef : new OutputDef("none", new Object(){});
    }

    public CanvasDef getCanvasDef() {
        return canvasDef;
    }

    public void setCanvasDef(final CanvasDef canvasDef) {
        this.canvasDef = canvasDef;
    }

    public SceneDef getSceneDef() {
        return sceneDef;
    }

    public void setSceneDef(final SceneDef sceneDef) {
        this.sceneDef = sceneDef;
    }

    public OutputDef getOutputDef() {
        return outputDef;
    }

    public void setOutputDef(final OutputDef outputDef) {
        this.outputDef = outputDef;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("MapFieldDef");
        builder.append(StringUtilities.toStringTree(canvasDef,sceneDef,outputDef));
        return builder.toString();
    }

}
