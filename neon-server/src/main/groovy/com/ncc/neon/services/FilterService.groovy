/*
 * Copyright 2013 Next Century Corporation
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ncc.neon.services

import com.ncc.neon.query.filter.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Service for working with a user's current filters
 */

@Component
@Path("/filterservice")
class FilterService {

    /** empty dataset returned when invalid filter is removed or when the filters are cleared */
    private static final DataSet EMPTY_DATASET = new DataSet(databaseName: "", tableName: "")

    @Autowired
    FilterState filterState

    /**
     * Get information about the filters
     * @param dataSet The DataSet to get the filters of
     * @return information about the current filters
     */
    @POST
    @Path("getfilters")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    HashMap getFilters(DataSet dataSet) {
        List<Filter> filters = filterState.getFiltersForDataset(dataSet)
        return [count: filters.size()]

    }

    /**
     * Add a filter
     * @param filterKey The filter to add
     * @return an ADD filter event
     */
    @POST
    @Path("addfilter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    FilterEvent addFilter(FilterKey filterKey) {
        filterState.addFilter(filterKey)
        return new FilterEvent(type: "ADD", dataSet: filterKey.dataSet)

    }

    /**
     * Removes the filters associated with the specified id
     * @param filterId The id of the filter to remove
     * @return a REMOVE filter event
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("removefilter")
    FilterEvent removeFilter(String id) {
        FilterKey removed = filterState.removeFilter(id)
        DataSet dataset = removed ? removed.dataSet : EMPTY_DATASET
        return new FilterEvent(type: "REMOVE", dataSet: dataset)
    }

    /**
     * Replace the filters for the given filter key. If none exists, this works the same as addFilter(filterKey)
     * @param filterKey The filter to replace
     * @return a REPLACE filter event
     */
    @POST
    @Path("replacefilter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    FilterEvent replaceFilter(FilterKey filterKey) {
        removeFilter(filterKey.id)
        addFilter(filterKey)
        return new FilterEvent(type: "REPLACE", dataSet: filterKey.dataSet)
    }

    /**
     * Clears all filters.
     * @return a CLEAR filter event
     */
    @POST
    @Path("clearfilters")
    @Produces(MediaType.APPLICATION_JSON)
    FilterEvent clearFilters() {
        filterState.clearAllFilters()
        // use an empty dataset since the clear can span multiple datasets
        return new FilterEvent(type: "CLEAR", dataSet: EMPTY_DATASET)
    }
}
