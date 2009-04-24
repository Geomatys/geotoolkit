/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.gui.swing.debug;

import java.awt.Rectangle;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author sorel
 */
public class dummy {

    public static void main(String[] args) throws FactoryException, TransformException{
        
        GeneralEnvelope env = new GeneralEnvelope(new Rectangle(-180, -90, 360, 180));
        env.setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84);
        
        MathTransform trs = CRS.findMathTransform(env.getCoordinateReferenceSystem(), CRS.decode("EPSG:3395"));
        
        CRS.transform(trs, env);
        
        
        
    }
    
    
}
