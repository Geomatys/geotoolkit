/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.wps.xml;

import org.geotoolkit.ows.xml.Languages;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.ows.xml.AbstractCodeType;
import org.geotoolkit.ows.xml.AbstractContact;
import org.geotoolkit.ows.xml.AbstractOnlineResourceType;
import org.geotoolkit.ows.xml.AbstractOperationsMetadata;
import org.geotoolkit.ows.xml.AbstractResponsiblePartySubset;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;
import org.geotoolkit.ows.xml.AbstractServiceProvider;
import org.geotoolkit.ows.xml.AcceptVersions;
import org.geotoolkit.ows.xml.AllowedValues;
import org.geotoolkit.ows.xml.AnyValue;
import org.geotoolkit.ows.xml.BoundingBox;
import org.geotoolkit.ows.xml.DomainMetadata;
import org.geotoolkit.ows.xml.ExceptionResponse;
import org.geotoolkit.ows.xml.LanguageString;
import org.geotoolkit.ows.xml.OWSXmlFactory;
import org.geotoolkit.ows.xml.ValueReference;
import org.geotoolkit.wps.io.WPSIO;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class WPSXmlFactory {

    public static WPSCapabilities buildWPSCapabilities(final String version,  final AbstractServiceIdentification si, final AbstractServiceProvider sp,
            final AbstractOperationsMetadata om, final String updateSequence, ProcessOfferings po, final Languages lg) {

         if ("1.0.0".equals(version)) {
             
            if (si != null && !(si instanceof org.geotoolkit.ows.xml.v110.ServiceIdentification)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 service Identification.");
            }
            if (sp != null && !(sp instanceof org.geotoolkit.ows.xml.v110.ServiceProvider)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 service provider.");
            }
            if (om != null && !(om instanceof org.geotoolkit.ows.xml.v110.OperationsMetadata)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 operation metadata.");
            }
            if (po != null && !(po instanceof org.geotoolkit.wps.xml.v100.ProcessOfferings)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 process offerings.");
            }
            if (lg != null && !(lg instanceof org.geotoolkit.wps.xml.v100.Languages)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 language offerings.");
            }
            return new  org.geotoolkit.wps.xml.v100.WPSCapabilitiesType(
                       (org.geotoolkit.ows.xml.v110.ServiceIdentification)si,
                       (org.geotoolkit.ows.xml.v110.ServiceProvider)      sp,
                       (org.geotoolkit.ows.xml.v110.OperationsMetadata)   om,
                       version,
                       updateSequence,
                       (org.geotoolkit.wps.xml.v100.ProcessOfferings)     po,
                       (org.geotoolkit.wps.xml.v100.Languages)            lg,
                       null);
        } else if ("2.0.0".equals(version)) {
            
            if (si != null && !(si instanceof org.geotoolkit.ows.xml.v200.ServiceIdentification)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 service Identification.");
            }
            if (sp != null && !(sp instanceof org.geotoolkit.ows.xml.v200.ServiceProvider)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 service provider.");
            }
            if (om != null && !(om instanceof org.geotoolkit.ows.xml.v200.OperationsMetadata)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 operation metadata.");
            }
            if (po != null && !(po instanceof org.geotoolkit.wps.xml.v200.Contents)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 process offerings(contents).");
            }
            if (lg != null && !(lg instanceof org.geotoolkit.ows.xml.v200.CapabilitiesBaseType.Languages)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 language offerings.");
            }
            return new  org.geotoolkit.wps.xml.v200.WPSCapabilitiesType(
                       (org.geotoolkit.ows.xml.v200.ServiceIdentification)si,
                       (org.geotoolkit.ows.xml.v200.ServiceProvider)      sp,
                       (org.geotoolkit.ows.xml.v200.OperationsMetadata)   om,
                       version,
                       updateSequence,
                       (org.geotoolkit.wps.xml.v200.Contents)             po,
                       (org.geotoolkit.ows.xml.v200.CapabilitiesBaseType.Languages) lg);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }
    
    public static WPSCapabilities buildWPSCapabilities(final String version, final String updateSequence) {
         if ("1.0.0".equals(version)) {
            return new  org.geotoolkit.wps.xml.v100.WPSCapabilitiesType(
                       version,
                       updateSequence);
        } else if ("2.0.0".equals(version)) {
            
            return new  org.geotoolkit.wps.xml.v200.WPSCapabilitiesType(
                       version,
                       updateSequence);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }
    
    public static Languages buildLanguages(final String version, final String _default, final List<String> supported) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v100.Languages(_default, supported);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v200.CapabilitiesBaseType.Languages(supported);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static AbstractCodeType buildCode(final String version, final String value, final String codespace) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v110.CodeType(value, codespace);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v200.CodeType(value, codespace);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static ComplexDataType buildComplexDataType(final String version, final String encoding, final String mimeType, final String schema) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v100.ComplexDataType(encoding, mimeType, schema);
        } else if ("2.0.0".equals(version)) {
            final org.geotoolkit.wps.xml.v200.Format format = new org.geotoolkit.wps.xml.v200.Format(encoding, mimeType, schema, null, true);
            return new org.geotoolkit.wps.xml.v200.ComplexDataType(Arrays.asList(format));
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static Reference buildInOutReference(final String version, final WPSIO.IOType ioType) {
        if ("1.0.0".equals(version)) {
            if (ioType.equals(WPSIO.IOType.INPUT)) {
                return new org.geotoolkit.wps.xml.v100.InputReferenceType();
            } else {
                return new org.geotoolkit.wps.xml.v100.OutputReferenceType();
            }
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v200.ReferenceType();
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static Reference buildInputReference(final String version, final String encoding, final String mimeType, final String href) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v100.InputReferenceType(encoding, mimeType, href);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v200.ReferenceType();
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static Reference buildOutputReference(final String version, final String encoding, final String mimeType, final String href) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v100.OutputReferenceType(encoding, mimeType, href);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v200.ReferenceType();
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static ExecuteResponse buildExecuteResponse(final String version, final String service, final String lang, final String serviceInstance, final ProcessSummary processSum,
            List<? extends Input> inputs, List<DocumentOutputDefinition> outputs, List<DataOutput> dataOutput, StatusInfo status, final String jobId) {
        if ("1.0.0".equals(version)) {
            org.geotoolkit.wps.xml.v100.DataInputsType dataInputs = null;
            if (inputs != null) {
                List<org.geotoolkit.wps.xml.v100.InputType> in100 = new ArrayList<>();
                for (Input in : inputs) {
                    if (!(in instanceof org.geotoolkit.wps.xml.v100.InputType)) {
                        throw new IllegalArgumentException("Unexpected object class for 1.0.0 data input.");
                    }
                    in100.add((org.geotoolkit.wps.xml.v100.InputType)in);
                }
                dataInputs = new org.geotoolkit.wps.xml.v100.DataInputsType(in100);
            }
            org.geotoolkit.wps.xml.v100.OutputDefinitionsType outputDef = null;
            if (outputs != null) {
                List<org.geotoolkit.wps.xml.v100.DocumentOutputDefinitionType> out100 = new ArrayList<>();
                for (DocumentOutputDefinition out : outputs) {
                    if (!(out instanceof org.geotoolkit.wps.xml.v100.DocumentOutputDefinitionType)) {
                        throw new IllegalArgumentException("Unexpected object class for 1.0.0 output definitions.");
                    }
                    out100.add((org.geotoolkit.wps.xml.v100.DocumentOutputDefinitionType)out);
                }
                outputDef = new org.geotoolkit.wps.xml.v100.OutputDefinitionsType(out100);
            }
            if (processSum != null && !(processSum instanceof org.geotoolkit.wps.xml.v100.ProcessBriefType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 process brief.");
            }
            List<org.geotoolkit.wps.xml.v100.OutputDataType> outData100 = new ArrayList<>();
            if (dataOutput != null) {
                for (DataOutput po : dataOutput) {
                    if (!(po instanceof org.geotoolkit.wps.xml.v100.OutputDataType)) {
                        throw new IllegalArgumentException("Unexpected object class for 1.0.0 data outputput.");
                    }
                    outData100.add((org.geotoolkit.wps.xml.v100.OutputDataType)po);
                }
            }
            if (status != null && !(status instanceof org.geotoolkit.wps.xml.v100.StatusType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 process brief.");
            }
            return new org.geotoolkit.wps.xml.v100.ExecuteResponse(version, service, lang, serviceInstance, (org.geotoolkit.wps.xml.v100.ProcessBriefType)processSum, dataInputs, outputDef, outData100, (org.geotoolkit.wps.xml.v100.StatusType)status);
        
        } else if ("2.0.0".equals(version)) {
            List<org.geotoolkit.wps.xml.v200.DataOutputType> outData200 = new ArrayList<>();
            if (dataOutput != null) {
                for (DataOutput po : dataOutput) {
                    if (!(po instanceof org.geotoolkit.wps.xml.v200.DataOutputType)) {
                        throw new IllegalArgumentException("Unexpected object class for 2.0.0 data outputput.");
                    }
                    outData200.add((org.geotoolkit.wps.xml.v200.DataOutputType)po);
                }
            }
            return new org.geotoolkit.wps.xml.v200.Result(outData200, jobId);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static DataType buildDataType(final String version, Object content) {
        if ("1.0.0".equals(version)) {
            if (content instanceof org.geotoolkit.ows.xml.v110.BoundingBoxType) {
                return new org.geotoolkit.wps.xml.v100.DataType((org.geotoolkit.ows.xml.v110.BoundingBoxType)content);
            } else if (content instanceof org.geotoolkit.wps.xml.v100.ComplexDataType) {
                return new org.geotoolkit.wps.xml.v100.DataType((org.geotoolkit.wps.xml.v100.ComplexDataType)content);
            } else if (content instanceof org.geotoolkit.wps.xml.v100.LiteralDataType) {
                return new org.geotoolkit.wps.xml.v100.DataType((org.geotoolkit.wps.xml.v100.LiteralDataType)content);
            } else if (content == null){
                return new org.geotoolkit.wps.xml.v100.DataType();
            } else {
                throw new IllegalArgumentException("Unexpected Object for datatype content:" + content.getClass());
            }
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v200.Data(content);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static LiteralDataType buildLiteralDataValue(final String version, final String value, final String dataType, final String uom) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v100.LiteralDataType(value, dataType, uom);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v200.LiteralValue(value, dataType, uom);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static BoundingBox buildBoundingBoxDataValue(final String version, final Envelope env) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v110.BoundingBoxType(env);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v200.BoundingBoxType(env);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static DataOutput buildDataOutput(final String version, final String id, final LanguageString title, final LanguageString _abstract, Reference ref) {
        if ("1.0.0".equals(version)) {
            if (ref != null && !(ref instanceof org.geotoolkit.wps.xml.v100.OutputReferenceType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output reference.");
            }
            if (title != null && !(title instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output title.");
            }
            if (_abstract != null && !(_abstract instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output abstract.");
            }
            return new org.geotoolkit.wps.xml.v100.OutputDataType(
                 new org.geotoolkit.ows.xml.v110.CodeType(id), 
                    (org.geotoolkit.ows.xml.v110.LanguageStringType)title,
                    (org.geotoolkit.ows.xml.v110.LanguageStringType)_abstract, 
                    (org.geotoolkit.wps.xml.v100.OutputReferenceType)ref);
        } else if ("2.0.0".equals(version)) {
            if (ref != null && !(ref instanceof org.geotoolkit.wps.xml.v200.ReferenceType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 output reference.");
            }
            return new org.geotoolkit.wps.xml.v200.DataOutputType(id, (org.geotoolkit.wps.xml.v200.ReferenceType)ref);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static Input buildInput(final String version, final String id, final Reference title, final LanguageString _abstract, DataType data) {
        if ("1.0.0".equals(version)) {
            if (data != null && !(data instanceof org.geotoolkit.wps.xml.v100.DataType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output data.");
            }
            if (title != null && !(title instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output title.");
            }
            if (_abstract != null && !(_abstract instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output abstract.");
            }
            return new org.geotoolkit.wps.xml.v100.InputType(new org.geotoolkit.ows.xml.v110.CodeType(id), 
                    (org.geotoolkit.ows.xml.v110.LanguageStringType)title,
                    (org.geotoolkit.ows.xml.v110.LanguageStringType)_abstract, 
                    (org.geotoolkit.wps.xml.v100.DataType)data);
        } else if ("2.0.0".equals(version)) {
            if (data != null && !(data instanceof org.geotoolkit.wps.xml.v200.Data)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 output data.");
            }
            return new org.geotoolkit.wps.xml.v200.DataInputType(id, (org.geotoolkit.wps.xml.v200.Data)data);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static Input buildInput(final String version, final String id, final Reference title, final LanguageString _abstract, Reference ref) {
        if ("1.0.0".equals(version)) {
            if (ref != null && !(ref instanceof org.geotoolkit.wps.xml.v100.InputReferenceType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 input reference.");
            }
            if (title != null && !(title instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 input title.");
            }
            if (_abstract != null && !(_abstract instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 input abstract.");
            }
            return new org.geotoolkit.wps.xml.v100.InputType(new org.geotoolkit.ows.xml.v110.CodeType(id), 
                    (org.geotoolkit.ows.xml.v110.LanguageStringType)title,
                    (org.geotoolkit.ows.xml.v110.LanguageStringType)_abstract, 
                    (org.geotoolkit.wps.xml.v100.InputReferenceType)ref);
        } else if ("2.0.0".equals(version)) {
            if (ref != null && !(ref instanceof org.geotoolkit.wps.xml.v200.ReferenceType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 input reference.");
            }
            return new org.geotoolkit.wps.xml.v200.DataInputType(id, (org.geotoolkit.wps.xml.v200.ReferenceType)ref);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static DataOutput buildDataOutput(final String version, final String id, final LanguageString title, final LanguageString _abstract, DataType data) {
        if ("1.0.0".equals(version)) {
            if (data != null && !(data instanceof org.geotoolkit.wps.xml.v100.DataType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output data.");
            }
            if (title != null && !(title instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output title.");
            }
            if (_abstract != null && !(_abstract instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output abstract.");
            }
            return new org.geotoolkit.wps.xml.v100.OutputDataType(new org.geotoolkit.ows.xml.v110.CodeType(id), 
                    (org.geotoolkit.ows.xml.v110.LanguageStringType)title,
                    (org.geotoolkit.ows.xml.v110.LanguageStringType)_abstract, 
                    (org.geotoolkit.wps.xml.v100.DataType)data);
        } else if ("2.0.0".equals(version)) {
            if (data != null && !(data instanceof org.geotoolkit.wps.xml.v200.Data)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 output data.");
            }
            return new org.geotoolkit.wps.xml.v200.DataOutputType(id, (org.geotoolkit.wps.xml.v200.Data)data);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static LanguageString buildLanguageString(final String version, final String value, final String lang) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v110.LanguageStringType(value, lang);
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.ows.xml.v200.LanguageStringType(value, lang);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static ProcessSummary buildProcessSummary(final String version, final String id, final LanguageString title, final LanguageString _abstract, String processVersion) {
        if ("1.0.0".equals(version)) {
            if (title != null && !(title instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output title.");
            }
            if (_abstract != null && !(_abstract instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output abstract.");
            }
            return new org.geotoolkit.wps.xml.v100.ProcessBriefType(new org.geotoolkit.ows.xml.v110.CodeType(id), 
                                                                   (org.geotoolkit.ows.xml.v110.LanguageStringType) title,
                                                                   (org.geotoolkit.ows.xml.v110.LanguageStringType) _abstract,
                                                                   processVersion);
        } else if ("2.0.0".equals(version)) {
            List<org.geotoolkit.ows.xml.v200.LanguageStringType> titles = new ArrayList<>();
            if (title != null && !(title instanceof org.geotoolkit.ows.xml.v200.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 process title.");
            } else {
                titles.add((org.geotoolkit.ows.xml.v200.LanguageStringType) title);
            }
            List<org.geotoolkit.ows.xml.v200.LanguageStringType> abstracts = new ArrayList<>();
            if (_abstract != null && !(_abstract instanceof org.geotoolkit.ows.xml.v200.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 process abstract.");
            }else {
                abstracts.add((org.geotoolkit.ows.xml.v200.LanguageStringType) _abstract);
            }
            return new org.geotoolkit.wps.xml.v200.ProcessSummaryType(new org.geotoolkit.ows.xml.v200.CodeType(id), 
                                                                   titles,
                                                                   abstracts,
                                                                   null,
                                                                   processVersion);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static ProcessDescription buildProcessDescription(final String version, final AbstractCodeType id, final LanguageString title, final LanguageString _abstract, String processVersion,
            final boolean supportStorage, final boolean statusSupported, final List<InputDescription> inputs, final List<OutputDescription> outputs) {
        if ("1.0.0".equals(version)) {
            if (id != null && !(id instanceof org.geotoolkit.ows.xml.v110.CodeType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 id.");
            }
            if (title != null && !(title instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output title.");
            }
            if (_abstract != null && !(_abstract instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output abstract.");
            }
            List<org.geotoolkit.wps.xml.v100.InputDescriptionType> po100 = new ArrayList<>();
            for (InputDescription po : inputs) {
                if (!(po instanceof org.geotoolkit.wps.xml.v100.InputDescriptionType)) {
                    throw new IllegalArgumentException("Unexpected object class for 1.0.0 data input.");
                }
                po100.add((org.geotoolkit.wps.xml.v100.InputDescriptionType)po);
            }
            List<org.geotoolkit.wps.xml.v100.OutputDescriptionType> out100 = new ArrayList<>();
            for (OutputDescription po : outputs) {
                if (!(po instanceof org.geotoolkit.wps.xml.v100.OutputDescriptionType)) {
                    throw new IllegalArgumentException("Unexpected object class for 1.0.0 data output.");
                }
                out100.add((org.geotoolkit.wps.xml.v100.OutputDescriptionType)po);
            }
            return new org.geotoolkit.wps.xml.v100.ProcessDescriptionType((org.geotoolkit.ows.xml.v110.CodeType)id, 
                                                                   (org.geotoolkit.ows.xml.v110.LanguageStringType) title,
                                                                   (org.geotoolkit.ows.xml.v110.LanguageStringType) _abstract,
                                                                   processVersion,
                                                                   supportStorage,
                                                                   statusSupported,
                                                                   po100,
                                                                   out100);
        } else if ("2.0.0".equals(version)) {
            if (id != null && !(id instanceof org.geotoolkit.ows.xml.v200.CodeType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 id.");
            }
            List<org.geotoolkit.ows.xml.v200.LanguageStringType> titles = new ArrayList<>();
            if (title != null && !(title instanceof org.geotoolkit.ows.xml.v200.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 process title.");
            } else {
                titles.add((org.geotoolkit.ows.xml.v200.LanguageStringType) title);
            }
            List<org.geotoolkit.ows.xml.v200.LanguageStringType> abstracts = new ArrayList<>();
            if (_abstract != null && !(_abstract instanceof org.geotoolkit.ows.xml.v200.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 process abstract.");
            }else {
                abstracts.add((org.geotoolkit.ows.xml.v200.LanguageStringType) _abstract);
            }
            List<org.geotoolkit.wps.xml.v200.InputDescriptionType> po200 = new ArrayList<>();
            for (InputDescription po : inputs) {
                if (!(po instanceof org.geotoolkit.wps.xml.v200.InputDescriptionType)) {
                    throw new IllegalArgumentException("Unexpected object class for 2.0.0 data input.");
                }
                po200.add((org.geotoolkit.wps.xml.v200.InputDescriptionType)po);
            }
            List<org.geotoolkit.wps.xml.v200.OutputDescriptionType> out200 = new ArrayList<>();
            for (OutputDescription po : outputs) {
                if (!(po instanceof org.geotoolkit.wps.xml.v200.OutputDescriptionType)) {
                    throw new IllegalArgumentException("Unexpected object class for 2.0.0 data output.");
                }
                out200.add((org.geotoolkit.wps.xml.v200.OutputDescriptionType)po);
            }
            return new org.geotoolkit.wps.xml.v200.ProcessDescriptionType((org.geotoolkit.ows.xml.v200.CodeType)id, 
                                                                   titles,
                                                                   abstracts,
                                                                   null,
                                                                   po200,
                                                                   out200);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static ProcessOfferings buildProcessOfferings(final String version, List<ProcessSummary> processOfferings) {
        if ("1.0.0".equals(version)) {
            List<org.geotoolkit.wps.xml.v100.ProcessBriefType> po100 = new ArrayList<>();
            for (ProcessSummary po : processOfferings) {
                if (!(po instanceof org.geotoolkit.wps.xml.v100.ProcessBriefType)) {
                    throw new IllegalArgumentException("Unexpected object class for 1.0.0 process summary.");
                }
                po100.add((org.geotoolkit.wps.xml.v100.ProcessBriefType)po);
            }
            return new org.geotoolkit.wps.xml.v100.ProcessOfferings(po100);
        } else if ("2.0.0".equals(version)) {
            List<org.geotoolkit.wps.xml.v200.ProcessSummaryType> po200 = new ArrayList<>();
            for (ProcessSummary po : processOfferings) {
                if (!(po instanceof org.geotoolkit.wps.xml.v200.ProcessSummaryType)) {
                    throw new IllegalArgumentException("Unexpected object class for 2.0.0 process summary.");
                }
                po200.add((org.geotoolkit.wps.xml.v200.ProcessSummaryType)po);
            }
            return new org.geotoolkit.wps.xml.v200.Contents(po200);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static InputDescription buildInputDescription(final String version, final AbstractCodeType id, final LanguageString title, final LanguageString _abstract,
            final Integer minOccur, final String maxOccur, final DataDescription dataDescription) {
        
        if ("1.0.0".equals(version)) {
            if (id != null && !(id instanceof org.geotoolkit.ows.xml.v110.CodeType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 intput description id.");
            }
            if (title != null && !(title instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 intput description title.");
            }
            if (_abstract != null && !(_abstract instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 intput description abstract.");
            }
            Integer maxOccurNumber = null;
            if (maxOccur != null) {
                maxOccurNumber = Integer.parseInt(maxOccur);
            }
            if (dataDescription != null && 
              !((dataDescription instanceof org.geotoolkit.wps.xml.v100.SupportedComplexDataInputType) || 
                (dataDescription instanceof org.geotoolkit.wps.xml.v100.LiteralInputType) || 
                (dataDescription instanceof org.geotoolkit.wps.xml.v100.SupportedCRSsType))) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 intput data description.");
            }
            return new org.geotoolkit.wps.xml.v100.InputDescriptionType((org.geotoolkit.ows.xml.v110.CodeType)id,
                                                                        (org.geotoolkit.ows.xml.v110.LanguageStringType) title,
                                                                        (org.geotoolkit.ows.xml.v110.LanguageStringType) _abstract,
                                                                        minOccur,
                                                                        maxOccurNumber,
                                                                        dataDescription);
        } else if ("2.0.0".equals(version)) {
            if (id != null && !(id instanceof org.geotoolkit.ows.xml.v200.CodeType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 intput description id.");
            }
            List<org.geotoolkit.ows.xml.v200.LanguageStringType> titles = new ArrayList<>();
            if (title != null && !(title instanceof org.geotoolkit.ows.xml.v200.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 intput description title.");
            } else {
                titles.add((org.geotoolkit.ows.xml.v200.LanguageStringType) title);
            }
            List<org.geotoolkit.ows.xml.v200.LanguageStringType> abstracts = new ArrayList<>();
            if (_abstract != null && !(_abstract instanceof org.geotoolkit.ows.xml.v200.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 intput description abstract.");
            }else {
                abstracts.add((org.geotoolkit.ows.xml.v200.LanguageStringType) _abstract);
            }
            if (dataDescription != null && 
              !((dataDescription instanceof org.geotoolkit.wps.xml.v200.ComplexDataType) || 
                (dataDescription instanceof org.geotoolkit.wps.xml.v200.LiteralDataType) || 
                (dataDescription instanceof org.geotoolkit.wps.xml.v200.BoundingBoxData))) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 intput data description.");
            }
            return new org.geotoolkit.wps.xml.v200.InputDescriptionType((org.geotoolkit.ows.xml.v200.CodeType)id,
                                                                        titles,
                                                                        abstracts,
                                                                        null,
                                                                        minOccur,
                                                                        maxOccur,
                                                                        dataDescription);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static OutputDescription buildOutputDescription(final String version, final AbstractCodeType id, final LanguageString title, final LanguageString _abstract,
            final DataDescription dataDescription) {
        
        if ("1.0.0".equals(version)) {
            if (id != null && !(id instanceof org.geotoolkit.ows.xml.v110.CodeType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output description id.");
            }
            if (title != null && !(title instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output description title.");
            }
            if (_abstract != null && !(_abstract instanceof org.geotoolkit.ows.xml.v110.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output description abstract.");
            }
            if (dataDescription != null && 
              !((dataDescription instanceof org.geotoolkit.wps.xml.v100.SupportedComplexDataInputType) || 
                (dataDescription instanceof org.geotoolkit.wps.xml.v100.LiteralOutputType) || 
                (dataDescription instanceof org.geotoolkit.wps.xml.v100.SupportedCRSsType))) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output data description.");
            }
            return new org.geotoolkit.wps.xml.v100.OutputDescriptionType((org.geotoolkit.ows.xml.v110.CodeType)id,
                                                                        (org.geotoolkit.ows.xml.v110.LanguageStringType) title,
                                                                        (org.geotoolkit.ows.xml.v110.LanguageStringType) _abstract,
                                                                        dataDescription);
        } else if ("2.0.0".equals(version)) {
            if (id != null && !(id instanceof org.geotoolkit.ows.xml.v200.CodeType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 output description id.");
            }
            List<org.geotoolkit.ows.xml.v200.LanguageStringType> titles = new ArrayList<>();
            if (title != null && !(title instanceof org.geotoolkit.ows.xml.v200.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 output description title.");
            } else {
                titles.add((org.geotoolkit.ows.xml.v200.LanguageStringType) title);
            }
            List<org.geotoolkit.ows.xml.v200.LanguageStringType> abstracts = new ArrayList<>();
            if (_abstract != null && !(_abstract instanceof org.geotoolkit.ows.xml.v200.LanguageStringType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 output description abstract.");
            }else {
                abstracts.add((org.geotoolkit.ows.xml.v200.LanguageStringType) _abstract);
            }
            if (dataDescription != null && 
              !((dataDescription instanceof org.geotoolkit.wps.xml.v200.ComplexDataType) || 
                (dataDescription instanceof org.geotoolkit.wps.xml.v200.LiteralDataType) || 
                (dataDescription instanceof org.geotoolkit.wps.xml.v200.BoundingBoxData))) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 output data description.");
            }
            return new org.geotoolkit.wps.xml.v200.OutputDescriptionType((org.geotoolkit.ows.xml.v200.CodeType)id,
                                                                        titles,
                                                                        abstracts,
                                                                        null,
                                                                        dataDescription);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }

    public static Format buildComplexDataDescription(String version, String encoding, String mimetype, String schema, Integer maximumMegabytes) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v100.ComplexDataDescriptionType(encoding, mimetype, schema);
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.wps.xml.v200.Format(encoding, mimetype, schema, maximumMegabytes);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static ComplexDataTypeDescription buildComplexDataDescriptions(String version, Format _default, List<Format> formats, Integer maximumMegabytes) {
        if ("1.0.0".equals(version)) {
            if (_default != null && !(_default instanceof org.geotoolkit.wps.xml.v100.ComplexDataDescriptionType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 default description.");
            }
            List<org.geotoolkit.wps.xml.v100.ComplexDataDescriptionType> fo100 = new ArrayList<>();
            for (Format po : formats) {
                if (!(po instanceof org.geotoolkit.wps.xml.v100.ComplexDataDescriptionType)) {
                    throw new IllegalArgumentException("Unexpected object class for 1.0.0 description.");
                }
                fo100.add((org.geotoolkit.wps.xml.v100.ComplexDataDescriptionType)po);
            }
            return new org.geotoolkit.wps.xml.v100.SupportedComplexDataInputType((org.geotoolkit.wps.xml.v100.ComplexDataDescriptionType)_default, fo100, maximumMegabytes);
        } else if ("2.0.0".equals(version)) {
            List<org.geotoolkit.wps.xml.v200.Format> fo200 = new ArrayList<>();
            if (_default != null && !(_default instanceof org.geotoolkit.wps.xml.v200.Format)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 default format.");
            } else if (_default != null){
                ((org.geotoolkit.wps.xml.v200.Format)_default).setDefault(true);
                fo200.add((org.geotoolkit.wps.xml.v200.Format)_default);
            }
            for (Format po : formats) {
                if (!(po instanceof org.geotoolkit.wps.xml.v200.Format)) {
                    throw new IllegalArgumentException("Unexpected object class for 2.0.0 format.");
                }
                fo200.add((org.geotoolkit.wps.xml.v200.Format)po);
            }
           return new org.geotoolkit.wps.xml.v200.ComplexDataType(fo200);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static Object buildUOMS(String version, DomainMetadata _default, List<DomainMetadata> supported) {
        if ("1.0.0".equals(version)) {
            if (_default != null && !(_default instanceof org.geotoolkit.ows.xml.v110.DomainMetadataType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 default uom.");
            }
            List<org.geotoolkit.ows.xml.v110.DomainMetadataType> po200 = new ArrayList<>();
            for (DomainMetadata po : supported) {
                if (!(po instanceof org.geotoolkit.ows.xml.v110.DomainMetadataType)) {
                    throw new IllegalArgumentException("Unexpected object class for 1.0.0 uom.");
                }
                po200.add((org.geotoolkit.ows.xml.v110.DomainMetadataType)po);
            }
            return new org.geotoolkit.wps.xml.v100.SupportedUOMsType((org.geotoolkit.ows.xml.v110.DomainMetadataType)_default,po200 );
        } else if ("2.0.0".equals(version)) {
           if (_default != null && !(_default instanceof org.geotoolkit.ows.xml.v200.DomainMetadataType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 default uom.");
            }
           return _default;
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static DomainMetadata buildDomainMetadata(String version, String value, String reference) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v110.DomainMetadataType(value, reference);
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.ows.xml.v200.DomainMetadataType(value, reference);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static AnyValue buildAnyValue(String version) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v110.AnyValue();
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.ows.xml.v200.AnyValue();
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static AllowedValues buildAllowedValues(String version, Collection values) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v110.AllowedValues(values);
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.ows.xml.v200.AllowedValues(values);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static AcceptVersions buildAcceptVersions(String version, String... versions) {
        if ("1.0.0".equals(version)) {
           return new org.geotoolkit.ows.xml.v110.AcceptVersionsType(versions);
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.ows.xml.v200.AcceptVersionsType(versions);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static LiteralDataDescription buildLiteralInputDataDescription(String version, DomainMetadata dataType, Object uoMs, AllowedValues allowedValues, 
            AnyValue anyValue, ValueReference valuesReference, String defaultValue) {
        if ("1.0.0".equals(version)) {
            if (dataType != null && !(dataType instanceof org.geotoolkit.ows.xml.v110.DomainMetadataType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 dataType.");
            }
            if (uoMs != null && !(uoMs instanceof org.geotoolkit.wps.xml.v100.SupportedUOMsType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 uom.");
            }
            if (allowedValues != null && !(allowedValues instanceof org.geotoolkit.ows.xml.v110.AllowedValues)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 allowedValues.");
            }
            if (anyValue != null && !(anyValue instanceof org.geotoolkit.ows.xml.v110.AnyValue)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 anyValue.");
            }
            if (valuesReference != null && !(valuesReference instanceof org.geotoolkit.wps.xml.v100.ValuesReferenceType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 valuesReference.");
            }
            return new org.geotoolkit.wps.xml.v100.LiteralInputType((org.geotoolkit.ows.xml.v110.DomainMetadataType)dataType,
                                                                    (org.geotoolkit.wps.xml.v100.SupportedUOMsType)uoMs,
                                                                    (org.geotoolkit.ows.xml.v110.AllowedValues)allowedValues,
                                                                    (org.geotoolkit.ows.xml.v110.AnyValue)anyValue,
                                                                    (org.geotoolkit.wps.xml.v100.ValuesReferenceType)valuesReference,
                                                                    defaultValue);
        } else if ("2.0.0".equals(version)) {
            if (dataType != null && !(dataType instanceof org.geotoolkit.ows.xml.v200.DomainMetadataType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 dataType.");
            }
            if (uoMs != null && !(uoMs instanceof org.geotoolkit.ows.xml.v200.DomainMetadataType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 uom.");
            }
            if (allowedValues != null && !(allowedValues instanceof org.geotoolkit.ows.xml.v200.AllowedValues)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 allowedValues.");
            }
            if (anyValue != null && !(anyValue instanceof org.geotoolkit.ows.xml.v200.AnyValue)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 anyValue.");
            }
            if (valuesReference != null && !(valuesReference instanceof org.geotoolkit.ows.xml.v200.ValuesReference)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 valuesReference.");
            }
            return new org.geotoolkit.wps.xml.v200.LiteralDataType((org.geotoolkit.ows.xml.v200.AllowedValues)allowedValues,
                                                                   (org.geotoolkit.ows.xml.v200.AnyValue)anyValue,
                                                                   (org.geotoolkit.ows.xml.v200.ValuesReference)valuesReference,
                                                                   (org.geotoolkit.ows.xml.v200.DomainMetadataType)dataType,
                                                                   (org.geotoolkit.ows.xml.v200.DomainMetadataType)uoMs,
                                                                   defaultValue, null);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static LiteralDataDescription buildLiteralOutputDataDescription(String version, DomainMetadata dataType, Object uoMs) {
        if ("1.0.0".equals(version)) {
            if (dataType != null && !(dataType instanceof org.geotoolkit.ows.xml.v110.DomainMetadataType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 dataType.");
            }
            if (uoMs != null && !(uoMs instanceof org.geotoolkit.wps.xml.v100.SupportedUOMsType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 uom.");
            }
            return new org.geotoolkit.wps.xml.v100.LiteralOutputType((org.geotoolkit.ows.xml.v110.DomainMetadataType)dataType,
                                                                    (org.geotoolkit.wps.xml.v100.SupportedUOMsType)uoMs);
        } else if ("2.0.0".equals(version)) {
            if (dataType != null && !(dataType instanceof org.geotoolkit.ows.xml.v200.DomainMetadataType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 dataType.");
            }
            if (uoMs != null && !(uoMs instanceof org.geotoolkit.ows.xml.v200.DomainMetadataType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 uom.");
            }
            return new org.geotoolkit.wps.xml.v200.LiteralDataType((org.geotoolkit.ows.xml.v200.DomainMetadataType)dataType,
                                                                   (org.geotoolkit.ows.xml.v200.DomainMetadataType)uoMs);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static ProcessOfferings buildProcessOfferings(String version, String lang, List<ProcessDescription> descriptions) {
        if ("1.0.0".equals(version)) {
            List<org.geotoolkit.wps.xml.v100.ProcessDescriptionType> po100 = new ArrayList<>();
            for (ProcessDescription po : descriptions) {
                if (!(po instanceof org.geotoolkit.wps.xml.v100.ProcessDescriptionType)) {
                    throw new IllegalArgumentException("Unexpected object class for 1.0.0 process description.");
                }
                po100.add((org.geotoolkit.wps.xml.v100.ProcessDescriptionType)po);
            }
            return new org.geotoolkit.wps.xml.v100.ProcessDescriptions(lang, po100);
        } else if ("2.0.0".equals(version)) {
            List<org.geotoolkit.wps.xml.v200.ProcessOffering> po200 = new ArrayList<>();
            for (ProcessDescription po : descriptions) {
                if (!(po instanceof org.geotoolkit.wps.xml.v200.ProcessDescriptionType)) {
                    throw new IllegalArgumentException("Unexpected object class for 2.0.0 process description.");
                }
                org.geotoolkit.wps.xml.v200.ProcessOffering poff = new org.geotoolkit.wps.xml.v200.ProcessOffering((org.geotoolkit.wps.xml.v200.ProcessDescriptionType)po);
                po200.add(poff);
            }
            return new org.geotoolkit.wps.xml.v200.ProcessOfferings(po200);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }

    public static DocumentOutputDefinition buildOutputDefinition(String version, String id, Boolean asReference) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v100.DocumentOutputDefinitionType(new org.geotoolkit.ows.xml.v110.CodeType(id), asReference);
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.wps.xml.v200.OutputDefinitionType(id, asReference);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static StatusInfo buildStatusInfoAccepted(String version, XMLGregorianCalendar creationDate, String acceptedStatus, String jobId) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v100.StatusType(creationDate, acceptedStatus, null);
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.wps.xml.v200.StatusInfo(acceptedStatus, jobId);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static StatusInfo buildStatusInfoFailed(String version, XMLGregorianCalendar creationDate, ExceptionResponse exceptionReport, String jobId) {
        if ("1.0.0".equals(version)) {
            if (exceptionReport != null && !(exceptionReport instanceof org.geotoolkit.ows.xml.v110.ExceptionReport)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 exception report.");
            }
            
            return new org.geotoolkit.wps.xml.v100.StatusType(creationDate, (org.geotoolkit.ows.xml.v110.ExceptionReport) exceptionReport);
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.wps.xml.v200.StatusInfo("Process failed:" + exceptionReport.toString(), jobId);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static StatusInfo buildStatusInfoPaused(String version, XMLGregorianCalendar creationDate, Integer progression, String msg, String jobId) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v100.StatusType(creationDate, null, new org.geotoolkit.wps.xml.v100.ProcessStartedType(msg, progression));
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.wps.xml.v200.StatusInfo(msg, progression, jobId);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static StatusInfo buildStatusInfoStarted(String version, XMLGregorianCalendar creationDate, Integer progression, String msg, String jobId) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v100.StatusType(creationDate, new org.geotoolkit.wps.xml.v100.ProcessStartedType(msg, progression), null);
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.wps.xml.v200.StatusInfo(msg, progression, jobId);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static ExceptionResponse buildExceptionReport(String version, final String exceptionText, final String exceptionCode, final String locator, final String exVersion) {
        if ("1.0.0".equals(version)) {
           return new org.geotoolkit.ows.xml.v110.ExceptionReport(exceptionText, exceptionCode, locator, version);
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.ows.xml.v200.ExceptionReport(exceptionText, exceptionCode, locator, version);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static StatusInfo buildStatusInfoSucceed(String version, XMLGregorianCalendar creationDate, String succeedStatus, String jobId) {
        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wps.xml.v100.StatusType(creationDate, null, succeedStatus);
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.wps.xml.v200.StatusInfo(succeedStatus, jobId);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }

    public static Execute buildExecuteRequest(String version, String language, String identifier, List<Input> inputs, boolean isRaw, 
            Boolean storeExecuteResponse, Boolean lineage, Boolean status, List<OutputDefinition> outputs) {
        
        if ("1.0.0".equals(version)) {
            org.geotoolkit.wps.xml.v100.DataInputsType dataInputs = null;
            if (inputs != null) {
                List<org.geotoolkit.wps.xml.v100.InputType> in100 = new ArrayList<>();
                for (Input in : inputs) {
                    if (!(in instanceof org.geotoolkit.wps.xml.v100.InputType)) {
                        throw new IllegalArgumentException("Unexpected object class for 1.0.0 data input.");
                    }
                    in100.add((org.geotoolkit.wps.xml.v100.InputType)in);
                }
                dataInputs = new org.geotoolkit.wps.xml.v100.DataInputsType(in100);
            }
            
            org.geotoolkit.wps.xml.v100.ResponseFormType responseForm;
            // raw data
            if (isRaw) {
                if (!outputs.isEmpty()) {
                    OutputDefinition outRaw = outputs.get(0);
                    if (outRaw != null && !(outRaw instanceof org.geotoolkit.wps.xml.v100.OutputDefinitionType)) {
                        throw new IllegalArgumentException("Unexpected object class for 1.0.0 out definition raw type.");
                    }
                    responseForm = new org.geotoolkit.wps.xml.v100.ResponseFormType((org.geotoolkit.wps.xml.v100.OutputDefinitionType)outRaw);
                } else {
                    throw new IllegalArgumentException("Raw data mode must specifiy one output");
                }
            } else {
                List<org.geotoolkit.wps.xml.v100.DocumentOutputDefinitionType> out100 = new ArrayList<>();
                for (OutputDefinition out : outputs) {
                    if (!(out instanceof org.geotoolkit.wps.xml.v100.DocumentOutputDefinitionType)) {
                        throw new IllegalArgumentException("Unexpected object class for 1.0.0 document output.");
                    }
                    out100.add((org.geotoolkit.wps.xml.v100.DocumentOutputDefinitionType)out);
                }
                org.geotoolkit.wps.xml.v100.ResponseDocumentType responseDocument = new org.geotoolkit.wps.xml.v100.ResponseDocumentType(storeExecuteResponse, lineage, status, out100);
                responseForm = new org.geotoolkit.wps.xml.v100.ResponseFormType(responseDocument);
            }
            
            return new org.geotoolkit.wps.xml.v100.Execute(language, new org.geotoolkit.ows.xml.v110.CodeType(identifier), dataInputs, responseForm);
            
        } else if ("2.0.0".equals(version)) {
            List<org.geotoolkit.wps.xml.v200.DataInputType> in200 = new ArrayList<>();
            for (Input in : inputs) {
                if (!(in instanceof org.geotoolkit.wps.xml.v200.DataInputType)) {
                    throw new IllegalArgumentException("Unexpected object class for 1.0.0 data input.");
                }
                in200.add((org.geotoolkit.wps.xml.v200.DataInputType)in);
            }
            List<org.geotoolkit.wps.xml.v200.OutputDefinitionType> out200 = new ArrayList<>();
            for (OutputDefinition out : outputs) {
                if (!(out instanceof org.geotoolkit.wps.xml.v200.OutputDefinitionType)) {
                    throw new IllegalArgumentException("Unexpected object class for 1.0.0 data output.");
                }
                out200.add((org.geotoolkit.wps.xml.v200.OutputDefinitionType)out);
            }
            String response = "document";
            if (isRaw) {
                response = "raw";
            }
           return new org.geotoolkit.wps.xml.v200.ExecuteRequestType(new org.geotoolkit.ows.xml.v200.CodeType(identifier), in200, out200, response);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }

    public static GetCapabilities buildGetCapabilities(String version, String service, String language, String updateSequence, AcceptVersions versions) {
        if ("1.0.0".equals(version)) {
            if (versions != null && !(versions instanceof org.geotoolkit.ows.xml.v110.AcceptVersionsType)) {
                throw new IllegalArgumentException("Unexpected object class for 1.0.0 acceptversion.");
            }
           return new org.geotoolkit.wps.xml.v100.GetCapabilities(service, language, updateSequence, (org.geotoolkit.ows.xml.v110.AcceptVersionsType)versions);
        } else if ("2.0.0".equals(version)) {
            if (versions != null && !(versions instanceof org.geotoolkit.ows.xml.v200.AcceptVersionsType)) {
                throw new IllegalArgumentException("Unexpected object class for 2.0.0 acceptversion.");
            }
           return new org.geotoolkit.wps.xml.v200.GetCapabilitiesType((org.geotoolkit.ows.xml.v200.AcceptVersionsType)versions, null, null, updateSequence, service);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }

    public static DescribeProcess buildDescribeProcess(String version, String service, String language, List<String> identifiers) {
        if ("1.0.0".equals(version)) {
            List<org.geotoolkit.ows.xml.v110.CodeType> id100 = new ArrayList<>();
            for (String id : identifiers) {
                id100.add(new org.geotoolkit.ows.xml.v110.CodeType(id));
            }
            return new org.geotoolkit.wps.xml.v100.DescribeProcess(service, language, id100);
        } else if ("2.0.0".equals(version)) {
            List<org.geotoolkit.ows.xml.v200.CodeType> id200 = new ArrayList<>();
            for (String id : identifiers) {
                id200.add(new org.geotoolkit.ows.xml.v200.CodeType(id));
            }
            return new org.geotoolkit.wps.xml.v200.DescribeProcess(service, language, id200);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }

    public static AbstractServiceIdentification buildServiceIdentification(String version, String name, String description, List<String> keywords, String service, List<String> versions, String fees, List<String> accessConstraint) {
        if ("1.0.0".equals(version)) {
            return OWSXmlFactory.buildServiceIdentification("1.1.0", name, service, keywords, service, versions, fees, versions);
        } else if ("2.0.0".equals(version)) {
            return OWSXmlFactory.buildServiceIdentification("2.0.0", name, service, keywords, service, versions, fees, versions);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }

    public static AbstractContact buildContact(String version, String phone, String fax, String email, String address, String city, String state, String zipCode, String country, String hoursOfService, String contactInstructions) {
        if ("1.0.0".equals(version)) {
            return OWSXmlFactory.buildContact("1.1.0", phone, fax, email, address, city, state, zipCode, country, hoursOfService, contactInstructions);
        } else if ("2.0.0".equals(version)) {
            return OWSXmlFactory.buildContact("2.0.0", phone, fax, email, address, city, state, zipCode, country, hoursOfService, contactInstructions);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }

    public static AbstractResponsiblePartySubset buildResponsiblePartySubset(String version, String fullname, String position, AbstractContact contact, String role) {
        if ("1.0.0".equals(version)) {
            return OWSXmlFactory.buildResponsiblePartySubset("1.1.0", fullname, position, contact, role);
        } else if ("2.0.0".equals(version)) {
            return OWSXmlFactory.buildResponsiblePartySubset("2.0.0", fullname, position, contact, role);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }

    public static AbstractOnlineResourceType buildOnlineResource(String version, String url) {
        if ("1.0.0".equals(version)) {
            return OWSXmlFactory.buildOnlineResource("1.1.0", url);
        } else if ("2.0.0".equals(version)) {
            return OWSXmlFactory.buildOnlineResource("2.0.0", url);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }

    public static AbstractServiceProvider buildServiceProvider(String version, String organisation, AbstractOnlineResourceType orgUrl, AbstractResponsiblePartySubset responsible) {
         if ("1.0.0".equals(version)) {
            return OWSXmlFactory.buildServiceProvider("1.1.0", organisation, orgUrl, responsible);
        } else if ("2.0.0".equals(version)) {
            return OWSXmlFactory.buildServiceProvider("2.0.0", organisation, orgUrl, responsible);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static GetResult buildGetResult(String version, String service, String jobId) {
        if ("1.0.0".equals(version)) {
            throw new IllegalArgumentException("GetResult operation does not exist in WPS 1.0.0");
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.wps.xml.v200.GetResult(service, jobId);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }
    
    public static GetStatus buildGetStatus(String version, String service, String jobId) {
        if ("1.0.0".equals(version)) {
            throw new IllegalArgumentException("GetResult operation does not exist in WPS 1.0.0");
        } else if ("2.0.0".equals(version)) {
           return new org.geotoolkit.wps.xml.v200.GetStatus(service, jobId);
        }
        throw new IllegalArgumentException("Unexpected version:" + version + " expecting 1.0.0 or 2.0.0");
    }

}
