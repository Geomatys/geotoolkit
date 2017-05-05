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

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.sis.internal.system.OS;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.xml.MarshallerPool;

import org.geotoolkit.client.Request;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.gui.swing.render2d.control.information.presenter.AbstractInformationPresenter;
import org.geotoolkit.ogc.xml.exception.ServiceExceptionReport;
import org.geotoolkit.ogc.xml.exception.ServiceExceptionType;
import org.geotoolkit.wms.WMSCoverageReference;
import org.geotoolkit.wms.WebMapClient;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.geotoolkit.wms.xml.WMSMarshallerPool;

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
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class WMSPresenter extends AbstractInformationPresenter{

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.wms.map");

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
        final WebMapClient server = (WebMapClient)reference.getStore();
        try {
            final AbstractWMSCapabilities capa = server.getCapabilities();
            mimeTypes.addAll(capa.getCapability().getRequest().getGetFeatureInfo().getFormats());
        } catch (CapabilitiesException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(),ex);
        }

        final JPanel guiTopPanel = new JPanel(new BorderLayout());
        final JPanel guiCenterPanel = new JPanel(new BorderLayout());
        guiCenterPanel.setPreferredSize(new Dimension(350, 300));

        final JLabel guiMimeLabel = new JLabel(MessageBundle.format("mimeType") +"  ");
        final JComboBox guiMimeTypes = new JComboBox();
        guiMimeTypes.setModel(new ListComboBoxModel(mimeTypes));
        guiTopPanel.add(BorderLayout.WEST, guiMimeLabel);
        guiTopPanel.add(BorderLayout.CENTER, guiMimeTypes);

        guiMimeTypes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiCenterPanel.removeAll();

                try {
                    final String requestMT = guiMimeTypes.getSelectedItem().toString();
                    final Request request = getFeatureInfo(reference, context, area, requestMT, 20);

                    try {
                        final URI uri = request.getURL().toURI();
                        final JPanel urlPanel = new JPanel(new BorderLayout());
                        final Action action = new BrowserAction(uri);
                        final JXHyperlink link = new JXHyperlink(action);
                        urlPanel.add(BorderLayout.CENTER,link);

                        final JButton copyToClipboardBtn = new JButton("Copy URL");
                        copyToClipboardBtn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                final Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clpbrd.setContents(new StringSelection(uri.toString()), null);
                            }
                        });
                        urlPanel.add(BorderLayout.EAST, copyToClipboardBtn);

                        guiCenterPanel.add(BorderLayout.NORTH,urlPanel);
                    } catch(Exception ex) {
                        LOGGER.log(Level.WARNING, "Can't build request URL.");
                    }

                    new Thread(){
                        @Override
                        public void run() {
                            downloadGetFeatureInfo(guiCenterPanel, request, requestMT);
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

    private static void downloadGetFeatureInfo(final JPanel contentPane, final Request request, String requestMT){

        final JXBusyLabel guiBuzy = new JXBusyLabel(new Dimension(30, 30));
        guiBuzy.setBusy(true);
        contentPane.add(BorderLayout.CENTER,guiBuzy);
        contentPane.revalidate();
        contentPane.repaint();

        try {
            final URL url = request.getURL();
            final HttpURLConnection cnx = (HttpURLConnection)url.openConnection();
            final String respContentType = cnx.getContentType();

            InputStream in = cnx.getInputStream();
            if (!in.markSupported()) {
                in = new BufferedInputStream(in);
            }

            Component content = null;
            Dimension dim = contentPane.getPreferredSize();
            try {

                //maybe an error occurs
                try {
                    in.mark(4096);
                    Object result = unmarshallWMSResp(in);
                    if (result instanceof ServiceExceptionReport) {
                        final ServiceExceptionReport report = (ServiceExceptionReport) result;

                        final StringBuilder builder = new StringBuilder("An error occurred : \n");
                        for (ServiceExceptionType expType : report.getServiceExceptions()) {
                            builder.append(expType.getMessage());
                        }
                        content = renderError(builder.toString());
                    }
                } catch (JAXBException ex) {
                    //can't unmarshall -> maybe not an actual error
                    try {
                        in.reset();
                    } catch (IOException ioe) {
                        //error when reset the InputStream -> recreate a new one
                        in = url.openConnection().getInputStream();
                    }
                }

                if (content == null) {
                    //try to guess response type
                    if (respContentType.startsWith("image")) {
                        content = renderImage(in, dim);
                    } else if (respContentType.equals("text/html")) {
                        content = renderHTML(in);
                    } else if (respContentType.equals("text/xml")) {
                        content = renderXML(in);
                    } else {
                        content = renderText(in, null);
                    }
                }

            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                content = renderError("Error when request the FeatureInfo : \n"+ex.getMessage());
            }

            //can't render response
            if (content == null) {
                content = renderError("Can't render GetFeatureInfo response for requested URL : "+url.toString());
            }

            contentPane.remove(guiBuzy);
            contentPane.add(BorderLayout.CENTER, new JScrollPane(content));
            contentPane.setSize(dim);
            contentPane.revalidate();
            contentPane.repaint();

        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    /**
     * Unmarshall WMS service response.
     * @param stream InputStream
     * @return Object like
     * @throws JAXBException
     * @throws IOException
     */
    private static Object unmarshallWMSResp(InputStream stream) throws JAXBException, IOException {
        Unmarshaller unMarshaller   = null;
        MarshallerPool selectedPool = WMSMarshallerPool.getInstance();
        try {
            unMarshaller = selectedPool.acquireUnmarshaller();
            return unMarshaller.unmarshal(stream);
        } finally {
            if (selectedPool != null && unMarshaller != null) {
                selectedPool.recycle(unMarshaller);
            }
        }
    }

    /**
     * Render html in a JTextPane
     * @param stream InputStream
     * @throws IOException
     */
    private static Component renderHTML(InputStream stream) throws IOException {
        String html = getStringContent(stream);
        String css = null;
        final Pattern style = Pattern.compile("<style>([^><]+?)</style>", Pattern.MULTILINE);
        final Matcher matcher = style.matcher(html);
        if (matcher.find()) {
            css = matcher.group(1);
            html = html.substring(0, matcher.start(0)) + html.substring(matcher.end(0));
        }

        final StyleSheet styles = new StyleSheet();
        if (css != null) {
            styles.addRule(css);
        }

        final JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textPane.setStyledDocument(new HTMLDocument(styles));
        textPane.setText(html);

        return textPane;
    }

    /**
     * Render xml in a JTextPane
     * @param stream InputStream
     * @throws IOException
     */
    private static Component renderXML(InputStream stream) throws IOException {
        return renderText(stream, "text/xml");
    }

    /**
     * Render text in a JTextPane
     * @param stream InputStream
     * @param contentType String
     * @return JTextPane
     * @throws IOException
     */
    private static Component renderText(InputStream stream, final String contentType) throws IOException {
        final String content = getStringContent(stream);
        final JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        if (contentType != null) {
            textPane.setContentType(contentType);
        }
        textPane.setText(content);

        return textPane;
    }

    /**
     * Get String from an InputStream
     * @param stream
     * @return
     * @throws IOException
     */
    private static String getStringContent(InputStream stream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final StringBuilder out = new StringBuilder();
        final String newLine = System.getProperty("line.separator");
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }
        return out.toString();
    }

    /**
     * Render error message in a JTextPane
     * @param error String
     * @return JTextPane with error
     */
    private static Component renderError(final String error){
        final JTextPane errorPane = new JTextPane();
        Font font = errorPane.getFont();
        font = font.deriveFont(Font.BOLD);
        errorPane.setFont(font);
        errorPane.setForeground(Color.RED);
        errorPane.setText(error);
        return errorPane;
    }

    /**
     * Try to render Image in Component from URLConnection.
     * @param stream InputStream
     * @return JLabel with Image inside or null.
     * @throws IOException
     */
    private static Component renderImage(final InputStream stream, Dimension dim) throws IOException {

        BufferedImage img = ImageIO.read(stream);
        if (img != null) {
            dim.setSize(img.getWidth(), img.getHeight());
            return new JLabel(new ImageIcon(img));
        }
        return null;
    }

    /**
     * Custom action that open an URI in system browser
     * using first Desktop API and try with command line
     * if Desktop API failed.
     */
    private class BrowserAction extends AbstractAction {

        URI uri;

        public BrowserAction(URI uri) {
            super();
            this.uri = uri;
            putValue(Action.NAME, uri.toString());
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if ( !browseDesktop(uri)) {
                if (!openCommandLine(uri.toString())) {
                    LOGGER.log(Level.WARNING, "Unable to open browser for uri : "+uri.toString());
                }
            }
        }

        private boolean browseDesktop(URI uri) {

            try {
                if (!Desktop.isDesktopSupported()) {
                    return false;
                }

                if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    return false;
                }

                Desktop.getDesktop().browse(uri);

                return true;
            } catch (Throwable t) {
                return false;
            }
        }

        private boolean openCommandLine(String what) {

            OS os = OS.current();

            if (os.equals(OS.LINUX)) {
                if (runCommand("gnome-open", what)) return true;
                if (runCommand("kde-open", what)) return true;
                if (runCommand("xdg-open", what)) return true;
            }

            if (os.equals(OS.MAC_OS)) {
                if (runCommand("open", what)) return true;
            }

            if (os.equals(OS.WINDOWS)) {
                if (runCommand("explorer", what)) return true;
            }

            return false;
        }

        private boolean runCommand(String command, String uri) {

            String[] parts = new String[] {command, uri};

            try {
                Process p = Runtime.getRuntime().exec(parts);
                if (p == null) return false;

                try {
                    int retval = p.exitValue();
                    if (retval == 0) {
                        return false;
                    } else {
                        return false;
                    }
                } catch (IllegalThreadStateException itse) {
                    return true;
                }
            } catch (IOException e) {
                return false;
            }
        }
    }
}
