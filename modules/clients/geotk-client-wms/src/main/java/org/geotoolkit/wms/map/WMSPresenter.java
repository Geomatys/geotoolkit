/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.wms.map;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import org.geotoolkit.client.Request;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.gui.swing.go2.control.information.presenter.AbstractInformationPresenter;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.CoverageMapLayer;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.wms.WMSCoverageReference;
import org.geotoolkit.wms.WebMapServer;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Presenter for WMS layer, this will send a getFeatureInfo query to retrieve more information.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSPresenter extends AbstractInformationPresenter{

    private static final Logger LOGGER = Logging.getLogger(WMSPresenter.class);

    public WMSPresenter() {
        super(100);
    }

    @Override
    public JComponent createComponent(final Object graphic,
            final RenderingContext2D context, final SearchAreaJ2D area) {

        if (!(graphic instanceof ProjectedCoverage)) {
            return null;
        }

        final ProjectedCoverage graCoverage = (ProjectedCoverage) graphic;
        final CoverageMapLayer layer = graCoverage.getLayer();

        if(!(layer.getCoverageReference() instanceof WMSCoverageReference)){
            return null;
        }

        final WMSCoverageReference reference = (WMSCoverageReference) layer.getCoverageReference();

        //get the different mime types
        final List<String> mimeTypes = new ArrayList<String>();
        final WebMapServer server = (WebMapServer)reference.getStore();
        try {
            final AbstractWMSCapabilities capa = server.getCapabilities();
            mimeTypes.addAll(capa.getCapability().getRequest().getGetFeatureInfo().getFormats());
        } catch (CapabilitiesException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(),ex);
        }

        final JPanel guiTopPanel = new JPanel(new BorderLayout());
        final JPanel guiCenterPanel = new JPanel(new BorderLayout());
        guiCenterPanel.setPreferredSize(new Dimension(350, 300));

        final JLabel guiMimeLabel = new JLabel(MessageBundle.getString("mimeType") +"  ");
        final JComboBox guiMimeTypes = new JComboBox();
        guiMimeTypes.setModel(new ListComboBoxModel(mimeTypes));
        guiTopPanel.add(BorderLayout.WEST, guiMimeLabel);
        guiTopPanel.add(BorderLayout.CENTER, guiMimeTypes);

        guiMimeTypes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiCenterPanel.removeAll();

                try {
                    final Request request = getFeatureInfo(reference, context, area, guiMimeTypes.getSelectedItem().toString(), 20);
                    try{
                        final JXHyperlink link = new JXHyperlink();
                        link.setURI(request.getURL().toURI());
                        guiCenterPanel.add(BorderLayout.NORTH,link);
                    }catch(Exception ex){
                        //hyperlinks is not supported on all platforms
                    }

                    new Thread(){
                        @Override
                        public void run() {
                            downloadGetFeatureInfo(guiCenterPanel, request);
                        }
                    }.start();

                } catch (TransformException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                } catch (FactoryException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                } catch (NoninvertibleTransformException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }

                guiCenterPanel.revalidate();
                guiCenterPanel.repaint();
            }
        });



        final JPanel guiAll = new JPanel(new BorderLayout());
        guiAll.add(BorderLayout.NORTH,guiTopPanel);
        guiAll.add(BorderLayout.CENTER,guiCenterPanel);
        return guiAll;
    }

    public Request getFeatureInfo(final WMSCoverageReference reference, final RenderingContext context, final SearchArea mask,
                final String infoFormat, final int featureCount)
                throws TransformException, FactoryException,
                NoninvertibleTransformException{

            final RenderingContext2D ctx2D = (RenderingContext2D) context;
            final DirectPosition center = mask.getDisplayGeometry().getCentroid();

            final Request url;
                url = reference.queryFeatureInfo(
                        ctx2D.getCanvasObjectiveBounds(),
                        ctx2D.getCanvasDisplayBounds().getSize(),
                        (int) center.getOrdinate(0),
                        (int) center.getOrdinate(1),
                        reference.getLayerNames(),
                        infoFormat,featureCount);

            return url;
        }

    private static void downloadGetFeatureInfo(final JPanel contentPane, final Request request){

        final JXBusyLabel guiBuzy = new JXBusyLabel(new Dimension(30, 30));
        guiBuzy.setBusy(true);
        contentPane.add(BorderLayout.CENTER,guiBuzy);
        contentPane.revalidate();
        contentPane.repaint();


        InputStream input = null;

        try{
            input = request.getResponseStream();

            Component content;
            try {

                final BufferedImage image = ImageIO.read(input);
                content = new JLabel(new ImageIcon(image));

            } catch (Exception ex) {
                try {
                    StringWriter writer = new StringWriter();
                    InputStreamReader streamReader = new InputStreamReader(input);
                    BufferedReader buffer = new BufferedReader(streamReader);
                    String line="";
                    while ( null!=(line=buffer.readLine())){
                        writer.write(line);
                    }
                    content = new JTextPane();
                    ((JTextPane)content).setText(writer.toString());
                } catch (Exception ex2) {
                    content = new JPanel();
                }
            }

            contentPane.remove(guiBuzy);
            contentPane.add(BorderLayout.CENTER,new JScrollPane(content));
            contentPane.revalidate();
            contentPane.repaint();

        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }finally{
            if(input != null){
                try {
                    input.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }

    }

}
