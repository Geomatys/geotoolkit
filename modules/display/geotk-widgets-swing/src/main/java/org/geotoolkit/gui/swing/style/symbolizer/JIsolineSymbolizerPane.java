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

package org.geotoolkit.gui.swing.style.symbolizer;

import org.apache.sis.storage.DataStoreException;

import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.display2d.ext.isoline.symbolizer.IsolineSymbolizer;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.style.JColorMapPane;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.feature.FeatureType;
import org.opengis.style.*;

import javax.measure.unit.NonSI;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import org.geotoolkit.gui.swing.resource.MessageBundle;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * Swing editor for IsolineSymbolizer.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class JIsolineSymbolizerPane extends StyleElementEditor<IsolineSymbolizer> implements PropertyPane {

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

    private final JColorMapPane guiColorMapPane = new JColorMapPane();
    private final JLineSymbolizerPane guiLineSymbolizerPane = new JLineSymbolizerPane();
    private final JTextSymbolizerPane guiTextSymbolizerPane = new JTextSymbolizerPane();
    private final JCheckBox guiShowTextCB = new JCheckBox();
    private final JCheckBox guiIsolineOnlyCB = new JCheckBox();

    private MapLayer layer = null;
    private RasterSymbolizer oldRS;
    private FeatureMapLayer isolineMockLayer;

    public JIsolineSymbolizerPane() {
        super(IsolineSymbolizer.class);
        guiShowTextCB.setText(MessageBundle.format("isolineEditor_showLabel"));
        guiShowTextCB.setSelected(false);

        guiIsolineOnlyCB.setText(MessageBundle.format("isolineEditor_isolineOnly"));
        guiIsolineOnlyCB.setSelected(false);

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(guiShowTextCB);
        topPanel.add(guiIsolineOnlyCB);

        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.LEFT);
        tabbedPane.add(MessageBundle.format("isolineEditor_colormap"), guiColorMapPane);
        tabbedPane.add(MessageBundle.format("isolineEditor_line"), guiLineSymbolizerPane);
        tabbedPane.add(MessageBundle.format("isolineEditor_text"), guiTextSymbolizerPane);
        add(BorderLayout.NORTH, topPanel);
        add(BorderLayout.CENTER, tabbedPane);

        final PropertyChangeListener propListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                JIsolineSymbolizerPane.this.propertyChange(evt);
            }
        };

        guiShowTextCB.addPropertyChangeListener(propListener);
        guiIsolineOnlyCB.addPropertyChangeListener(propListener);
        guiColorMapPane.addPropertyChangeListener(propListener);
        guiLineSymbolizerPane.addPropertyChangeListener(propListener);
        guiTextSymbolizerPane.addPropertyChangeListener(propListener);
    }

    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        this.layer = layer;
        guiColorMapPane.setLayer(layer);

        //use isoline mock layer
        guiLineSymbolizerPane.setLayer(isolineMockLayer);
        guiTextSymbolizerPane.setLayer(isolineMockLayer);
    }

    @Override
    public void parse(IsolineSymbolizer target) {

        if (target != null) {
            this.oldRS = target.getRasterSymbolizer();
            guiColorMapPane.parse(target.getRasterSymbolizer());
            guiLineSymbolizerPane.parse(target.getLineSymbolizer());
            guiTextSymbolizerPane.parse(target.getTextSymbolizer());
            guiIsolineOnlyCB.setSelected(target.getIsolineOnly());
            guiShowTextCB.setSelected(target.getTextSymbolizer() != null);
        } else {
            RasterSymbolizer nullRasterSymbolizer = null;
            guiColorMapPane.parse(nullRasterSymbolizer);
            guiLineSymbolizerPane.parse(null);
            guiTextSymbolizerPane.parse(null);
            guiShowTextCB.setSelected(false);
            guiIsolineOnlyCB.setSelected(false);
        }
    }

    @Override
    public IsolineSymbolizer create() {

        //rebuild RasterSymbolizer from ColorMap
        final ColorMap colorMap = guiColorMapPane.create();
        final String name = (oldRS != null ? oldRS.getName() : "RasterSymbolizer");
        final ContrastEnhancement enchance = (oldRS != null ? oldRS.getContrastEnhancement() : DEFAULT_CONTRAST_ENHANCEMENT);
        final ShadedRelief relief = (oldRS != null ? oldRS.getShadedRelief() : DEFAULT_SHADED_RELIEF);
        final Description desc = (oldRS != null ? oldRS.getDescription() : DEFAULT_DESCRIPTION);
        final ChannelSelection selection = SF.channelSelection(
                SF.selectedChannelType(guiColorMapPane.getSelectedBand(),DEFAULT_CONTRAST_ENHANCEMENT));

        final RasterSymbolizer rasterSymbolizer = SF.rasterSymbolizer(
                name,DEFAULT_GEOM,desc, NonSI.PIXEL,LITERAL_ONE_FLOAT,
                selection, OverlapBehavior.LATEST_ON_TOP, colorMap, enchance, relief, null);

        final TextSymbolizer textSymbolizer = guiShowTextCB.isSelected() ? guiTextSymbolizerPane.create() : null;

        return new IsolineSymbolizer(
                rasterSymbolizer,
                guiLineSymbolizerPane.create(),
                textSymbolizer,
                guiIsolineOnlyCB.isSelected());
    }

    @Override
    protected Object[] getFirstColumnComponents() {
        return new Object[0];
    }

    @Override
    public boolean canHandle(Object candidate) {
        return super.canHandle(candidate) || (candidate instanceof CoverageMapLayer);
    }

    //keep track of where the symbolizer was to avoid rewriting the complete style
    private MutableRule parentRule = null;
    private int parentIndex = 0;

    @Override
    public void setTarget(Object candidate) {
        isolineMockLayer = null;
        if (candidate instanceof CoverageMapLayer) {
            try {
                final FeatureType ft = IsolineSymbolizer.buildIsolineType();
                isolineMockLayer = MapBuilder.createFeatureLayer(
                        FeatureStoreUtilities.collection("", (org.geotoolkit.feature.type.FeatureType) ft),
                        getStyleFactory().style());
            } catch (DataStoreException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(),ex);
            }
            setLayer((CoverageMapLayer) candidate);
            //search for a IsolineSymbolizer
            parentRule = null;
            parentIndex = 0;
            for(MutableFeatureTypeStyle fts : this.layer.getStyle().featureTypeStyles()){
                for(MutableRule r : fts.rules()){
                    for(int i=0,n=r.symbolizers().size();i<n;i++){
                        Symbolizer s = r.symbolizers().get(i);
                        if(s instanceof IsolineSymbolizer){
                            parse((IsolineSymbolizer)s);
                            parentRule = r;
                            parentIndex = i;
                            return;
                        }
                    }
                }
            }
            parse(null);
        }else if(candidate instanceof IsolineSymbolizer){
            parse((IsolineSymbolizer)candidate);
        }else{
            parse(null);
        }
    }

    @Override
    public void apply() {
        if(layer!=null){
            final IsolineSymbolizer symbol = create();

            if(parentRule!=null){
                parentRule.symbolizers().remove(parentIndex);
                parentRule.symbolizers().add(parentIndex,symbol);
            }else{
                //style did not exist, add a new feature type style for it
                final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
                final MutableRule rule = SF.rule(symbol);
                fts.rules().add(rule);
                fts.setDescription(SF.description("isoline", "isoline"));
                rule.setDescription(SF.description("isoline", "isoline"));
                layer.getStyle().featureTypeStyles().add(fts);
                parentRule = rule;
                parentIndex = 0;
            }
        }
    }

    private void propertyChange(PropertyChangeEvent evt) {
        if (PROPERTY_UPDATED.equalsIgnoreCase(evt.getPropertyName())) {
            firePropertyChange(PROPERTY_UPDATED, null, create());
        }
    }

    @Override
    public void reset() {
        parse(null);
    }

    @Override
    public String getTitle() {
        return MessageBundle.format("isolineEditor_displayName");
    }

    @Override
    public ImageIcon getIcon() {
        //TODO
        return null;
    }

    @Override
    public Image getPreview() {
        return null;
    }

    @Override
    public String getToolTip() {
        return MessageBundle.format("isolineEditor_displayName");
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
