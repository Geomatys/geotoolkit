/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013 Geomatys
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
package  org.geotoolkit.gui.swing.parameters.creator;

import org.opengis.parameter.GeneralParameterDescriptor;

/**
 * Interface with one method used by ParameterDescriptor creator panels to know
 * if a parameter is editable or not.
 *
 * @author Quentin Boileau (Geomatys)
 */
public interface EditableParameterFilter {

    public boolean isEditable(GeneralParameterDescriptor descriptor);

    public boolean isRemovable(GeneralParameterDescriptor descriptor);
}
