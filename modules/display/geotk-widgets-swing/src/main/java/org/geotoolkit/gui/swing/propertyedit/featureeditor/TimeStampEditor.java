/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.gui.swing.propertyedit.featureeditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.swingx.JXDatePicker;
import org.opengis.feature.type.PropertyType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class TimeStampEditor extends PropertyValueEditor implements ActionListener, ChangeListener{
    
    private JXDatePicker datePicker = new JXDatePicker();
    private JSpinner hours = new JSpinner();
    private JSpinner minutes = new JSpinner();
    
    public TimeStampEditor() {
        super(new BorderLayout());
        add(BorderLayout.WEST, datePicker);
        add(BorderLayout.CENTER, hours);
        add(BorderLayout.EAST, minutes);
        datePicker.setOpaque(false);
        datePicker.addActionListener(this);
        datePicker.getEditor().addFocusListener(this);
        
        hours.setOpaque(false);
        hours.addChangeListener(this);
        ((JSpinner.DefaultEditor) hours.getEditor()).getTextField().addFocusListener(this);
        
        minutes.setOpaque(false);
        minutes.addChangeListener(this);
        ((JSpinner.DefaultEditor) minutes.getEditor()).getTextField().addFocusListener(this);
        
    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        return Timestamp.class.equals(candidate.getBinding());
    }

    @Override
    public void setValue(PropertyType type, Object value) {
        final Timestamp t = (Timestamp) value;
        if (t != null) {
            datePicker.setDate(t);
            final Calendar calendar = GregorianCalendar.getInstance(); 
            calendar.setTime(t);   
            final int hour  = calendar.get(Calendar.HOUR_OF_DAY);
            final int minute = calendar.get(Calendar.MINUTE);
            hours.setValue(hour);
            minutes.setValue(minute);
        }
    }

    @Override
    public Object getValue() {
        final Calendar calendar = GregorianCalendar.getInstance(); 
        final Date d = datePicker.getDate();
        if (d != null) {
            calendar.setTime(datePicker.getDate());   
            calendar.set(Calendar.HOUR_OF_DAY, (Integer)hours.getValue());
            calendar.set(Calendar.MINUTE, (Integer)minutes.getValue());
            return new Timestamp(calendar.getTimeInMillis());
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        valueChanged();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        valueChanged();
    }

    @Override
    public void setEnabled(boolean enabled) {
        datePicker.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return datePicker.isEnabled();
    }
}
