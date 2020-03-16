/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.gui.javafx.process;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import org.geotoolkit.process.ProcessingRegistry;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.identification.ServiceIdentification;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXRegistryPane extends BorderPane {

    private final SimpleObjectProperty<ProcessingRegistry> valueProperty = new SimpleObjectProperty<>();

    private final WebView browser = new WebView();


    public FXRegistryPane() {

        setCenter(browser);

        valueProperty.addListener((ObservableValue<? extends ProcessingRegistry> ov, ProcessingRegistry t, ProcessingRegistry t1) -> {
            update(t1);
        });
    }

    public Property<ProcessingRegistry> valueProperty() {
        return valueProperty;
    }

    private void update(ProcessingRegistry desc) {

        if (desc == null) {
            browser.getEngine().loadContent("");
            return;
        }


        final Identification identification = desc.getIdentification();
        final Citation citation = identification.getCitation();
        final InternationalString abs = identification.getAbstract();

        if (identification instanceof ServiceIdentification) {
            ServiceIdentification si = (ServiceIdentification) identification;
        }

        final StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        if (citation != null) {
            for (Identifier id : citation.getIdentifiers()) {
                sb.append("<h1>").append(id.getCode()).append("</h1>\n");
            }
        }
        if (abs != null) {
            sb.append("<p>").append(abs).append("</p>\n");
        }

        sb.append("</body></html>");
        browser.getEngine().loadContent(sb.toString());

    }
}
