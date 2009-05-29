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
package org.geotoolkit.display2d.primitive;

import org.geotoolkit.display.primitive.*;
import java.awt.Graphics2D;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Base class for Geotools implementations of {@link org.opengis.go.display.primitive.Graphic}
 * primitives. This implementation is designed for use with
 * {@link org.geotools.display.canvas.BufferedCanvas2D}.
 *
 * @since 2.3
 * @source $URL: http://svn.geotools.org/trunk/modules/unsupported/go/src/main/java/org/geotools/display/primitive/GraphicPrimitive2D.java $
 * @version $Id: GraphicPrimitive2D.java 30380 2008-05-22 14:53:34Z johann.sorel $
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 */
public abstract class GraphicJ2D extends ReferencedGraphic2D {
    /**
     * Constructs a new graphic using the specified objective CRS.
     *
     * @param  crs The objective coordinate reference system.
     * @throws IllegalArgumentException if {@code crs} is null or has an incompatible number of
     *         dimensions.
     */
    protected GraphicJ2D(final ReferencedCanvas2D canvas,final CoordinateReferenceSystem crs)
            throws IllegalArgumentException{
        super(canvas,crs);
    }
    
    /**
     * Paints this graphic. This method is invoked by
     * {@link org.geotools.display.canvas.BufferedCanvas2D} every time this graphic needs to be
     * repainted. Implementations are responsible for transformations from their own underlying
     * data CRS to the {@linkplain RenderingContext#displayCRS display CRS} if needed. The
     * {@link RenderingContext} object provides informations for such transformations:
     *
     * <ul>
     * <li><p><b><code>context.{@link RenderingContext#getMathTransform getMathTransform}(
     *        graphicCRS,
     *        context.{@link RenderingContext#objectiveCRS objectiveCRS} )</code></b><br>
     *     Returns a transform from the underlying CRS to the rendering CRS.</p></li>
     *
     * <li><p><b><code>context.{@link RenderingContext#getMathTransform getMathTransform}(
     *        context.{@link RenderingContext#objectiveCRS objectiveCRS},
     *        context.{@link RenderingContext#displayCRS displayCRS} )</code></b><br>
     *     Returns a transform from the rendering CRS to the <cite>Java2D</cite> CRS in
     *     "dots" units (usually 1/72 of inch). This transformation is zoom dependent.</p></li>
     * </ul>
     *
     * By default, painting is done in the <cite>Java2D</cite> user space (a.k.a.
     * {@linkplain RenderingContext#displayCRS display CRS}. However, the CRS can
     * easily be switched. An implementation may looks like as below:
     *
     * <blockquote><code>
     * {@linkplain java.awt.Graphics2D} graphics =
     * context.{@linkplain RenderingContext#getGraphics getGraphics()};<br>
     *
     * context.{@linkplain RenderingContext#setGraphicsCRS setGraphicsCRS}(context.{@linkplain
     * RenderingContext#objectiveCRS objectiveCRS});<br>
     *
     * // </code>NOTE: Skip the above line if you want to perform the rendering in<code><br>
     *
     * // </code>the default <cite>Java2D</cite> user space (a.k.a.
     * {@link RenderingContext#displayCRS displayCRS}) rather than<code><br>
     *
     * // </code>in terms of "real world" units.<code><br>
     *
     * try {<br>
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * // </code><cite>Paint here map features in geographic coordinates (usually meters or
     * angular degrees).</cite><code><br>
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * context.{@linkplain RenderingContext#addPaintedArea addPaintedArea}(...);<br>
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * context.{@linkplain RenderingContext#setGraphicsCRS setGraphicsCRS}(context.{@linkplain
     * RenderingContext#displayCRS displayCRS});<br>
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * // </code><cite>Write here texts or labels. Coordinates are in <u>dots</u>.</cite><code><br>
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * context.{@linkplain RenderingContext#addPaintedArea addPaintedArea}(...);<br>
     *
     * } finally {<br>
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * // </code><cite>Restore here {@link Graphics2D} it its initial state.</cite><code><br>
     *
     * }<br>
     * </code></blockquote>
     *
     * During the rendering process, implementations are encouraged to declare a (potentially
     * approximative) bounding shape of their painted area with calls to
     * {@link RenderingContext#addPaintedArea(Shape)}. This is an optional operation: providing
     * those hints only help {@link org.geotools.display.canvas.BufferedCanvas2D} to speed up
     * future rendering and events processing.
     *
     * @param  context Information relatives to the rendering context. This object contains the
     *         {@link Graphics2D} to use and methods for getting {@link MathTransform} objects.
     */
    public abstract void paint(final RenderingContext2D context);

    public Object getUserObject(){
        return null;
    }
    
}
