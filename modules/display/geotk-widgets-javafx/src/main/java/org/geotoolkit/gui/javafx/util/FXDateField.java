/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.gui.javafx.util;

import java.time.LocalDateTime;
import javafx.beans.property.ObjectProperty;
import jidefx.scene.control.decoration.DecorationPane;
import jidefx.scene.control.field.LocalDateTimeField;

/**
 * TODO improve control to support all Date type objects.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class FXDateField extends DecorationPane{
    
    private final LocalDateTimeField field;
    
    public FXDateField() { 
        super(new DateTimeField("yyyy-MM-dd HH:mm:ss"));
        
        field = (LocalDateTimeField) getContent();
        //bug with the literal value 'T'
        //field.setDateTimeFormatter(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        field.setAutoAdvance(false);
        field.setAutoReformat(false);
        field.setAutoSelectAll(true);
        field.setPopupButtonVisible(true);
    }
        
    public LocalDateTime getValue(){
        return field.getValue();
    }
    
    public void setValue(LocalDateTime date){
        field.setValue(date);
    }

    public LocalDateTimeField getField() {
        return field;
    }
    
    public ObjectProperty<LocalDateTime> valueProperty(){
        return field.valueProperty();
    }
    
    private static class DateTimeField extends LocalDateTimeField{

        public DateTimeField() {
        }

        public DateTimeField(String pattern) {
            super(pattern);
        }

        /**
         * Overriden to support null values.
         * @return 
         */
        @Override
        public LocalDateTime getValue() {
            
            String text = getText();
            LocalDateTime value = super.getValue();
            
            if("-- ::".equals(text)){
                if(value == null) {
                    super.setValue(LocalDateTime.now());
                }
                else{
                    super.setValue(null);
                }
            }
            
            return super.getValue();
        }

        @Override
        public void replaceText(int start, int end, String text) {
            if (text.length() >= 1) {
                // Change behavior, avoid erasing all text when replacing a full group
                String existingText = getText();
                int newEnd = Math.max(0, Math.min(end, existingText.length()));
                String deletedText = existingText.substring(start, newEnd);
                if(deletedText.length()>text.length()){
                    end = start+text.length();
                }
            }
            super.replaceText(start, end, text);
        }
    }
    
    
}
