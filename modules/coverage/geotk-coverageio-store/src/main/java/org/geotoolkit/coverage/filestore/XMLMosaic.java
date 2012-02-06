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
package org.geotoolkit.coverage.filestore;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.BitSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.converter.Classes;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLMosaic implements GridMosaic{
        
    //empty tile informations
    @XmlTransient
    private BufferedImage emptyTile = null;
    @XmlTransient
    private byte[] emptyTileEncoded = null;
    
    //written values
    double scale;
    double upperleftX;
    double upperleftY;
    int gridWidth;
    int gridHeight;
    int tileWidth;
    int tileHeight;
    String completion;

    @XmlTransient
    XMLPyramid pyramid = null;
    @XmlTransient
    BitSet tileExist;
    @XmlTransient
    BitSet tileEmpty;
    
    void initialize(XMLPyramid pyramid){
        this.pyramid = pyramid;
        if(completion == null){
            completion = "";
        }
        tileExist = new BitSet(gridWidth*gridHeight);
        tileEmpty = new BitSet(gridWidth*gridHeight);
        String packed = completion.replaceAll("\n", "");
        packed = packed.replaceAll("\t", "");
        packed = packed.replaceAll(" ", "");
        for(int i=0,n=packed.length();i<n;i++){
            final char c = packed.charAt(i);
            tileExist.set(i, c!='0');
            tileEmpty.set(i, c=='2');
        }
        
        //create an empty tile
        emptyTile = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(emptyTile, "PNG", out);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(XMLMosaic.class.getName()).log(Level.SEVERE, null, ex);
        }
        emptyTileEncoded = out.toByteArray();
    }
    
    private void updateCompletionString(){
        final StringBuilder sb = new StringBuilder();
        int index = 0;
        for(int y=0,l=getGridSize().height;y<l;y++){
            sb.append('\n');
            for(int x=0,n=getGridSize().width;x<n;x++){
                char tc = !tileExist.get(index) ? '0': 
                          !tileEmpty.get(index) ? '1': 
                                                  '2';
                sb.append(tc);
                index++;
            }
        }
        sb.append('\n');
        completion = sb.toString();
    }
    
    /**
     * Id equals scale string value
     */
    @Override
    public String getId() {
        return String.valueOf(scale);
    }

    public File getFolder(){
        return new File(getPyramid().getFolder(),getId());
    }
    
    @Override
    public XMLPyramid getPyramid() {
        return pyramid;
    }
    
    @Override
    public Point2D getUpperLeftCorner() {
        return new Point2D.Double(upperleftX, upperleftY);
    }

    @Override
    public Dimension getGridSize() {
        return new Dimension(gridWidth, gridHeight);
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public Dimension getTileSize() {
        return new Dimension(tileWidth, tileHeight);
    }
    
    @Override
    public Envelope getEnvelope(){
        final double minX = getUpperLeftCorner().getX();
        final double maxY = getUpperLeftCorner().getY();
        final double spanX = getTileSize().width * getGridSize().width * scale;
        final double spanY = getTileSize().height* getGridSize().height* scale;
        
        final GeneralEnvelope envelope = new GeneralEnvelope(
                getPyramid().getCoordinateReferenceSystem());
        envelope.setRange(0, minX, minX + spanX);
        envelope.setRange(1, maxY - spanY, maxY );
        
        return envelope;
    }
    
    @Override
    public Envelope getEnvelope(int col, int row) {
        final double minX = getUpperLeftCorner().getX();
        final double maxY = getUpperLeftCorner().getY();
        final double spanX = getTileSize().width * scale;
        final double spanY = getTileSize().height * scale;
        
        final GeneralEnvelope envelope = new GeneralEnvelope(
                getPyramid().getCoordinateReferenceSystem());
        envelope.setRange(0, minX + col*spanX, minX + (col+1)*spanX);
        envelope.setRange(1, maxY - (row+1)*spanY, maxY - row*spanY);
        
        return envelope;
    }

    @Override
    public boolean isMissing(int col, int row) {
        return !tileExist.get(getTileIndex(col, row));
    }
    
    private boolean isEmpty(int col, int row){
        return tileEmpty.get(getTileIndex(col, row));
    }

    @Override
    public RenderedImage getTile(int col, int row, Map hints) throws DataStoreException {
        if(isEmpty(col, row)){
            return emptyTile;
        }
        
        try {
            return ImageIO.read(getTileFile(col, row));
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(),ex);
        }
    }

    @Override
    public InputStream getTileStream(int col, int row, Map hints) throws DataStoreException {
        if(isEmpty(col, row)){
            return new ByteArrayInputStream(emptyTileEncoded);
        }
        
        try {
            return new FileInputStream(getTileFile(col, row));
        } catch (FileNotFoundException ex) {
            throw new DataStoreException(ex.getMessage(),ex);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append("   scale = ").append(getScale());
        sb.append("   gridSize[").append(getGridSize().width).append(',').append(getGridSize().height).append(']');
        sb.append("   tileSize[").append(getTileSize().width).append(',').append(getTileSize().height).append(']');
        return sb.toString();
    }
    
    public File getTileFile(int col, int row) throws DataStoreException{
        checkPosition(col, row);
        return new File(getFolder(),row+"_"+col+"."+getPyramid().getPostfix());
    }

    void createTile(int col, int row, RenderedImage image) throws DataStoreException {
        if(isEmpty(image.getData())){
            tileExist.set(getTileIndex(col, row), true);
            tileEmpty.set(getTileIndex(col, row), true);
            updateCompletionString();
            return;
        }
        
        checkPosition(col, row);
        final File f = getTileFile(col, row);
        f.getParentFile().mkdirs();
        try {
            final ImageOutputStream out = ImageIO.createImageOutputStream(f);
            final ImageWriter writer = ImageIO.getImageWritersByFormatName("PNG").next();
            writer.setOutput(out);
            writer.write(image);
            writer.dispose();
            tileExist.set(getTileIndex(col, row), true);
            tileEmpty.set(getTileIndex(col, row), false);
            updateCompletionString();
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(),ex);
        }
    }
    
    void writeTiles(final RenderedImage image, final boolean onlyMissing) throws DataStoreException{
        
        ImageWriter writer = null;
        try {            
            writer = ImageIO.getImageWritersByFormatName("PNG").next();
            final boolean canWriteRaster = writer.canWriteRasters();
            
            for(int y=0,ny=image.getNumYTiles(); y<ny; y++){
                for(int x=0,nx=image.getNumXTiles(); x<nx; x++){
                    if(onlyMissing && !isMissing(x, y)){
                        continue;
                    }
                    
                    final int tileIndex = getTileIndex(x, y);
                    checkPosition(x, y);
                    
                    final Raster raster = image.getTile(x, y);
                    
                    //check if image is empty
                    if(isEmpty(raster)){
                        tileExist.set(tileIndex, true);
                        tileEmpty.set(tileIndex, true);
                        continue;
                    }
                    
                    final File f = getTileFile(x, y);
                    f.getParentFile().mkdirs();
                    
                    final ImageOutputStream out = ImageIO.createImageOutputStream(f);                    
                    writer.reset();
                    writer.setOutput(out);
                    
                    //write tile
                    if(canWriteRaster){
                        final IIOImage buffer = new IIOImage(raster, null, null);                    
                        writer.write(buffer);
                    }else{
                        //encapsulate image in a buffered image with parent color model
                        final BufferedImage buffer = new BufferedImage(
                                image.getColorModel(), 
                                (WritableRaster)raster, true, null);
                        writer.write(buffer);
                    }
                    
                    tileExist.set(tileIndex, true);
                    tileEmpty.set(tileIndex, false);
                }
            }
                    
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(),ex);
        } finally{
            updateCompletionString();
            if(writer != null){
                writer.dispose();
            }
        }
        
    }
    
    private void checkPosition(int col, int row) throws DataStoreException{
        if(col >= getGridSize().width || row >=getGridSize().height){
            throw new DataStoreException("Tile position is outside the grid : " + col +" "+row);
        }
    }
    
    private int getTileIndex(int col, int row){
        final int index = row*getGridSize().width + col;
        return index;
    }

    /**
     * check if image is empty
     */
    private static boolean isEmpty(Raster raster){
        double[] array = null;
        searchEmpty:
        for(int x=0,width=raster.getWidth(); x<width; x++){
            for(int y=0,height=raster.getHeight(); y<height; y++){
                array = raster.getPixel(x, y, array);
                for(double d : array){
                    if(d != 0){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
}
