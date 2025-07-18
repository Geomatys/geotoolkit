
package org.geotoolkit.observation;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.sis.storage.DataStoreException;
import static org.geotoolkit.observation.OMUtils.EVENT_TIME;
import org.geotoolkit.observation.model.OMEntity;
import org.geotoolkit.observation.model.Offering;
import org.geotoolkit.observation.model.ResponseMode;
import org.geotoolkit.observation.model.ResultMode;
import org.geotoolkit.observation.query.AbstractObservationQuery;
import org.geotoolkit.observation.query.HistoricalLocationQuery;
import org.geotoolkit.observation.query.ObservationQuery;
import org.geotoolkit.observation.query.ObservedPropertyQuery;
import org.geotoolkit.observation.query.ResultQuery;
import static org.geotoolkit.ows.xml.OWSExceptionCode.INVALID_PARAMETER_VALUE;
import static org.geotoolkit.observation.AbstractObservationStoreFactory.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.Literal;
import org.opengis.filter.TemporalOperator;
import org.opengis.filter.TemporalOperatorName;
import org.opengis.geometry.Envelope;
import org.geotoolkit.observation.model.Observation;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.Procedure;
import org.geotoolkit.observation.model.SamplingFeature;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalPrimitive;
import static org.apache.sis.temporal.TemporalDate.toDate;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.opengis.filter.ComparisonOperator;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractObservationFilterReader implements ObservationFilterReader {

    protected final String observationIdBase;
    protected final String phenomenonIdBase;
    protected final String sensorIdBase;
    protected final String observationTemplateIdBase;

    /**
     * Requested entity type.
     */
    protected OMEntity entityType;

    // special query flags set in init methods
    protected boolean noCompositePhenomenon;
    protected boolean includeFoiInTemplate;
    protected boolean includeTimeInTemplate;
    protected boolean includeIDInDataBlock;
    protected boolean includeTimeForProfile;
    protected boolean includeQualityFields;
    protected boolean separatedMeasure;
    protected boolean separatedProfileObs;

    protected String responseFormat;

    protected ResultMode resultMode;
    protected ResponseMode responseMode;
    protected QName resultModel;
    protected Integer decimationSize;

    protected String currentProcedure;

    // paging parameters extracted from query
    protected Long limit;
    protected Long offset;

    // Query like parameters, from setter methodes
    protected final List<String> measureIdFilter     = new ArrayList<>();
    protected final List<String> procedures          = new ArrayList<>();
    protected final List<String> observedPhenomenons = new ArrayList<>();
    protected final List<String> featureOfInterests  = new ArrayList<>();
    protected final List<String> offerings           = new ArrayList<>();
    protected final List<String> observationIds      = new ArrayList<>();

    protected Date startTime;
    protected Date endTime;
    protected Envelope boundingbox;
    protected ComparisonOperator resultFilter;
    protected ComparisonOperator propertiesFilter;
    protected String procedureType;

    protected static final GeometryFactory GF = new GeometryFactory();

    public AbstractObservationFilterReader(final Map<String, Object> properties) {
        this.phenomenonIdBase          = (String) properties.get(PHENOMENON_ID_BASE_NAME);
        this.observationTemplateIdBase = (String) properties.getOrDefault(OBSERVATION_TEMPLATE_ID_BASE_NAME, "urn:observation:template:");
        this.observationIdBase         = (String) properties.getOrDefault(OBSERVATION_ID_BASE_NAME, "");
        this.sensorIdBase              = (String) properties.getOrDefault(SENSOR_ID_BASE_NAME, "");
    }

    @Override
    public void init(AbstractObservationQuery aquery) throws DataStoreException {
        this.entityType = aquery.getEntityType();
        if (aquery instanceof ObservationQuery query) {
            this.includeTimeInTemplate = query.isIncludeTimeInTemplate();
            this.responseMode          = query.getResponseMode();
            this.resultModel           = query.getResultModel();
            this.includeFoiInTemplate  = query.isIncludeFoiInTemplate();
            this.includeTimeForProfile = query.isIncludeTimeForProfile();
            this.includeIDInDataBlock  = query.isIncludeIdInDataBlock();
            this.includeQualityFields  = query.isIncludeQualityFields();
            this.separatedMeasure      = query.isSeparatedMeasure();
            this.separatedProfileObs   = query.isSeparatedProfileObservation();
            this.resultMode            = query.getResultMode();
            this.responseFormat        = query.getResponseFormat();
            this.decimationSize        = query.getDecimationSize();
        } else if (aquery instanceof ResultQuery query) {
            this.includeTimeForProfile = query.isIncludeTimeForProfile();
            this.responseMode          = query.getResponseMode();
            this.currentProcedure      = query.getProcedure();
            this.includeIDInDataBlock  = query.isIncludeIdInDataBlock();
            this.includeQualityFields  = query.isIncludeQualityFields();
            this.responseFormat        = query.getResponseFormat();
            this.decimationSize        = query.getDecimationSize();

        } else if (aquery instanceof HistoricalLocationQuery query) {
            this.decimationSize        = query.getDecimationSize();
        } else if (aquery instanceof ObservedPropertyQuery query) {
            this.noCompositePhenomenon = query.isNoCompositePhenomenon();
        }
        this.limit  = aquery.getLimit().isPresent() ? aquery.getLimit().getAsLong() : null;
        this.offset = aquery.getOffset();
    }

    @Override
    public FilterAppend setProcedure(String procedure) throws DataStoreException {
        if (procedure != null) this.procedures.add(procedure);
        return new FilterAppend(true);
    }

    @Override
    public FilterAppend setProcedureType(String type) throws DataStoreException {
        this.procedureType = type;
        return new FilterAppend(true);
    }

    @Override
    public FilterAppend setObservedProperty(String phenomenon) {
        if (phenomenon != null) this.observedPhenomenons.add(phenomenon);
        return new FilterAppend(true);
    }

    @Override
    public FilterAppend setFeatureOfInterest(String foi) {
        if (foi != null) this.featureOfInterests.add(foi);
        return new FilterAppend(true);
    }

    @Override
    public FilterAppend setObservationId(String oid) {
        if (oid != null) {
            this.observationIds.add(oid);
            /*
             *   2 possibility :
             *   1) observation template:
             *       -> <template base>  <proc id>
             *       -> <template base>  <proc id> - <phen id>
             *   2) look for observation by id:
             *       -> <observation base>  <proc id> - <measure id>
             *       -> <observation base>  <proc id> - <phen id> - <measure id>
             */
            if (oid.contains(observationTemplateIdBase)) {
                String[] component = oid.substring(observationTemplateIdBase.length()).split("-");

                if (component.length == 2) {
                    this.procedures.add(component[0]);
                    this.observedPhenomenons.add(component[1]);
                } else if (component.length == 1) {
                    this.procedures.add(component[0]);
                } else {
                    throw new IllegalArgumentException("Invalid observation id supplied");
                }

            } else if (oid.startsWith(observationIdBase)) {
                String[] component = oid.substring(observationIdBase.length()).split("-");
                if (component.length == 3) {
                    this.procedures.add(component[0]);
                    this.observedPhenomenons.add(component[1]);
                    this.measureIdFilter.add(component[2]);

                } else if (component.length == 2) {
                    this.procedures.add(component[0]);
                    this.measureIdFilter.add(component[1]);
                } else {
                    throw new IllegalArgumentException("Invalid observation id supplied");
                }

            } else {
                throw new IllegalArgumentException("Invalid observation id supplied");
            }
        }
        return new FilterAppend(true);
    }

    @Override
    public FilterAppend setTimeFilter(TemporalOperator tFilter) throws DataStoreException {
        if (tFilter == null) return new FilterAppend();

        Object time = tFilter.getExpressions().get(1);
        if (time instanceof Literal && !(time instanceof TemporalPrimitive)) {
            time = ((Literal)time).getValue();
        }
        TemporalOperatorName type = tFilter.getOperatorType();
        if (type == TemporalOperatorName.EQUALS || type == TemporalOperatorName.DURING) {
            Optional<Temporal> ti;
            if (time instanceof Period tp) {
                startTime = toDate(tp.getBeginning().getPosition());
                endTime   = toDate(tp.getEnding().getPosition());
            } else if ((ti = TemporalUtilities.toTemporal(time)).isPresent()) {
                startTime = toDate(ti.get());
                endTime   = startTime;
            } else {
                throw new ObservationStoreException("TM_Equals/TM_During operation require timeInstant or TimePeriod!", INVALID_PARAMETER_VALUE, EVENT_TIME);
            }
        } else if (type == TemporalOperatorName.BEFORE) {

            // for the operation before the temporal object must be an timeInstant
            Optional<Temporal> ti = TemporalUtilities.toTemporal(time);
            if (ti.isPresent()) {
                endTime = toDate(ti.get());
            } else {
                throw new ObservationStoreException("TM_Before operation require timeInstant!",  INVALID_PARAMETER_VALUE, EVENT_TIME);
            }
        } else if (type == TemporalOperatorName.AFTER) {

            // for the operation after the temporal object must be an timeInstant
            Optional<Temporal> ti = TemporalUtilities.toTemporal(time);
            if (ti.isPresent()) {
                startTime = toDate(ti.get());
            } else {
                throw new ObservationStoreException("TM_After operation require timeInstant!", INVALID_PARAMETER_VALUE, EVENT_TIME);
            }
        } else {
            throw new ObservationStoreException("This operation is not take in charge by the Web Service, supported one are: TM_Equals, TM_After, TM_Before, TM_During");
        }
        return new FilterAppend(true);
    }

    @Override
    public FilterAppend setBoundingBox(BinarySpatialOperator box) throws DataStoreException {
        this.boundingbox = OMUtils.getEnvelopeFromBBOXFilter(box);
        return new FilterAppend(true);
    }

    @Override
    public FilterAppend setOffering(String offering) throws DataStoreException {
        if (offering != null) this.offerings.add(offering);
        return new FilterAppend(true);
    }

    @Override
    public FilterAppend setResultFilter(ComparisonOperator filter) throws DataStoreException {
        this.resultFilter = filter;
        return new FilterAppend(true);
    }

    @Override
    public FilterAppend setPropertiesFilter(ComparisonOperator filter) throws DataStoreException {
        this.propertiesFilter = filter;
        return new FilterAppend(true);
    }

    @Override
    public List<ObservationResult> filterResult() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported with entity type " + entityType.getName());
    }

    @Override
    public Set<String> getIdentifiers() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported with entity type " + entityType.getName());
    }

    @Override
    public long getCount() throws DataStoreException {
        return getIdentifiers().size();
    }

    @Override
    public List<Observation> getObservations() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported with entity type " + entityType.getName());
    }

    @Override
    public List<SamplingFeature> getFeatureOfInterests() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported with entity type " + entityType.getName());
    }

    @Override
    public List<Phenomenon> getPhenomenons() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported with entity type " + entityType.getName());
    }

    @Override
    public List<Procedure> getProcesses() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported with entity type " + entityType.getName());
    }

    @Override
    public Map<String, Geometry> getSensorLocations() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported with entity type " + entityType.getName());
    }

    @Override
    public Map<String, Map<Date, Geometry>> getSensorHistoricalLocations() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported with entity type " + entityType.getName());
    }

    @Override
    public Map<String, Set<Date>> getSensorHistoricalTimes() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported with entity type " + entityType.getName());
    }

    @Override
    public Object getResults() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported with entity type " + entityType.getName());
    }

    @Override
    public Envelope getCollectionBoundingShape() {
        throw new UnsupportedOperationException("Not supported with entity type " + entityType.getName());
    }

    @Override
    public List<Offering> getOfferings() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported with entity type " + entityType.getName());
    }

    @Override
    public void refresh() throws DataStoreException {
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
