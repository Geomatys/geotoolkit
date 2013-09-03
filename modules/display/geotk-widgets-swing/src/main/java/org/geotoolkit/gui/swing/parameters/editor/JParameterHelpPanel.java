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
package  org.geotoolkit.gui.swing.parameters.editor;

import  org.geotoolkit.gui.swing.parameters.ParameterType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.openide.util.NbBundle;

/**
 * Help Panel for parameter.
 * 
 * @author Quentin Boileau (Geomatys)
 */
public class JParameterHelpPanel extends JTextPane {
    
    
    private GeneralParameterDescriptor paramDesc;
    private String validationError = null;

    public JParameterHelpPanel(GeneralParameterDescriptor paramDesc) {
        this.paramDesc = paramDesc;
        
        setEditable(false);
        setContentType("text/html"); // NOI18N
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        
        //CSS
        final StyleSheet styles = new StyleSheet();
        styles.addRule("body {padding:10px; width:250px; background-color:#ffffff;}");
        styles.addRule("h1 {font-size:20px; font-weight:bold; text-align:left;}");
        styles.addRule("table {margin-left: 15px; width:200px;}");
        styles.addRule("tr {border-width: 1px;  border-style:solid; border-color:black;}");
        styles.addRule("td {border-width: 1px;  border-style:solid; border-color:black; padding:5px;}");
        styles.addRule(".data {text-align:right;}");
        styles.addRule("#error {color:red;}");
        setStyledDocument(new HTMLDocument(styles));
        
        updateHelpContent();
    }

    public GeneralParameterDescriptor getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(GeneralParameterDescriptor paramDesc) {
        this.paramDesc = paramDesc;
        updateHelpContent();
    }
    
    public void setParameter(GeneralParameterValuePanel selected) {
        validationError = selected.getValidationError();
        setParamDesc(selected.getDescriptor());
    }
    
    private void updateHelpContent() {
        if (paramDesc != null) {
            
            ParameterType parameterType = null;
            if (paramDesc instanceof ParameterDescriptor) {
                parameterType = ParameterType.SIMPLE;
            } else {
                parameterType = ParameterType.GROUP;
            }
            
            String title = null;
            String description = null;
            String mandatory = null;
            String defaultValue = null;
            String[] validValues = null;
            String dataType = null;
            String dataUnits = null;
            int minOccurs = 1;
            int maxOccurs = 1;

            title = paramDesc.getName().getCode();
            description = paramDesc.getRemarks() != null ? paramDesc.getRemarks().toString() : null;
            minOccurs = paramDesc.getMinimumOccurs();
            maxOccurs = paramDesc.getMaximumOccurs();
            
            // get parameter class, default value and unit
            if (parameterType.equals(ParameterType.SIMPLE)) {
                if (minOccurs == 1 && maxOccurs == 1) {
                    mandatory = MessageBundle.getString("parameters.editorHelpMandatory");
                } else {
                    mandatory = MessageBundle.getString("parameters.editorHelpOptional");
                }
                dataType = ((ParameterDescriptor)paramDesc).getValueClass().getSimpleName();
                defaultValue = String.valueOf(((ParameterDescriptor)paramDesc).getDefaultValue());
                final Set valueSet = ((ParameterDescriptor)paramDesc).getValidValues();
                if (valueSet != null) {
                    List<String> valueString = new ArrayList<String>();
                    for (Object object : valueSet) {
                        valueString.add(String.valueOf(object));
                    }
                    validValues = valueString.toArray(new String [valueString.size()]);
                }
                dataUnits = ((ParameterDescriptor)paramDesc).getUnit() != null ? ((ParameterDescriptor)paramDesc).getUnit().toString() : null;
            }
            
            //create html string
            final StringBuilder sb = new StringBuilder();
            sb.append("<html>");
            sb.append("<body>");
            sb.append("<h1>").append(title).append("</h1>");
            sb.append("<hr/>");
            sb.append("<br/>");
            sb.append("<table>");
            sb.append("<tbody>");
            if (mandatory != null) {
                sb.append("<tr>");
                    sb.append("<td>").append(MessageBundle.getString("parameters.editorHelpMandatoryLabel")).append("</td>");
                    sb.append("<td class=\"data\">").append(mandatory).append("</td>");
                sb.append("</tr>");
            }
            
            if (parameterType.equals(ParameterType.SIMPLE)) {
                if (dataType != null) {
                    sb.append("<tr>");
                        sb.append("<td>").append(MessageBundle.getString("parameters.editorHelpTypeLabel")).append("</td>");
                        sb.append("<td class=\"data\">").append(dataType).append("</td>");
                    sb.append("</tr>");
                }
                
                if (defaultValue != null) {
                    sb.append("<tr>");
                        sb.append("<td>").append(MessageBundle.getString("parameters.editorHelpDefaultLabel")).append("</td>");
                        sb.append("<td class=\"data\">").append(defaultValue).append("</td>");
                    sb.append("</tr>");
                }
                
                if (validValues != null) {
                    sb.append("<tr>");
                        sb.append("<td>").append(MessageBundle.getString("parameters.editorHelpValidLabel")).append("</td>");
                        sb.append("<td class=\"data\">").append(validValues).append("</td>");
                    sb.append("</tr>");
                }
                
                if (dataUnits != null) {
                    sb.append("<tr>");
                        sb.append("<td>").append(MessageBundle.getString("parameters.editorHelpUnitLabel")).append("</td>");
                        sb.append("<td class=\"data\">").append(dataUnits).append("</td>");
                    sb.append("</tr>");
                }
            } else {
                sb.append("<tr>");
                    sb.append("<td>").append(MessageBundle.getString("parameters.minOccurs")).append("</td>");
                    sb.append("<td class=\"data\">").append(minOccurs).append("</td>");
                sb.append("</tr>");
                sb.append("<tr>");
                    sb.append("<td>").append(MessageBundle.getString("parameters.maxOccurs")).append("</td>");
                    sb.append("<td class=\"data\">").append(maxOccurs).append("</td>");
                sb.append("</tr>");
            }
            
            sb.append("</tbody>");
            sb.append("</table>");
            sb.append("<br/>");
            
            if (description != null) {
                sb.append("<h3>").append(MessageBundle.getString("parameters.editorHelpDescriptionLabel")).append(" : ").append("</h3>");
                sb.append("<p>").append(description).append("</p>");
            }
            
            sb.append("<br/>");
            sb.append("<br/>");
            if (validationError != null) {
                sb.append("<div id=\"error\">");
                    sb.append("<h3>").append(MessageBundle.getString("parameters.editorHelpValidationErrorLabel")).append(" : ").append("</h3>");
                    sb.append("<p>").append(validationError).append("</p>");
                sb.append("</div>");
            }
            
            sb.append("</body>");
            sb.append("</html>");
            setText(sb.toString());
            this.revalidate();
        }
    }
    
}
