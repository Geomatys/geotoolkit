
package org.geotoolkit.observation.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.JTStoGeometry;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.gml.xml.GMLPeriod;
import org.geotoolkit.gml.xml.GMLInstant;
import org.geotoolkit.observation.OMUtils;
import org.geotoolkit.observation.xml.AbstractObservation;
import org.geotoolkit.observation.xml.OMXmlFactory;
import org.geotoolkit.sos.xml.ObservationOffering;
import org.geotoolkit.sos.xml.ResponseModeType;
import org.geotoolkit.sos.xml.SOSXmlFactory;
import static org.geotoolkit.sos.xml.SOSXmlFactory.*;
import org.geotoolkit.swe.xml.AbstractBoolean;
import org.geotoolkit.swe.xml.AbstractDataComponent;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.AbstractEncoding;
import org.geotoolkit.swe.xml.AbstractQualityProperty;
import org.geotoolkit.swe.xml.AbstractText;
import org.geotoolkit.swe.xml.AbstractTime;
import org.geotoolkit.swe.xml.AnyScalar;
import org.geotoolkit.swe.xml.DataArray;
import org.geotoolkit.swe.xml.DataArrayProperty;
import org.geotoolkit.swe.xml.DataComponentProperty;
import org.geotoolkit.swe.xml.DataRecord;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.geotoolkit.swe.xml.Quantity;
import org.geotoolkit.swe.xml.SimpleDataRecord;
import org.geotoolkit.swe.xml.TextBlock;
import org.geotoolkit.temporal.object.DefaultInstant;
import org.geotoolkit.temporal.object.DefaultPeriod;
import org.geotoolkit.temporal.object.DefaultTemporalPrimitive;
import org.geotoolkit.temporal.object.DefaultTemporalPosition;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.quality.Element;
import org.opengis.observation.Measure;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.temporal.TemporalPosition;
import org.opengis.util.FactoryException;

/**
 * Utility class that transform observation model to XML and vice versa.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ObservationTransformUtils {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.observation.model");

    public static AbstractObservation toXML(org.opengis.observation.Observation obs, String version) {
        if (obs instanceof Observation model) {
            String name = null;
            if (model.getName() != null) {
                name = model.getName().getCode();
            }
            FeatureProperty fProp = null;
            if (model.getFeatureOfInterest() != null) {
                fProp = OMXmlFactory.buildFeatureProperty(version, toXML(model.getFeatureOfInterest(), version));
            }
            List<Element> qualities = model.getResultQuality();
            if (model.getResult() instanceof ComplexResult) {
                return OMXmlFactory.buildObservation(version,
                                                    model.getId(),
                                                    name,
                                                    model.getDescription(),
                                                    fProp,
                                                    toXML(model.getObservedProperty(), version),
                                                    toXML(model.getProcedure(), version),
                                                    toXML(model.getResult(), version),
                                                    toXML(model.getSamplingTime(), version),
                                                    null,
                                                    qualities);
            } else if (model.getResult() instanceof MeasureResult) {
                return OMXmlFactory.buildMeasurement(version,
                                                    model.getId(),
                                                    name,
                                                    model.getDescription(),
                                                    fProp,
                                                    toXML(model.getObservedProperty(), version),
                                                    toXML(model.getProcedure(), version),
                                                    toXML(model.getResult(), version),
                                                    toXML(model.getSamplingTime(), version),
                                                    null,
                                                    qualities);
            } else {
                throw new IllegalArgumentException("Unsupported result implementation:" + model.getResult().getClass().getName());
            }
        } else if (obs instanceof org.geotoolkit.observation.xml.AbstractObservation aobs) {
            return aobs;
        } else if (obs == null) {
            return null;
        }
        throw new IllegalArgumentException("Unsupported implementation:" + obs.getClass().getName());
    }

    public static Object toXML(Result result, String version) {
        if (result instanceof MeasureResult meas) {
            return switch (meas.getField().type) {
                case QUANTITY -> SOSXmlFactory.buildMeasure(version, meas.getField().uom, (Number) meas.getValue());
                default       -> meas.getValue();
            };
        } else if (result instanceof ComplexResult cr) {
            List<AnyScalar> xmlFields = cr.getFields().stream().map(f -> getScalar(f, version)).toList();
            TextEncoderProperties textEnc = cr.getTextEncodingProperties();
            TextBlock tx = buildTextBlock(version, "encoding-1", textEnc.getTokenSeparator(), textEnc.getBlockSeparator(), textEnc.getDecimalSeparator());
            return buildComplexResult(version, xmlFields, cr.getNbValues(), tx, cr.getValues());
        } else if (result == null) {
            return null;
        }
        throw new IllegalArgumentException("Unsupported implementation:" + result.getClass().getName());
    }

    private static AnyScalar getScalar(final Field field, final String version) {
        final AbstractDataComponent compo = getComponent(field, version, false);
        return buildAnyScalar(version, null, field.name, compo);
    }

    private static AbstractDataComponent getComponent(final Field field, final String version, boolean nameAsId) {
        final List<AbstractQualityProperty> quality = new ArrayList<>();
        if (field.qualityFields != null) {
            for (Field qField : field.qualityFields) {
                quality.add(buildQualityProperty(version, getComponent(qField, version, true)));
            }
        }
        if (field.type == null) throw new IllegalStateException("Null type not allowed !");

       return switch(field.type) {
           case QUANTITY -> buildQuantity(version, nameAsId ? field.name : null, field.description, buildUomProperty(version, field.uom, null), null, quality);
           case TEXT     -> buildText(version,     nameAsId ? field.name : null, field.description, null, quality);
           case TIME     -> buildTime(version,     nameAsId ? field.name : null, field.description, null, quality);
           case BOOLEAN  -> buildBoolean(version,  nameAsId ? field.name : null, field.description, null, quality);
       };
    }

    public static DataArrayProperty buildComplexResult(final String version, final List<AnyScalar> fields, final Integer nbValue,
            final TextBlock encoding, final String values) {
        final String arrayID     = "dataArray-0" ;
        final String recordID    = "datarecord-0";
        final AbstractDataRecord record = buildSimpleDatarecord(version, null, recordID, null, null, fields);

        return buildDataArrayProperty(version, arrayID, nbValue, arrayID, record, encoding, values, null);
    }


    public static TemporalGeometricPrimitive toXML(TemporalGeometricPrimitive time, String version) {
        if (time instanceof GMLInstant || time instanceof GMLPeriod) {
            return time;
        } else  if (time instanceof Period model) {
            return SOSXmlFactory.buildTimePeriod(version, null, model.getBeginning(), model.getEnding());
        } else if(time instanceof DefaultInstant model) {
            return SOSXmlFactory.buildTimeInstant(version, null, model.getInstant());
        } else if (time == null) {
            return null;
        }
         throw new IllegalArgumentException("Unsupported implementation:" + time.getClass().getName());
    }

    public static org.opengis.observation.Process toXML(org.opengis.observation.Process proc, String version) {
        if (proc instanceof Procedure model) {
            return SOSXmlFactory.buildProcess(version, model.getId(), model.getName(), model.getDescription());
        } else if (proc instanceof  org.geotoolkit.observation.xml.Process) {
            return proc;
        } else if (proc == null) {
            return null;
        }
         throw new IllegalArgumentException("Unsupported implementation:" + proc.getClass().getName());
    }

    public static org.geotoolkit.swe.xml.Phenomenon toXML(org.opengis.observation.Phenomenon phen, String version) {
        if (phen instanceof CompositePhenomenon model) {
            List<org.geotoolkit.swe.xml.Phenomenon> components = new ArrayList<>();
            for (org.opengis.observation.Phenomenon compo : model.getComponent()) {
                components.add(toXML(compo, version));
            }
            return SOSXmlFactory.buildCompositePhenomenon(version, model.getId(), model.getName(), model.getDefinition(), model.getDescription(), components);
        } else if (phen instanceof Phenomenon model) {
            return SOSXmlFactory.buildPhenomenon(version, model.getId(), model.getName(), model.getDefinition(), model.getDescription());
        } else if (phen instanceof  org.geotoolkit.swe.xml.Phenomenon xmlp) {
            return xmlp;
        } else if (phen == null) {
            return null;
        }
        throw new IllegalArgumentException("Unsupported implementation:" + phen.getClass().getName());
    }

    public static org.geotoolkit.sampling.xml.SamplingFeature toXML(org.opengis.observation.sampling.SamplingFeature foi, String version) {
        String gmlVersion = SOSXmlFactory.getGMLVersion(version);
        if (foi instanceof SamplingFeature sfm) {
            FeatureProperty sampleFeatureProperty =  buildFeatureProperty(version, sfm.getSampledFeatureId());
            if (sfm.getGeometry() instanceof Point pt) {
                org.geotoolkit.gml.xml.Point location = null;
                try {
                    location =  (org.geotoolkit.gml.xml.Point) JTStoGeometry.toGML(gmlVersion, pt);
                } catch(FactoryException ex) {
                    LOGGER.log(Level.WARNING, "error while build foi gml point", ex);
                }
                return buildSamplingPoint(version, sfm.getId(), sfm.getName(), sfm.getDescription(), sampleFeatureProperty, location);
            } else if (sfm.getGeometry() instanceof LineString ls) {

                org.geotoolkit.gml.xml.LineString location = null;
                org.geotoolkit.gml.xml.Envelope env = null;
                try {
                    location =  (org.geotoolkit.gml.xml.LineString) JTStoGeometry.toGML(gmlVersion, ls);
                    JTSEnvelope2D env2D = JTS.toEnvelope(ls);
                    env = SOSXmlFactory.buildEnvelope(version, "bound-" + sfm.getId(), env2D.getMinX(), env2D.getMinY(), env2D.getMaxX(), env2D.getMaxY(), location.getSrsName());
                } catch(FactoryException ex) {
                    LOGGER.log(Level.WARNING, "error while build foi gml point", ex);
                }
                return buildSamplingCurve(version, sfm.getId(), sfm.getName(), sfm.getDescription(), sampleFeatureProperty, location, null, null, env);
            } else {
                return buildSamplingFeature(version, sfm.getId(), sfm.getName(), sfm.getDescription(), sampleFeatureProperty);
            }
        } else if (foi instanceof org.geotoolkit.sampling.xml.SamplingFeature sp) {
            return sp;
        } else if (foi == null) {
            return null;
        }
        throw new IllegalArgumentException("Unsupported implementation:" + foi.getClass().getName());
    }

    public static String SOSResultModel(QName rm) {
        if (rm.equals(OMUtils.OBSERVATION_QNAME)) {
            return "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Observation";
        } else if (rm.equals(OMUtils.MEASUREMENT_QNAME)) {
            return "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement";
        } else {
            LOGGER.warning("unexpected result model in offering:" + rm);
        }
        return null;

    }

    public static org.geotoolkit.sos.xml.ObservationOffering toXML(Offering off, List<String> procedureDescription, List<PhenomenonProperty> observedProperties, List<ResponseMode> responseModes,
            List<String> responseFormats, List<String> resultModels200, String version) {
        if (off != null) {
            List<QName> resultModels = new ArrayList<>();
            for (String rm : resultModels200) {
                if (rm.equals("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Observation")) {
                    resultModels.add(OMUtils.OBSERVATION_QNAME);
                } else if (rm.equals("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement")) {
                    resultModels.add(OMUtils.MEASUREMENT_QNAME);
                } else {
                    LOGGER.warning("unexpected result model in offering:" + rm);
                }
            }
            return SOSXmlFactory.buildOffering(version,
                                               off.getId(),
                                               off.getName(),
                                               off.getDescription(),
                                               off.getSrsNames(),
                                               toXML(off.getTime(), version),
                                               off.getProcedure(),
                                               observedProperties,
                                               off.getObservedProperties(),
                                               off.getFeatureOfInterestIds(),
                                               responseFormats,
                                               resultModels,
                                               resultModels200,
                                               responseModes.stream().map(rm -> ResponseModeType.fromValue(rm.value())).toList(),
                                               procedureDescription);
        }
        return null;
    }

    public static Procedure toModel(org.opengis.observation.Process process) {
        if (process instanceof org.geotoolkit.observation.xml.Process xmlProc) {
            return new Procedure(xmlProc.getHref(), xmlProc.getName(), xmlProc.getDescription(), null);
        } else if (process instanceof Procedure model) {
            return model;
        } else if (process == null) {
            return null;
        }
        throw new IllegalArgumentException("Unsupported implementation:" + process.getClass().getName());
    }

    public static Phenomenon toModel(org.opengis.observation.Phenomenon phen) {
        if (phen instanceof org.geotoolkit.swe.xml.CompositePhenomenon xmlPhen) {
            String definition = xmlPhen.getDefinition();
            String name = null;
            Identifier ident = xmlPhen.getName();
            if (ident != null) {
                if (definition != null) {
                    name = ident.getDescription() != null ? ident.getDescription().toString() : null;
                } else {
                    name = ident.getCode();
                }
            }
            List<Phenomenon> components = new ArrayList<>();
            for (org.opengis.observation.Phenomenon compo : xmlPhen.getComponent()) {
                components.add(toModel(compo));
            }
            return new CompositePhenomenon(xmlPhen.getId(), name, xmlPhen.getDefinition(), xmlPhen.getDescription(), null, components);
        }
        if (phen instanceof org.geotoolkit.swe.xml.Phenomenon xmlPhen) {
            String definition = xmlPhen.getDefinition();
            String name = null;
            Identifier ident = xmlPhen.getName();
            if (ident != null) {
                if (definition != null) {
                    name = ident.getDescription() != null ? ident.getDescription().toString() : null;
                } else {
                    name = ident.getCode();
                }
            }
            return new Phenomenon(xmlPhen.getId(), name, definition, xmlPhen.getDescription(), null);
        } else if (phen instanceof Phenomenon model) {
            return model;
        } else if (phen == null) {
            return null;
        }
        throw new IllegalArgumentException("Unsupported implementation:" + phen.getClass().getName());
    }

    public static Phenomenon toModel(final PhenomenonProperty phenProp) {
        if (phenProp.getHref() != null) {
            return new Phenomenon(phenProp.getHref(), phenProp.getHref(), null, null, null);
        } else if (phenProp.getPhenomenon() != null) {
            return toModel(phenProp.getPhenomenon());
        }
        return null;
    }

    public static SamplingFeature toModel(final FeatureProperty foiProp) {
        if (foiProp.getHref() != null) {
            return new SamplingFeature(foiProp.getHref(), foiProp.getHref(), null, null, null, null);
        } else if (foiProp.getAbstractFeature() != null) {
            return toModel((org.opengis.observation.sampling.SamplingFeature) foiProp.getAbstractFeature());
        }
        return null;
    }

    public static SamplingFeature toModel(final org.opengis.observation.sampling.SamplingFeature foi) {
        if (foi instanceof org.geotoolkit.sampling.xml.SamplingFeature sp) {
            String sfid = null;
            if (!sp.getSampledFeatures().isEmpty()) {
                sfid  = sp.getSampledFeatures().get(0).getHref();
            }
            String name = null;
            if (sp.getName() != null) {
                name = sp.getName().getCode();
            }
            Geometry geom = null;
            if (sp.getGeometry() instanceof AbstractGeometry gmlGeom) {
                try {
                    geom = GeometrytoJTS.toJTS(gmlGeom);
                    String srsName = gmlGeom.getSrsName();
                    CoordinateReferenceSystem crs;
                    if (srsName != null) {
                        crs = CRS.forCode(srsName);
                    } else {
                        crs = CommonCRS.WGS84.geographic();
                    }
                    JTS.setCRS(geom, crs);
                } catch (FactoryException ex) {
                    LOGGER.log(Level.WARNING, "error while building foi JTS geometry", ex);
                }
            } else if (sp.getGeometry() != null) {
                throw new IllegalArgumentException("Unexpected geometry implementation:" + sp.getGeometry().getClass().getName());
            }
            return new SamplingFeature(sp.getId(), name, sp.getDescription(), null, sfid, geom);
        } else if (foi instanceof SamplingFeature model) {
            return model;
        } else if (foi == null) {
            return null;
        }
        throw new IllegalArgumentException("Unsupported implementation:" + foi.getClass().getName());
    }

    public static Observation toModel(final org.opengis.observation.Observation obs) {
        if (obs instanceof org.geotoolkit.observation.xml.AbstractObservation aobs) {
            String name = null;
            if (aobs.getName() != null) {
                name = aobs.getName().getCode();
            }
            Phenomenon phenModel = toModel(aobs.getObservedProperty());

            // try to guess the observation type
            Map<String, Object> properties = new HashMap<>();
            boolean timeSeries = true;

            Result result;
            if (aobs.getResult() instanceof DataArrayProperty dap) {
                timeSeries = isTimeseries(dap);
                result = toModel(dap);

            } else if (aobs.getResult() != null) {
                Object value = aobs.getResult();
                FieldType ft;
                String uom = null;
                if (value instanceof Measure meas) {
                    uom = meas.getUom() != null ? meas.getUom().getUnitsSystem() : null;
                    ft  = FieldType.QUANTITY;
                    value = meas.getValue();
                } else if (aobs.getResult() instanceof Boolean) {
                    ft = FieldType.BOOLEAN;
                } else if (aobs.getResult() instanceof String) {
                    ft = FieldType.TEXT;
                } else if (aobs.getResult() instanceof Date || aobs.getResult() instanceof XMLGregorianCalendar) {
                    ft = FieldType.TIME;
                } else {
                    throw new IllegalArgumentException("Unexpected result type: " + aobs.getResult().getClass());
                }
                // todo extract descriptions from phenomenon?
                Field f = new Field(-1, ft, phenModel.getId(), phenModel.getId(), phenModel.getName(), uom);
                result = new MeasureResult(f, value);
            } else {
                // we can't know the field type here
                Field f = new Field(-1, FieldType.QUANTITY, phenModel.getId(), phenModel.getId(), phenModel.getName(), null);
                result = new MeasureResult(f, null);
            }
            if (timeSeries) {
                properties.put("type", "timeseries");
            } else {
                properties.put("type", "profile");
            }
            return new Observation(aobs.getId(),
                                   name,
                                   null,
                                   aobs.getDefinition(),
                                   aobs.getObservationType(),
                                   toModel(aobs.getProcedure()),
                                   toModel((TemporalGeometricPrimitive)aobs.getSamplingTime()),
                                   toModel(aobs.getPropertyFeatureOfInterest()),
                                   phenModel,
                                   aobs.getResultQuality(),
                                   result,
                                   properties);
        } else if (obs instanceof Observation model) {
            return model;
        } else if (obs == null) {
            return null;
        }
        throw new IllegalArgumentException("Unsupported implementation:" + obs.getClass().getName());
    }

    public static ComplexResult toModel(DataArrayProperty dap) {
        DataArray da = dap.getDataArray();
        if (da != null && da.getPropertyElementType() != null) {
            List<Field> fields = new ArrayList<>();
            if (da.getPropertyElementType().getAbstractRecord() instanceof DataRecord dr) {

                int i = 1;
                for (DataComponentProperty dcp : dr.getField()) {
                    AbstractDataComponent component = dcp.getValue();
                    fields.add(toModel(i, dcp.getName(), component));
                    i++;
                }
            } else if (da.getPropertyElementType().getAbstractRecord() instanceof SimpleDataRecord dr) {

                int i = 1;
                for (AnyScalar dcp : dr.getField()) {
                    AbstractDataComponent component = dcp.getValue();
                    fields.add(toModel(i, dcp.getName(), component));
                    i++;
                }
            } else {
                throw new IllegalArgumentException("Malformed result data Array: unexpected datarecord type.");
            }
            TextEncoderProperties tep = toModel(da.getEncoding());
            Integer nbValues = null;
            if (da.getElementCount() != null && da.getElementCount().getCount() != null) {
                nbValues = da.getElementCount().getCount().getValue();
            }
            return new ComplexResult(fields, tep, da.getValues(), nbValues);
        } else {
            throw new IllegalArgumentException("Malformed result data Array: missing Element Type.");
        }
    }

    private static boolean isTimeseries(DataArrayProperty dap) {
        DataArray da = dap.getDataArray();
        if (da != null && da.getPropertyElementType() != null) {
            List<Field> fields = new ArrayList<>();
            if (da.getPropertyElementType().getAbstractRecord() instanceof DataRecord dr) {

                if (!dr.getField().isEmpty()) {
                    return (dr.getField().get(0).getValue() instanceof AbstractTime);
                }
            } else if (da.getPropertyElementType().getAbstractRecord() instanceof SimpleDataRecord dr) {

                if (!dr.getField().isEmpty()) {
                    return (dr.getField().iterator().next().getValue() instanceof AbstractTime);
                }
            }
        }
        return true;
    }

    private static TextEncoderProperties toModel(AbstractEncoding encoding) {
        if (encoding instanceof TextBlock tb) {
            return new TextEncoderProperties(tb.getDecimalSeparator(),
                                             tb.getTokenSeparator(),
                                             tb.getBlockSeparator());
        } else if (encoding != null) {
            throw new IllegalArgumentException("Only text block encoding is supported");
        }
        return null;
    }

    private static Field toModel(int index, String id, AbstractDataComponent component) {
        List<Field> qualityFields = new ArrayList<>();
        String uom = null;
        FieldType ft = null;
        if (component instanceof Quantity q) {
            ft = FieldType.QUANTITY;
            if (q.getUom() != null) {
                uom = q.getUom().getCode();
            }
            if (q.getQuality() != null) {
                for (AbstractQualityProperty aqp : q.getQuality()) {
                    AbstractDataComponent dc = aqp.getDataComponent();
                    if (dc != null) {
                        qualityFields.add(toModel(index, dc.getId(), dc));
                    }
                }
            }
        } else if (component instanceof AbstractText) {
            ft = FieldType.TEXT;
        } else if (component instanceof AbstractBoolean) {
            ft = FieldType.BOOLEAN;
        } else if (component instanceof AbstractTime q) {
            ft = FieldType.TIME;
            if (q.getUom() != null) {
                uom = q.getUom().getCode();
            }
        } else {
            throw new IllegalArgumentException("Only Quantity, Text Boolean AND Time are supported for now");
        }
        return new Field(index, ft, id, component.getLabel(), component.getDefinition(), uom, qualityFields);
    }

    public static TemporalGeometricPrimitive toModel(final TemporalGeometricPrimitive gmlTime) {
        if (gmlTime instanceof org.geotoolkit.gml.xml.GMLInstant gmi) {
            String name = gmi.getId() != null ? gmi.getId() : UUID.randomUUID().toString() + "-time";
            if (gmi.getTimePosition() != null && gmi.getTimePosition().getIndeterminatePosition() != null) {
                TemporalPosition tp = new DefaultTemporalPosition(
                        CommonCRS.Temporal.JULIAN.crs(),
                        IndeterminateValue.valueOf(gmi.getTimePosition().getIndeterminatePosition().value().toUpperCase()));
                return new DefaultInstant(Collections.singletonMap(NAME_KEY, name), tp);
            }
            return new DefaultInstant(Collections.singletonMap(NAME_KEY, name), gmi.getInstant());
        } else if (gmlTime instanceof org.geotoolkit.gml.xml.GMLPeriod gmp) {
            String name = gmp.getId() != null ? gmp.getId() : UUID.randomUUID().toString() + "-time";
            return new DefaultPeriod(Collections.singletonMap(NAME_KEY, name),
                                    new DefaultInstant(gmp.getBeginning()),
                                    new DefaultInstant(gmp.getEnding()));
        } else if (gmlTime instanceof DefaultTemporalPrimitive) {
            return gmlTime;
        } else if (gmlTime == null) {
            return null;
        }
        throw new IllegalArgumentException("Unsupported implementation:" + gmlTime.getClass().getName());
    }

    public static Offering toModel(final ObservationOffering off) {
        if (off != null) {
            String name = null;
            if (off.getName() != null) {
                name = off.getName().getCode();
            }
            String procedure = null;
            if (!off.getProcedures().isEmpty()) {
                procedure = off.getProcedures().get(0);
            }
            return new Offering(off.getId(),
                                name,
                                off.getDescription(),
                                null,
                                off.getObservedArea(),
                                off.getSrsName(),
                                toModel(off.getTime()),
                                procedure,
                                off.getObservedProperties(),
                                off.getFeatureOfInterestIds());
        }
        return null;
    }
}
