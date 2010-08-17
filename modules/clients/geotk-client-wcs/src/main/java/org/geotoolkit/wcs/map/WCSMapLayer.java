/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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
package org.geotoolkit.wcs.map;

import java.awt.Image;
import java.util.logging.Level;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.net.URL;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.processing.CoverageProcessingException;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.AbstractMapLayer;
import org.geotoolkit.map.DynamicMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.wcs.GetCoverageRequest;
import org.geotoolkit.wcs.WebCoverageServer;

import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;


/**
 * Map representation of a WCS layer.
 *
 * @author Johann Sorel
 * @module pending
 */
public class WCSMapLayer extends AbstractMapLayer implements DynamicMapLayer {

    /**
     * EPSG:4326 object.
     */
    private static final CoordinateReferenceSystem EPSG_4326;
    static {
        CoordinateReferenceSystem crs = null;
        try {
            crs = CRS.decode("EPSG:4326");
        } catch (NoSuchAuthorityCodeException ex) {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
        } catch (FactoryException ex) {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
        }
        EPSG_4326 = crs;
    }


    //TODO : we should use the envelope provided by the wms capabilities
    private static final Envelope MAXEXTEND_ENV = new Envelope2D(DefaultGeographicCRS.WGS84, -180, -90, 360, 180);

    /**
     * The web coverage server to request.
     */
    private final WebCoverageServer server;

    /**
     * The layer to request.
     */
    private String layer;

    /**
     * Output format of the response.
     */
    private String format = "image/png";

    private boolean useLocalReprojection = false;

    public WCSMapLayer(final WebCoverageServer server, final String layer) {
        super(new DefaultStyleFactory().style());
        this.server = server;
        this.layer = layer;
    }

    /**
     * Returns the {@link WebCoverageServer} to request. Can't be {@code null}.
     */
    public WebCoverageServer getServer() {
        return server;
    }

    /**
     * Define if the map layer must rely on the geotoolkit reprojection capabilities
     * if the distant server can not handle the canvas crs.
     * The result image might not be pretty, but still better than no image.
     * @param useLocalReprojection
     */
    public void setUseLocalReprojection(boolean useLocalReprojection) {
        this.useLocalReprojection = useLocalReprojection;
    }

    public boolean isUseLocalReprojection() {
        return useLocalReprojection;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds() {
        return MAXEXTEND_ENV;
    }

    /**
     * Creates the {@linkplain GetCoverageRequest get coverage request} object.
     *
     * @return A {@linkplain GetCoverageRequest get coverage request} object containing the
     *         predefined parameters.
     */
    public GetCoverageRequest createGetMapRequest() {
        final GetCoverageRequest request = server.createGetCoverage();
        request.setCoverage(layer);
        return request;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL query(final RenderingContext context) throws PortrayalException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(RenderingContext context) throws PortrayalException {
        if (!(context instanceof RenderingContext2D)) {
            throw new PortrayalException("WCSMapLayer only support rendering for RenderingContext2D");
        }
        //todo
    }

    private static void portray(RenderingContext2D renderingContext, GridCoverage2D dataCoverage) throws PortrayalException{
        final CanvasMonitor monitor = renderingContext.getMonitor();
        final Graphics2D g2d = renderingContext.getGraphics();

        final CoordinateReferenceSystem coverageCRS = dataCoverage.getCoordinateReferenceSystem();
        try{
            final CoordinateReferenceSystem candidate2D = CRSUtilities.getCRS2D(coverageCRS);
            if(!CRS.equalsIgnoreMetadata(candidate2D,renderingContext.getObjectiveCRS2D()) ){

                dataCoverage = (GridCoverage2D) Operations.DEFAULT.resample(dataCoverage.view(ViewType.NATIVE), renderingContext.getObjectiveCRS2D());

                if(dataCoverage != null){
                    dataCoverage = dataCoverage.view(ViewType.RENDERED);
                }
            }
        } catch (CoverageProcessingException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        } catch(Exception ex){
            //several kind of errors can happen here, we catch anything to avoid blocking the map component.
            monitor.exceptionOccured(
                new IllegalStateException("Coverage is not in the requested CRS, found : " +
                "\n"+ coverageCRS +
                " was expecting : \n" +
                renderingContext.getObjectiveCRS() +
                "\nOriginal Cause:"+ ex.getMessage(), ex), Level.WARNING);
            return;
        }

        if(dataCoverage == null){
            LOGGER.log(Level.WARNING, "Reprojected coverage is null.");
            return;
        }

        //we must switch to objectiveCRS for grid coverage
        renderingContext.switchToObjectiveCRS();

        final RenderedImage img = dataCoverage.getRenderedImage();
        final MathTransform2D trs2D = dataCoverage.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT);
        if(trs2D instanceof AffineTransform){
            g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
            g2d.drawRenderedImage(img, (AffineTransform)trs2D);
        }else if (trs2D instanceof LinearTransform) {
            final LinearTransform lt = (LinearTransform) trs2D;
            final int col = lt.getMatrix().getNumCol();
            final int row = lt.getMatrix().getNumRow();
            //TODO using only the first parameters of the linear transform
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass());
        }else{
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass() );
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Image getLegend() throws PortrayalException {
        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Sets the layer names to requests.
     *
     * @param names Array of layer names.
     */
    public void setLayerName(final String name) {
        this.layer = name;
    }

    /**
     * Returns the layer names.
     */
    public String getLayerName() {
        return layer;
    }

    /**
     * Sets the format for the output response. By default sets to {@code image/png}
     * if none.
     *
     * @param format The mime type of an output format.
     */
    public void setFormat(String format) {
        this.format = format;
        if (this.format == null) {
            format = "image/png";
        }
    }

    /**
     * Gets the format for the output response. By default {@code image/png}.
     */
    public String getFormat() {
        return format;
    }

}
