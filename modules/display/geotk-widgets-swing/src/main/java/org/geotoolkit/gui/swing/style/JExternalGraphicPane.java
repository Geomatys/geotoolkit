/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.style;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.sis.feature.SingleAttributeTypeBuilder;

import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.PropertyValueEditor;
import org.geotoolkit.gui.swing.resource.MessageBundle;

import org.geotoolkit.map.MapLayer;
import org.opengis.feature.AttributeType;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.style.ExternalGraphic;
import org.openide.util.Exceptions;

/**
 * External graphic panel
 *
 * @author Johann Sorel
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class JExternalGraphicPane extends StyleElementEditor<ExternalGraphic> {

    private static final AttributeType URLTYPE = new SingleAttributeTypeBuilder().setName(NamesExt.valueOf(""))
            .setValueClass(URL.class).build();
    private static final FileFilter IMAGES_FILTER = new FileNameExtensionFilter("Images", "jpg", "gif", "png", "ico", "bmp", "svg");

    private MapLayer layer = null;
    private ExternalGraphic external = null;

    /** Creates new form JDisplacementPanel */
    public JExternalGraphicPane() {
        super(ExternalGraphic.class);
        initComponents();
        guiPreview.setMir(true);
        guiURL.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(PropertyValueEditor.PROP_VALUE.equals(evt.getPropertyName())){
                    guessMimeType();
                    guiPreview.parse(create());
                }
            }
        });
    }

    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
    }

    @Override
    public MapLayer getLayer() {
        return layer;
    }

    @Override
    public void parse(final ExternalGraphic ext) {
        guiMime.setText("");
        guiURL.setValue(URLTYPE, null);
        this.external = ext;

        if (external != null) {
            //TODO : not handled yet
            //external.getCustomProperties();
            guiMime.setText(external.getFormat());

            final OnlineResource res = external.getOnlineResource();
            if(res != null && res.getLinkage() != null){
                try {
                    guiURL.setValue(URLTYPE, res.getLinkage().toURL());
                } catch (MalformedURLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            guiPreview.parse(ext);
        }
    }

    @Override
    public ExternalGraphic create() {
        final URL url = (URL) guiURL.getValue();
        if (url != null) {
            external = getStyleFactory().externalGraphic(url, guiMime.getText());
        } else {
            external = null;
        }
        return external;
    }

    private void guessMimeType() {
        final URL url = (URL) guiURL.getValue();

        String mimeType = null;
        if (url != null) {
            try {
                String ext = IOUtilities.extension(url);
                if ("svg".equalsIgnoreCase(ext)) {
                    mimeType = "image/svg";
                } else {
                    mimeType = XImageIO.fileExtensionToMimeType(ext);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        guiMime.setText(mimeType);
    }

    @Override
    protected Object[] getFirstColumnComponents() {
        return new Object[]{};
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        guiMime = new JTextField();
        guiPreview = new org.geotoolkit.gui.swing.style.JPreview();
        guiURL = new org.geotoolkit.gui.swing.propertyedit.featureeditor.URLEditor(IMAGES_FILTER);

        setOpaque(false);

        jLabel2.setText(MessageBundle.format("mime")); // NOI18N

        jLabel3.setText(MessageBundle.format("url")); // NOI18N

        guiPreview.setLayout(null);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(guiPreview, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiURL, GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiMime)))
                .addContainerGap())
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {jLabel2, jLabel3});

        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(guiMime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(guiURL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(guiPreview, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiURL, jLabel3});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiMime, jLabel2});

    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTextField guiMime;
    private org.geotoolkit.gui.swing.style.JPreview guiPreview;
    private org.geotoolkit.gui.swing.propertyedit.featureeditor.URLEditor guiURL;
    private JLabel jLabel2;
    private JLabel jLabel3;
    // End of variables declaration//GEN-END:variables

}
