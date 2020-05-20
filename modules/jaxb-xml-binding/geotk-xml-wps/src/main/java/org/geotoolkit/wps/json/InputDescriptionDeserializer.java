/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.wps.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class InputDescriptionDeserializer extends StdDeserializer<InputDescriptionBase> {

    public InputDescriptionDeserializer() {
        this(null);
    }

    public InputDescriptionDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public InputDescriptionBase deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        JsonNode bboxNode = node.get("supportedCRS");
        JsonNode formNode = node.get("formats");
        JsonNode litNode = node.get("literalDataDomains");

        if (bboxNode != null) {
            List<SupportedCrs> crss = new ArrayList<>();
            if (bboxNode instanceof ArrayNode) {
                ArrayNode an = (ArrayNode) bboxNode;
                Iterator<JsonNode> it = an.elements();
                while (it.hasNext()) {
                    JsonNode n = it.next();
                    JsonNode def = n.get("default");
                    JsonNode crs = n.get("crs");
                    SupportedCrs scrs = new SupportedCrs();
                    if (def instanceof BooleanNode) {
                        scrs.setDefault(((BooleanNode)def).booleanValue());
                    }
                    if (crs instanceof TextNode) {
                        scrs.setCrs((crs.asText()));
                    }
                    crss.add(scrs);
                }
            }
            return new BoundingBoxInputDescription(crss);
        } else if (formNode != null) {
            List<FormatDescription> formats = new ArrayList<>();
            if (formNode instanceof ArrayNode) {
                ArrayNode an = (ArrayNode) formNode;
                Iterator<JsonNode> it = an.elements();
                while (it.hasNext()) {
                    JsonNode n = it.next();
                    JsonNode def = n.get("default");
                    JsonNode mt = n.get("mimeType");
                    JsonNode sc = n.get("schema");
                    JsonNode en = n.get("encoding");
                    JsonNode mm = n.get("maximumMegabytes");
                    FormatDescription f = new FormatDescription();
                    if (def instanceof BooleanNode) {
                        f.setDefault(((BooleanNode)def).booleanValue());
                    }
                    if (mt instanceof TextNode) {
                        f.setMimeType((mt.asText()));
                    }
                    if (sc instanceof TextNode) {
                        f.setSchema((sc.asText()));
                    }
                    if (en instanceof TextNode) {
                        f.setEncoding((en.asText()));
                    }
                    if (mm instanceof IntNode) {
                        f.setMaximumMegabytes(((IntNode)mm).intValue());
                    }
                    formats.add(f);
                }
            }
            return new ComplexInputDescription(formats);
        } else {
            List<LiteralDataDomain> litDomains = new ArrayList<>();
            if (litNode instanceof ArrayNode) {
                ArrayNode an = (ArrayNode) litNode;
                Iterator<JsonNode> it = an.elements();
                while (it.hasNext()) {
                    JsonNode n = it.next();

                    LiteralDataDomain litDomain = new LiteralDataDomain();

                    litDomain.setDataType(readNameReference(n.get("dataType")));
                    litDomain.setUom(readNameReference(n.get("uom")));

                    JsonNode av = n.get("anyValue");
                    JsonNode vr = n.get("valuesReference");
                    JsonNode dv = n.get("defaultValue");
                    if (av instanceof BooleanNode) {
                        litDomain.setAnyValue(((BooleanNode)av).booleanValue());
                    }
                    if (vr instanceof TextNode) {
                        litDomain.setValuesReference((vr.asText()));
                    }
                    if (dv instanceof TextNode) {
                        litDomain.setDefaultValue((dv.asText()));
                    }
                    litDomain.setAllowedRanges(readAllowedRanges(n.get("allowedRanges")));
                    litDomain.setAllowedValues(readAllowedValues(n.get("allowedValues")));

                    litDomains.add(litDomain);
                }
            }
            return new LiteralInputDescription(litDomains);
        }
    }

    private NameReferenceType readNameReference(JsonNode n) {
        if (n != null) {
            String name = null;
            if (n.get("name") instanceof TextNode) {
                name = n.get("name").asText();
            }
            String href = null;
            if (n.get("reference") instanceof TextNode) {
                href = n.get("reference").asText();
            }
            return new NameReferenceType(name, href);
        }
        return null;
    }

    private AllowedValues readAllowedValues(JsonNode n) {
        if (n != null) {
            List<String> av = new ArrayList<>();
            if (n.get("allowedValues") instanceof ArrayNode) {
                ArrayNode an = (ArrayNode) n.get("allowedValues");
                Iterator<JsonNode> it = an.elements();
                while (it.hasNext()) {
                    JsonNode avn = it.next();
                    if (avn instanceof TextNode) {
                        av.add((avn.asText()));
                    }
                }
            }
            return new AllowedValues(av);
        }
        return null;
    }


    private AllowedRanges readAllowedRanges(JsonNode n) {
        if (n != null) {
            List<Range> av = new ArrayList<>();
            if (n.get("allowedRanges") instanceof ArrayNode) {
                ArrayNode an = (ArrayNode) n.get("allowedRanges");
                Iterator<JsonNode> it = an.elements();
                while (it.hasNext()) {
                    JsonNode ar = it.next();
                    Range r = new Range();
                    if (ar.get("minimumValue") instanceof TextNode) {
                        r.setMinimumValue((ar.get("minimumValue").asText()));
                    }
                    if (ar.get("spacing") instanceof TextNode) {
                        r.setSpacing((ar.get("spacing").asText()));
                    }
                    if (ar.get("maximumValue") instanceof TextNode) {
                        r.setMaximumValue((ar.get("maximumValue").asText()));
                    }
                    av.add(r);
                }
            }
            return new AllowedRanges(av);
        }
        return null;
    }
}


