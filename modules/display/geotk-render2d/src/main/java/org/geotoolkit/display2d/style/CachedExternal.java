/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.display2d.style;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import org.geotoolkit.renderer.style.DynamicSymbolFactoryFinder;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.style.ColorReplacement;
import org.opengis.style.ExternalGraphic;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.nio.IOUtilities;
import org.opengis.filter.Expression;

/**
 * Cached External graphic
 *
 * @author Johann Sorel (Geomatys)
 */
public class CachedExternal extends Cache<ExternalGraphic>{

    //Cached values
    private BufferedImage cachedImage = null;
    private boolean isSVG = false;

    public CachedExternal(final ExternalGraphic external){
        super(external);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void evaluate(){
        if(!isNotEvaluated) return;

        if(!evaluateExternal()){
            //no valid image we can clear the cache, nothing to paint, nothing
            // visible
            cachedImage = null;
            isStaticVisible = VisibilityState.UNVISIBLE;
        }else{
            isStaticVisible = VisibilityState.VISIBLE;
        }

        isStatic = !isSVG;
        requieredAttributs = EMPTY_ATTRIBUTS;
        isNotEvaluated = false;
    }

    /**
     *
     * @return true if is visible, false if something says there's nothing to paint
     */
    private boolean evaluateExternal(){

        //try to grab the inline image
        final Icon inline = styleElement.getInlineContent();
        if(inline != null){
            cachedImage = new BufferedImage(inline.getIconWidth(), inline.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) cachedImage.getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            inline.paintIcon(null, g2, 0,0);
            g2.dispose();
        }


        //if no inline image then use the online image
        if(cachedImage == null){
            OnlineResource online = styleElement.getOnlineResource();
            if(online != null && online.getLinkage() != null){

                final URI uri = styleElement.getOnlineResource().getLinkage();
                isSVG = uri != null && uri.toString().toLowerCase().endsWith(".svg");

                if (!isSVG) {
                    try (InputStream in = IOUtilities.open(uri)){
                        cachedImage = ImageIO.read(in);
                    } catch (IOException ex) {
                        LOGGER.log(Level.INFO, "CacheExternal, can read image for path : "+ uri.toString()+" : "+ex.getMessage());
                    }
                }
            }
        }

        cachedImage = recode(cachedImage, styleElement.getColorReplacements());

        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isVisible(final Object candidate) {
        return isValid();
    }

    /**
     *
     * @return true if this external image is valid.
     */
    public boolean isValid(){
        evaluate();
        return isSVG || cachedImage != null;
    }

    /**
     * Create or generate the image at a giver size.
     *
     * @param size : image size
     * @return BufferedImage
     */
    public BufferedImage getImage(final Float size, final float coeff, final RenderingHints hints){
        evaluate();

        BufferedImage img = cachedImage;

        if(cachedImage == null && isSVG){
            final URI uri = styleElement.getOnlineResource().getLinkage();

            if(uri != null){
                try{
                    final BufferedImage buffer = DynamicSymbolFactoryFinder.getImage(uri, styleElement.getFormat(),size, hints);
                    return recode(buffer, styleElement.getColorReplacements());
                }catch (Exception ex){
                    Logging.getLogger("org.geotoolkit.display2d.style").log(Level.WARNING, null, ex);
                }
            }

//            final float aspect = (float)(cachedImage.getHeight()) / size.floatValue() ;
//            final float maxwidth = cachedImage.getWidth() / aspect;
//
//            BufferedImage buffer = new BufferedImage( (int)(maxwidth+0.5f), (int)(size.floatValue()), BufferedImage.TYPE_INT_ARGB);
//            Graphics2D g2 = (Graphics2D) buffer.getGraphics();
//            if(hints != null) g2.setRenderingHints(hints);
//            g2.drawImage(cachedImage, 0, 0,buffer.getWidth(), buffer.getHeight(), 0, 0, cachedImage.getWidth(), cachedImage.getHeight(),null);
//            g2.dispose();
//            img = buffer;
        }


        //resize image if necessary
        if(cachedImage != null && size != null && !size.isNaN()){
            final float aspect = (float)(cachedImage.getHeight()) / size.floatValue() ;
            final float maxwidth = cachedImage.getWidth() / aspect;

            if((int)(maxwidth+0.5f)<=0 || (int)(size.floatValue()) <= 0){
                return null;
            }

            final BufferedImage buffer = new BufferedImage( (int)(maxwidth+0.5f), (int)(size.floatValue()), BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g2 = (Graphics2D) buffer.getGraphics();
            if(hints != null) g2.setRenderingHints(hints);
            g2.drawImage(cachedImage, 0, 0,buffer.getWidth(), buffer.getHeight(), 0, 0, cachedImage.getWidth(), cachedImage.getHeight(),null);
            g2.dispose();
            img = buffer;
        }

        if(coeff != 1){
            final float aspect = (float)(cachedImage.getHeight()) / coeff ;
            final float maxwidth = cachedImage.getWidth() / aspect;

            final BufferedImage buffer = new BufferedImage( (int)(maxwidth+0.5f), (int)(coeff), BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g2 = (Graphics2D) buffer.getGraphics();
            if(hints != null) g2.setRenderingHints(hints);
            g2.drawImage(cachedImage, 0, 0,buffer.getWidth(), buffer.getHeight(), 0, 0, cachedImage.getWidth(), cachedImage.getHeight(),null);
            g2.dispose();
            img = buffer;
        }

        return img;
    }

    public static CachedExternal cache(final ExternalGraphic external){
        return new CachedExternal(external);
    }

    private static BufferedImage recode(BufferedImage buffer, final Collection<ColorReplacement> replacements){
        if(buffer != null){
            for(final ColorReplacement replace : replacements){
                final Expression fct = replace.getRecoding();
                buffer = (BufferedImage) fct.apply(buffer);
            }
        }
        return buffer;
    }
}
