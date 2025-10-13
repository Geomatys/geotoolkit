/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometries.math.DataType;
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.geometries.math.TupleArray;
import org.apache.sis.geometries.math.TupleArrays;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.client.service.ServiceResponse;
import org.geotoolkit.ogcapi.client.dggs.DggsApi;
import org.geotoolkit.ogcapi.client.feature.FeatureApi;
import org.geotoolkit.ogcapi.model.common.CollectionDescription;
import org.geotoolkit.ogcapi.model.dggs.DggrsData;
import org.geotoolkit.ogcapi.model.dggs.DggrsDataValue;
import org.geotoolkit.ogcapi.model.dggs.DggrsItem;
import org.geotoolkit.ogcapi.model.dggs.DggrsListResponse;
import org.geotoolkit.ogcapi.model.jsonschema.JSONSchema;
import org.geotoolkit.ogcapi.model.jsonschema.JSONSchemaProperty;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystems;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.storage.dggs.internal.shared.ArrayDiscreteGlobalGridCoverage;
import org.geotoolkit.storage.rs.CodedCoverage;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.geotoolkit.storage.rs.CodedResource;
import org.opengis.feature.FeatureType;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DggrsResource extends CollectionItemResource implements CodedResource {

    private final FeatureApi featureApi;
    private final DggsApi dggrsApi;

    //cache
    private List<SampleDimension> sampleDimensions;
    private FeatureType type;
    private List<CodedGeometry> geometries;

    public DggrsResource(CollectionResource parent, CollectionDescription description) {
        super(parent, description);
        final OpenApiConfiguration config = parent.api.getConfiguration();
        featureApi = new FeatureApi(config);
        dggrsApi = new DggsApi(config);
    }

    @Override
    public synchronized FeatureType getSampleType() throws DataStoreException {
        if (type != null) return type;

        try {
            ServiceResponse<JSONSchema> jsonSchema = featureApi.collectinGetJsonSchema(description.getId(), null);
            final JSONSchema schema = jsonSchema.getData();

            sampleDimensions = new ArrayList<>();

            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(description.getId());
            for (Entry<String,JSONSchemaProperty> entry : schema.getProperties().entrySet()) {
                ftb.addAttribute(String.class).setName(entry.getKey());
                sampleDimensions.add(new SampleDimension.Builder().setName(entry.getKey()).build());
            }
            type = ftb.build();

        } catch (ServiceException ex) {
            throw new DataStoreException(ex);
        }

        return type;
    }

    @Override
    public CodedGeometry getGridGeometry() throws DataStoreException {
        return getAlternateGridGeometry().get(0);
    }

    @Override
    public synchronized List<CodedGeometry> getAlternateGridGeometry() throws DataStoreException {
        if (geometries != null) return geometries;

        geometries = new ArrayList<>();
        try {
            final Set<String> allKnownDggrs = DiscreteGlobalGridReferenceSystems.listDggrs();

            final ServiceResponse<DggrsListResponse> response = dggrsApi.collectionGetDGGRSList(description.getId(), "json");
            final DggrsListResponse list = response.getData();
            for (DggrsItem item : list.getDggrs()) {
                if (allKnownDggrs.contains(item.getId())) {
                    //todo we should check the definition
                    final DiscreteGlobalGridReferenceSystem dggrs = DiscreteGlobalGridReferenceSystems.forCode(item.getId());
                    CodedGeometry gg = new DiscreteGlobalGridGeometry(dggrs, null, null, null);
                    geometries.add(gg);
                }
            }
        } catch (ServiceException | FactoryException ex) {
            throw new DataStoreException(ex);
        }

        return geometries;
    }

    @Override
    public CodedCoverage read(CodedGeometry geometry, int... range) throws DataStoreException {
        getSampleType();

        final DiscreteGlobalGridGeometry g = (DiscreteGlobalGridGeometry) geometry;
        final DiscreteGlobalGridReferenceSystem dggrs = g.getReferenceSystem();
        final String dggrsName = dggrs.getName().getCode();
        Object baseZoneId = g.getBaseZoneId();
        Integer relativeDepth = g.getRelativeDepth();

        if (baseZoneId == null || relativeDepth == null) {
            throw new DataStoreException("base zone and/or relative depth are null");
        }

        baseZoneId = dggrs.getGridSystem().getHierarchy().toTextIdentifier(baseZoneId);
        try {
            final ServiceResponse<DggrsData> response = dggrsApi.collectionGetDGGRSZoneData(dggrsName, baseZoneId.toString(), description.getId(), "json", null, null, null, null, null, null, null, null, relativeDepth, null, null);
            final DggrsData data = response.getData();
            final List<DggrsDataValue> values = data.getValues().values().iterator().next();

            final List<TupleArray> samples = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                final List<Object> datas = values.get(0).getData();
                final double[] arr = new double[datas.size()];
                for (int k = 0; k <datas.size(); k++) {
                    Object v = datas.get(k);
                    if (v instanceof String s) {
                        try {
                            v = Double.valueOf(s);
                        } catch (NumberFormatException e) {
                            v = Double.NaN;
                        }
                    }
                    if (v instanceof Number n) {
                        arr[k] = n.doubleValue();
                    } else {
                        arr[k] = Double.NaN;
                    }
                }

                final SampleSystem ss = new SampleSystem(DataType.DOUBLE, sampleDimensions.get(i));
                final TupleArray array = TupleArrays.of(ss, arr);
                samples.add(array);
            }

            return new ArrayDiscreteGlobalGridCoverage(getIdentifier().get(), g, samples);

        } catch (ServiceException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        getSampleType();
        return sampleDimensions;
    }

}
