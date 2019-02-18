/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v200.AdditionalParameter;
import org.geotoolkit.ows.xml.v200.AdditionalParametersType;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.ows.xml.v200.KeywordsType;
import org.geotoolkit.ows.xml.v200.LanguageStringType;
import org.geotoolkit.wps.json.FormatDescription;


/**
 * Description of a process Output.
 *
 * In this use, the Description shall describe a process output.


 <p>Java class for OutputDescription complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OutputDescription">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}Description">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/wps/2.0}DataDescription"/>
 *         &lt;element name="Output" type="{http://www.opengis.net/wps/2.0}OutputDescription" maxOccurs="unbounded"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "OutputDescriptionType", propOrder = {
    "dataDescription",
    "output"
})
public class OutputDescription extends Description {

    @XmlElementRef
    protected DataDescription dataDescription;
    @XmlElement(name = "Output")
    protected List<OutputDescription> output;

    public OutputDescription() {

    }

    public OutputDescription(CodeType identifier, LanguageStringType title, List<LanguageStringType> _abstract,
           List<KeywordsType>keywords, DataDescription dataDescription) {
        super(identifier, title, _abstract, keywords);
        this.dataDescription = dataDescription;
    }

    public OutputDescription(CodeType identifier, LanguageStringType title, LanguageStringType _abstract,
            KeywordsType keywords, DataDescription dataDescription) {
        super(identifier, title, _abstract, keywords);
        this.dataDescription = dataDescription;
    }

    public OutputDescription(CodeType identifier, LanguageStringType title, LanguageStringType _abstract,
            KeywordsType keywords, List<AdditionalParametersType> additionalParams, DataDescription dataDescription) {
        super(identifier, title, _abstract, keywords, additionalParams);
        this.dataDescription = dataDescription;
    }

    public OutputDescription(org.geotoolkit.wps.json.OutputDescription out) {
        super(out);
        // here we need to determine the kind of data we want from the format..
        // we do a wild guess for now between complex / literal
        List<Format> formats = new ArrayList<>();
        boolean complex = true; // default choice
        for (FormatDescription format : out.getFormats()) {
            if ("text/plain".equals(format.getMimeType())) {
                complex = false;
            }
            formats.add(new Format(format));
        }
        if (complex) {
            this.dataDescription = new ComplexData(formats);
        } else {
            this.dataDescription = new LiteralData(formats, null, null, null);
        }
    }

    public DataDescription getDataDescription() {
        return dataDescription;
    }

    /**
     * Gets the value of the output property.
     *
     */
    public List<OutputDescription> getOutput() {
        if (output == null) {
            output = new ArrayList<>();
        }
        return this.output;
    }

}
