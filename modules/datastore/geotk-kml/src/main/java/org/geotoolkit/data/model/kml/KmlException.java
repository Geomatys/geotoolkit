/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.data.model.kml;

/**
 *
 * @author w7mainuser
 */
public class KmlException extends Exception{

    private String message;

    public KmlException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }



}
