/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

package org.geotoolkit.gui.javafx.render2d.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.text.NumberFormat;
import java.util.logging.Level;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import org.apache.sis.measure.Units;
import javax.swing.SwingConstants;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.ext.DecorationXMLParser;
import org.geotoolkit.display2d.ext.DefaultBackgroundTemplate;
import org.geotoolkit.display2d.ext.legend.DefaultLegendTemplate;
import org.geotoolkit.display2d.ext.northarrow.DefaultNorthArrowTemplate;
import org.geotoolkit.display2d.ext.northarrow.GraphicNorthArrowJ2D;
import org.geotoolkit.display2d.ext.northarrow.NorthArrowTemplate;
import org.geotoolkit.display2d.ext.scalebar.DefaultScaleBarTemplate;
import org.geotoolkit.display2d.ext.scalebar.GraphicScaleBarJ2D;
import org.geotoolkit.display2d.ext.scalebar.ScaleBarTemplate;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.OutputDef;
import org.geotoolkit.display2d.service.PortrayalExtension;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.FXMapAction;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXQuickPrintContextAction extends FXMapAction {

    public static final Image ICON_PRINT = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_PRINT,16,FontAwesomeIcons.DEFAULT_COLOR),null);

    private static final DefaultLegendTemplate LEGEND_TEMPLATE = new DefaultLegendTemplate(
            new DefaultBackgroundTemplate( //legend background
                    new BasicStroke(1), //stroke
                    Color.LIGHT_GRAY, //stroke paint
                    Color.WHITE, // fill paint
                    new Insets(10, 10, 10, 10), //border margins
                    8 //round border
            ),
            2, //gap between legend elements
            null, //glyph size, we can let it to null for the legend to use the best size
            new Font("Serial", Font.PLAIN, 11), //Font used for style rules
            true, // show layer names
            new Font("Serial", Font.BOLD, 13), //Font used for layer names
            true // display only visible layers
    );
    private static final ScaleBarTemplate SCALEBAR_KILOMETER_TEMPLATE = new DefaultScaleBarTemplate(
                            new DefaultBackgroundTemplate(
                                    new BasicStroke(1),
                                    Color.LIGHT_GRAY,
                                    new Color(255,255,255),
                                    new Insets(6, 6, 6, 6), 0),
                            new Dimension(250,40),10,
                            false, 4, NumberFormat.getNumberInstance(),
                            Color.DARK_GRAY, Color.GRAY, Color.WHITE,
                            10,true,false, new Font("Serial", Font.BOLD, 10),true,
                            Units.KILOMETRE);
    private static final ScaleBarTemplate SCALEBAR_METER_TEMPLATE = new DefaultScaleBarTemplate(
                            new DefaultBackgroundTemplate(
                                    new BasicStroke(1),
                                    Color.LIGHT_GRAY,
                                    new Color(255,255,255),
                                    new Insets(6, 6, 6, 6), 0),
                            new Dimension(50,40),10,
                            false, 4, NumberFormat.getNumberInstance(),
                            Color.DARK_GRAY, Color.GRAY, Color.WHITE,
                            10,true,false, new Font("Serial", Font.BOLD, 10),true,
                            Units.METRE);
    private static final NorthArrowTemplate NORTH_ARROW_TEMPLATE = new DefaultNorthArrowTemplate(
                    null,
                    DecorationXMLParser.class.getResource("/org/geotoolkit/icon/boussole.svg"),
                    new Dimension(100,100));


    public FXQuickPrintContextAction(FXMap map) {
        super(map,GeotkFX.getString(FXQuickPrintContextAction.class,"label"),
                GeotkFX.getString(FXQuickPrintContextAction.class,"label"),ICON_PRINT);
    }

    @Override
    public void accept(ActionEvent event) {
        if(map==null) return;

        try {
            final FileChooser.ExtensionFilter filterPng = new FileChooser.ExtensionFilter("Image PNG", "*.png");
            final FileChooser.ExtensionFilter filterDvg = new FileChooser.ExtensionFilter("Image SVG", "*.svg");
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(GeotkFX.getString(FXQuickPrintContextAction.class,"label"));
            fileChooser.getExtensionFilters().add(filterPng);
            fileChooser.getExtensionFilters().add(filterDvg);
            fileChooser.setSelectedExtensionFilter(filterPng);
            final File docFile = fileChooser.showSaveDialog(map.getScene().getWindow());


            final Rectangle2D dispSize = map.getCanvas().getDisplayBounds();

            final Hints hints = new Hints();
            hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            final PortrayalExtension ext = new PortrayalExtension() {
                @Override
                public void completeCanvas(J2DCanvas canvas) throws PortrayalException {
                    final GraphicScaleBarJ2D graphicScaleBarJ2D = new GraphicScaleBarJ2D(canvas);
                    graphicScaleBarJ2D.setPosition(SwingConstants.SOUTH_WEST);
                    final GraphicNorthArrowJ2D northArrowJ2D = new GraphicNorthArrowJ2D(canvas,NORTH_ARROW_TEMPLATE);
                    northArrowJ2D.setPosition(SwingConstants.NORTH_WEST);

                    try{
                        final double span = canvas.getVisibleEnvelope2D().getSpan(0);
                        if(span>5000){
                             graphicScaleBarJ2D.setTemplate(SCALEBAR_KILOMETER_TEMPLATE);
                        }else{
                             graphicScaleBarJ2D.setTemplate(SCALEBAR_METER_TEMPLATE);
                        }
                    }catch(Exception ex){/*not important*/}

                    canvas.getContainer().getRoot().getChildren().add(graphicScaleBarJ2D);
                    canvas.getContainer().getRoot().getChildren().add(northArrowJ2D);
                }
            };

            final String mimetype = fileChooser.getSelectedExtensionFilter()==filterPng ? "image/png" : "image/svg+xml";

            final CanvasDef cdef = new CanvasDef(new Dimension((int)dispSize.getWidth(),(int)dispSize.getHeight()),
                    "image/svg+xml".equals(mimetype) ? new Color(1, 1, 1, 1) : new Color(0, 0, 0, 0));
            final SceneDef sdef = new SceneDef(map.getContainer().getContext(),hints,ext);
            final ViewDef vdef = new ViewDef(map.getCanvas().getVisibleEnvelope());
            final OutputDef odef = new OutputDef(mimetype, docFile);
            DefaultPortrayalService.portray(cdef, sdef, vdef, odef);


        } catch (PortrayalException ex) {
            Loggers.DATA.log(Level.WARNING, ex.getMessage(), ex);
        }

    }

}
