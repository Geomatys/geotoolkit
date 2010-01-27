/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.feature.xml.jaxb;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

/**
 *
 * @author guilhem
 */
public class JAXBEventHandler implements ValidationEventHandler {

      public boolean handleEvent(ValidationEvent ve) {
        if (ve.getSeverity()==ve.FATAL_ERROR ||ve .getSeverity()==ve.ERROR){
            ValidationEventLocator  locator = ve.getLocator();
            /*print message from valdation event
            System.out.println("Message is " + ve.getMessage());
            //output line and column number
            System.out.println("Column is " +
                  locator.getColumnNumber() +
                  " at line number " + locator.getLineNumber());*/
            if (ve.getMessage() != null && ve.getMessage().startsWith("org.opengis.referencing.NoSuchAuthorityCodeException")) {
                return false;
            }

         }
         return true;
       }

   }
