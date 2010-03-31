/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.metadata.dimap;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.media.jai.Warp;
import javax.media.jai.WarpAffine;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.lang.Static;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.WarpTransform2D;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static org.geotoolkit.metadata.dimap.DimapConstants.*;
import static org.geotoolkit.util.DomUtilities.*;

/**
 * Utility class to parse dimap file.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@Static
public final class DimapParser {

    private DimapParser() {
    }

    /**
     * Convinient method to aquiere a DOM document from an input.
     * This is provided as a convinient method, any dom document may
     * be used for all methods in the class.
     */
    public static Document read(Object input) throws ParserConfigurationException, SAXException, IOException {
        final InputStream stream = toStream(input);
        // création d'une fabrique de documents
        final DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
        final DocumentBuilder constructeur = fabrique.newDocumentBuilder();
        final Document document = constructeur.parse(stream);
        stream.close();
        return document;
    }

    /**
     * Read the Coordinate Reference System of the grid.
     * Thoses informations are provided by the CoordinateReferenceSystem tag.
     * 
     * @param doc
     * @return CoordinateReferenceSystem
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    public static CoordinateReferenceSystem readCRS(Element doc) throws NoSuchAuthorityCodeException, FactoryException{
        final Element ele = firstElement(doc, TAG_CRS);
        final Element code = firstElement(ele, TAG_HORIZONTAL_CS_CODE);
        return CRS.decode(code.getTextContent());
    }

    /**
     * Read the Grid to CRS transform.
     * Thoses informations are provided by the Geoposition tag.
     *
     * @param doc
     * @return AffineTransform
     * @throws FactoryException
     * @throws TransformException
     */
    public static AffineTransform readGridToCRS(Element doc) throws FactoryException, TransformException{
        final Element ele = firstElement(doc, TAG_GEOPOSITION);
        final Element insert = firstElement(ele, TAG_GEOPOSITION_INSERT);
        final Element points = firstElement(ele, TAG_GEOPOSITION_POINTS);
        final Element affine = firstElement(ele, TAG_GEOPOSITION_AFFINE);

        if(insert != null){
            // X = ULXMAP + XDIM * i
            // Y = ULYMAP - YDIM * j
            final double ulx = textValue(insert, TAG_ULXMAP, Double.class);
            final double uly = textValue(insert, TAG_ULYMAP, Double.class);
            final double xdim = textValue(insert, TAG_XDIM, Double.class);
            final double ydim = textValue(insert, TAG_YDIM, Double.class);
            return new AffineTransform(xdim, 0, 0, -ydim, ulx, uly);
        }else if(affine != null){
            // X (CRS) = X0 + X1 * X(Data) + X2 * Y(Data)
            // Y (CRS) = Y0 + Y1 * X(Data) + Y2 * Y(Data)
            final double x0 = textValue(affine, TAG_AFFINE_X0, Double.class);
            final double x1 = textValue(affine, TAG_AFFINE_X1, Double.class);
            final double x2 = textValue(affine, TAG_AFFINE_X2, Double.class);
            final double y0 = textValue(affine, TAG_AFFINE_Y0, Double.class);
            final double y1 = textValue(affine, TAG_AFFINE_Y1, Double.class);
            final double y2 = textValue(affine, TAG_AFFINE_Y2, Double.class);
            return new AffineTransform(x0, y0, x1, y1, x2, y2);
        }else if(points != null){
            //throw new TransformException("points transform not supported yet.");
            final NodeList tiePoints = ele.getElementsByTagName(TAG_TIE_POINT);
            final List<Point2D> sources = new ArrayList<Point2D>();
            final List<Point2D> dests = new ArrayList<Point2D>();

            for(int i=0,n=tiePoints.getLength();i<n;i++){
                final Element vertex = (Element) tiePoints.item(i);
                final double coordX = textValue(vertex, TAG_TIE_POINT_CRS_X, Double.class);
                final double coordY = textValue(vertex, TAG_TIE_POINT_CRS_Y, Double.class);
                final int dataY = textValue(vertex, TAG_TIE_POINT_DATA_X, Integer.class);
                final int dataX = textValue(vertex, TAG_TIE_POINT_DATA_Y, Integer.class);
                sources.add(new Point2D.Double(dataX,dataY));
                dests.add(new Point2D.Double(coordX,coordY));
            }

            final WarpTransform2D warptrs = new WarpTransform2D(
                    sources.toArray(new Point2D[sources.size()]),
                    dests.toArray(new Point2D[dests.size()]), 1);

            final Warp warp = warptrs.getWarp();
            if(warp instanceof WarpAffine){
                final WarpAffine wa = (WarpAffine) warp;
                return wa.getTransform();
            }else{
                throw new TransformException("Wrap transform is not affine.");
            }
        }else{
            throw new TransformException("Geopositioning type unknowned.");
        }
        
    }

    /**
     * Read the raster dimension from the document. This include number of rows,
     * columns and bands.
     * Thoses informations are provided by the Raster_dimensions tag.
     * 
     * @param doc
     * @return int[] 0:rows, 1:cols, 2:bands
     */
    public static int[] readRasterDimension(Element doc){
        final Element ele = firstElement(doc, TAG_RASTER_DIMENSIONS);
        final int rows = textValue(ele, TAG_NROWS, Integer.class);
        final int cols = textValue(ele, TAG_NCOLS, Integer.class);
        final int bands = textValue(ele, TAG_NBANDS, Integer.class);
        return new int[]{rows,cols,bands};
    }

    /**
     * Read the coverage sample dimensions.
     * Thoses informations are provided by the Image_display tag.
     *
     * @param parent
     * @return GridSampleDimension
     */
    public static int[] readColorBandMapping(Element parent){
        final Element ele = firstElement(parent, TAG_IMAGE_DISPLAY);
        final Element displayOrder = firstElement(ele, TAG_BAND_DISPLAY_ORDER);
        final int red = textValue(displayOrder, TAG_RED_CHANNEL, Integer.class);
        final int green = textValue(displayOrder, TAG_GREEN_CHANNEL, Integer.class);
        final int blue = textValue(displayOrder, TAG_BLUE_CHANNEL, Integer.class);

        return new int[]{red,green,blue};
    }

    /**
     * Read the coverage sample dimensions.
     * Thoses informations are provided by the Image_display tag.
     *
     * @param doc
     * @return GridSampleDimension
     */
    @Deprecated
    public static GridSampleDimension[] readSampleDimensions(Element doc, String coverageName,int nbbands){
        final Element ele = firstElement(doc, TAG_IMAGE_DISPLAY);
        final Element displayOrder = firstElement(ele, TAG_BAND_DISPLAY_ORDER);
        final int red = textValue(displayOrder, TAG_RED_CHANNEL, Integer.class);
        final int green = textValue(displayOrder, TAG_GREEN_CHANNEL, Integer.class);
        final int blue = textValue(displayOrder, TAG_BLUE_CHANNEL, Integer.class);

        //todo not sure we should store color informations in the grid sample dimension
        final GridSampleDimension[] dimensions = new GridSampleDimension[nbbands];
        for(int i=1; i<=nbbands; i++){
            String name = String.valueOf(i)+" ";
            if(i == red){
                name = name + RED;
            }
            if(i == green){
                name = name + GREEN;
            }
            if(i == blue){
                name = name + BLUE;
            }
            dimensions[i-1] = new GridSampleDimension(name);

            //todo read the special values
        }

        return dimensions;
    }

    private static InputStream toStream(Object input) throws FileNotFoundException, IOException{

        if(input instanceof InputStream){
            return (InputStream) input;
        }else if(input instanceof File){
            return new FileInputStream((File)input);
        }else if(input instanceof URI){
            return ((URI)input).toURL().openStream();
        }else if(input instanceof URL){
            return ((URL)input).openStream();
        }else if(input instanceof String){
            return new URL((String)input).openStream();
        }else{
            throw new IOException("Can not handle inout type : " + input.getClass());
        }
    }

}
