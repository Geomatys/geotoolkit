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
package org.geotoolkit.display2d.container.fx;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Color;
import java.awt.Paint;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import org.apache.sis.feature.FeatureExt;
import org.geotoolkit.display2d.style.CachedLineSymbolizer;
import org.geotoolkit.display2d.style.CachedStroke;
import org.geotoolkit.display2d.style.CachedStrokeGraphic;
import org.geotoolkit.display2d.style.CachedStrokeSimple;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gui.javafx.render2d.shape.FXGeometry;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.Loggers;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.LineSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLineSymbolizer extends Group {

    private final FXFeature feature;
    private final CachedLineSymbolizer symbolizer;

    public FXLineSymbolizer(FXFeature feature, CachedLineSymbolizer symbolizer) {
        setCache(false);
        this.feature = feature;
        this.symbolizer = symbolizer;
        this.feature.context.objToDisp.addListener(this::changed);
        updateGraphic();
    }

    private void changed(ObservableValue observable, Object oldValue, Object newValue){
        updateGraphic();
    }

    private Geometry transform(Geometry geom){
        try{
            return JTS.transform(geom, feature.context.objToDisp.get());
        }catch(TransformException ex){
            Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
            return null;
        }
    }

    private void updateGraphic(){

        final LineSymbolizer base = symbolizer.getSource();
        final Geometry geom = transform((Geometry)FeatureExt.getDefaultGeometryAttributeValue(feature.feature));
        if(geom==null){
            getChildren().clear();
            return;
        }

        final CachedStroke cstroke = symbolizer.getCachedStroke();
        if(cstroke instanceof CachedStrokeSimple){
            final CachedStrokeSimple css = (CachedStrokeSimple) cstroke;
            final float strokeWidth = css.getStrokeWidth(feature);
            final Paint paint = css.getJ2DPaint(feature, 0, 0, 1, null);
            if(paint instanceof Color){
                final FXGeometry fxgeom = new FXGeometry(geom);
                fxgeom.setStrokeWidth(strokeWidth);
                fxgeom.setStroke(FXUtilities.toFxColor((Color)paint));
                getChildren().setAll(fxgeom);
            }

        }else if(cstroke instanceof CachedStrokeGraphic){

        }

    }


}
