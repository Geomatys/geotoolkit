/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.ncwms;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.client.Request;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.wms.GetMapRequest;
import org.geotoolkit.wms.WMSCoverageResource;
import org.opengis.util.GenericName;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Extend WMS Coverage Reference with additional ncWMS parameters.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class NcWMSCoverageResource extends WMSCoverageResource{


    /**
     * The image opacity.
     * <br/><br/>
     * Parameter key: OPACITY
     * <br/><br/>
     * Possible values: Integer between 0 and 100 inclusive
     * <br/><br/>
     * 0 = fully transparent, 100 = fully opaque (default). Only applies to image
     * formats that support partial pixel transparency (e.g. PNG). This parameter
     * is redundant if the client application can set image opacity (e.g. Google
     * Earth).
     *
     */
    private Integer ncOpacity = null;

    /**
     * The color scale range.
     * <br/><br/>
     * Parameter key: COLORSCALERANGE
     * <br/><br/>
     * Possible values: "auto" or "min,max"
     * <br/><br/>
     * COLORSCALERANGE omitted: (default) Default scale range used (this is to
     * allow backward compatibility with standards WMS clients, particularly
     * tiling clients: it ensures that the same color scale range is used for
     * each tile). COLORSCALERANGE=min,max: The extremes of the color scale are
     * set to min and max (in the native units of the variable in question).
     * COLORSCALERANGE=auto: ncWMS sets the scale range to the min and max values
     * of the generated image (i.e. maximum contrast stretch). See
     * <a target="_blank" href="http://www.resc.rdg.ac.uk/trac/ncWMS/wiki/ColorScaleRange">ColorScaleRange</a>
     * for more discussion of how ncWMS handles color ranges.
     *
     */
    //private String colorScaleRange = null;

    /**
     * The number of color bands in the palette.
     * <br/><br/>
     * Parameter key: NUMCOLORBANDS
     * <br/><br/>
     * Possible values: Any positive integer up to and including 254
     * <br/><br/>
     * Setting this to a relatively low number (e.g. 10) will produce obvious
     * color banding, giving the appearance of contour lines. Default value is
     * 254.
     *
     */
    private Integer numColorBands = null;

    /**
     * Choose from a linear or logarithmic color scale
     * <br/><br/>
     * Parameter key: LOGSCALE
     * <br/><br/>
     * Possible values: true or false
     * <br/><br/>
     * Set true to use a logarithmic spacing between the min and max of the color
     * scale range. This is particularly useful where data values vary over several
     * orders of magnitude in an image (common in biological parameters). LOGSCALE
     * cannot be set true if the color scale range includes zero or negative
     * values. Default is false.
     *
     */
    private Boolean logScale = null;

    public NcWMSCoverageResource(NcWebMapClient server, GenericName name) {
        super(server, name);
    }

    public NcWMSCoverageResource(NcWebMapClient server, String... layers) {
        super(server, layers);
    }

    /**
     * Sets the image opacity.
     *
     * @param opacity the image opacity to set.
     */
    public void setOpacity(final Integer opacity) {
        this.ncOpacity = opacity;
    }

    /**
     * Gets the number of color bands in the palette.
     *
     * @return the number of color bands in the palette.
     */
    public Integer getNumColorBands() {
        return numColorBands;
    }

    /**
     * Sets the number of color bands in the palette.
     *
     * @param numColorBands the number of color bands in the palette.
     */
    public void setNumColorBands(Integer numColorBands) {
        this.numColorBands = numColorBands;
    }

    /**
     * Gets the choice from a linear or logarithmic color scale.
     *
     * @return if we choose a logarithmic color scale or not.
     */
    public Boolean isLogScale() {
        return logScale;
    }

    /**
     * Sets the choice from a linear or logarithmic color scale.
     *
     * @param logScale The choice of using a logarithmic color scale or not.
     */
    public void setLogScale(Boolean logScale) {
        this.logScale = logScale;
    }



    /********************* Queries functions **********************************/

    /**
     * {@inheritdoc}
     */
    @Override
    public void prepareQuery(final GetMapRequest request, final GeneralEnvelope env,
            final Dimension dim, final Point2D pickCoord) throws TransformException,
            FactoryException{
        super.prepareQuery(request, env, dim, pickCoord);
        prepareNcWMSCommonRequest((NcGetMapRequest) request);
    }

    /**
     * Sets ncWMS common request parameters.
     * @param request the current request.
     */
    private void prepareNcWMSCommonRequest(final NcWMSCommonRequest request) {
        request.setOpacity(ncOpacity);
        request.setNumColorBands(numColorBands);
        request.setLogScale(logScale);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL query(final Envelope env, final Dimension rect) throws MalformedURLException,
    TransformException, FactoryException {
        final NcGetMapRequest request = ((NcWebMapClient) getOriginator()).createGetMap();
        prepareQuery(request, new GeneralEnvelope(env), rect, null);
        return request.getURL();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Request queryFeatureInfo(final Envelope env, final Dimension rect, int x,
            int y, final String[] queryLayers, final String infoFormat,
            final int featureCount) throws TransformException, FactoryException {
        final NcGetFeatureInfoRequest request = ((NcWebMapClient) getOriginator()).createGetFeatureInfo();
        prepareGetFeatureInfoRequest(request, env, rect, x, y, queryLayers, infoFormat, featureCount);
        return request;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Request queryLegend(final Dimension rect, final String format, final String rule,
            final Double scale) throws MalformedURLException {

        final NcGetLegendRequest request = ((NcWebMapClient) getOriginator()).createGetLegend();
        prepareGetLegendRequest(request, rect, format, rule, scale);

        /*
         * The STYLES parameter of the Getmap request is defined like this:
         * [style_name]/[palette_name] so we split the string and retrieve the value
         * of the PALETTE parameter.
         *
         */
        if (getStyles() != null && getStyles().length > 0) {
            if (getStyles()[0].contains("/") && getStyles()[0].split("/").length == 2) {
                final String palette = getStyles()[0].split("/")[1];
                request.setPalette(palette);
            }
        }

        prepareNcWMSCommonRequest(request);

        return request;
    }

    /**
     * Add mandatory parameters to the request objects.
     *
     * @param request The GetMetadata request object.
     * @param item Th type of the request. Possible values
     * are 'menu', 'layerDetails', 'timesteps', 'minmax', 'animationTimesteps'
     */
    private void prepareQueryMetadata(final NcGetMetadataRequest request,
            final String item) {
        request.setItem(item);
        request.setLayerName(getLayerNames()[0]);
    }

    /**
     * Generates a GetMetadata?item=layerDetails URL.
     *
     * @return the request URL.
     * @throws MalformedURLException
     */
    public URL queryMetadataLayerDetails() throws MalformedURLException {
        final NcGetMetadataRequest request = ((NcWebMapClient) getOriginator()).createGetMetadata();
        prepareQueryMetadata(request, "layerDetails");
        request.setTime(dimensions().get("TIME"));
        return request.getURL();
    }

    /**
     * Generates a GetMetadata?item=animationTimesteps URL.
     *
     * @param start The start date of the animation.
     * @param end The end date of the animation.
     * @return the request URL.
     * @throws MalformedURLException
     */
    public URL queryMetadataAnimationTimesteps(final String  start, final String end) throws MalformedURLException {
        final NcGetMetadataRequest request = ((NcWebMapClient) getOriginator()).createGetMetadata();
        prepareQueryMetadata(request, "animationTimesteps");
        request.setStart(start);
        request.setEnd(end);
        return request.getURL();
    }

    /**
     * Generates a GetMetadata?item=timesteps URL.
     *
     * @return the request URL.
     * @throws MalformedURLException
     */
    public URL queryMetadataTimesteps() throws MalformedURLException {
        final NcGetMetadataRequest request = ((NcWebMapClient) getOriginator()).createGetMetadata();
        prepareQueryMetadata(request, "timesteps");
        request.setDay(dimensions().get("TIME"));
        return request.getURL();
    }

    /**
     * Generates a GetMetadata?item=minmax URL.
     * ex: http://behemoth.nerc-essc.ac.uk/ncWMS/wms?
     * request=GetMetadata
     * &item=minmax
     * &bbox=9.125%2C53.17499923706055%2C30.291667938232%2C65.875
     * &crs=EPSG%3A4326
     * &width=50
     * &height=50
     * &layers=BALTIC_BEST_EST%2Fuvel
     * &elevation=-4
     * &time=2011-07-24T00%3A00%3A00.000Z
     *
     * @return the request URL.
     * @throws MalformedURLException
     */
    public URL queryMetadataMinmax(final String crsCode, final String bbox, final String width, final String height) throws MalformedURLException {
        final NcGetMetadataMinMaxRequest request = ((NcWebMapClient) getOriginator()).createGetMetadataMinMax();
        prepareQueryMetadata(request, "minmax");
        request.setTime(dimensions().get("TIME"));
        request.setElevation(dimensions().get("ELEVATION"));
        request.setCrs(crsCode);
        request.setBbox(bbox);
        request.setWidth(width);
        request.setHeight(height);

        return request.getURL();
    }

    /**
     * Generates a GetTransect URL.
     *
     * @param crsCode           A crs code.
     * @param lineString        Coordinates of a line: x1%y1,x2%y2 ....
     * @param outputFormat      The mimetype of the output format. Possible values: image/png, text/xml ...
     * @return the request URL.
     * @throws MalformedURLException
     */
    public URL queryTransect(final String crsCode, final String lineString,
            final String outputFormat) throws MalformedURLException {
        final NcGetTransectRequest request = ((NcWebMapClient) getOriginator()).createGetTransect();

        // Mandatory
        request.setLayer(getLayerNames()[0]);
        request.setCrs(crsCode);
        request.setFormat(outputFormat);
        request.setLineString(lineString);

        // Optional
        request.setTime(dimensions().get("TIME"));
        request.setElevation(dimensions().get("ELEVATION"));

        return request.getURL();
    }

    /**
     * Generates a GetVerticalProfile URL.
     *
     * @param crsCode       A crs code.
     * @param x             The X coordinate of a point
     * @param y             The Y coordinate of a point
     * @param outputFormat  The mimetype of the output format.Possible values: image/png ...
     * @return the request URL.
     * @throws MalformedURLException
     */
    public URL queryVerticalProfile(final String crsCode, float x, float y,
            final String outputFormat) throws MalformedURLException {
        final NcGetVerticalProfileRequest request = ((NcWebMapClient) getOriginator()).createGetVerticalProfile();

        // Mandatory
        request.setLayer(getLayerNames()[0]);
        request.setCrs(crsCode);
        request.setFormat(outputFormat);
        request.setPoint(x + " " + y);

        // Optional
        request.setTime(dimensions().get("TIME"));

        return request.getURL();
    }

    /**
     * Generates a GetVerticalProfile URL.
     *
     * @param crsCode       A crs code.
     * @param x             The X coordinate of a point
     * @param y             The Y coordinate of a point
     * @param outputFormat  The mimetype of the output format.Possible values: image/png ...
     * @param dateBegin     The period date begin
     * @param dateEnd       The period date end
     * @return the request URL.
     * @throws MalformedURLException
     */
    public URL queryTimeseries(final Envelope env, final Dimension rect, int x,
            int y, final String infoFormat,
            final String dateBegin, final String dateEnd) throws MalformedURLException, TransformException, FactoryException {
        final NcGetTimeseriesRequest request = ((NcWebMapClient) getOriginator()).createGetTimeseries();

        final String[] layer = new String[]{getLayerNames()[0]};
        prepareGetFeatureInfoRequest(request, env, rect, x, y, layer, infoFormat, 0);

        // Mandatory
        request.setDateBegin(dateBegin);
        request.setDateEnd(dateEnd);

        return request.getURL();
    }


}
