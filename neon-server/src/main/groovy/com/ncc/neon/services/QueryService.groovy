package com.ncc.neon.services

import com.ncc.neon.metadata.model.column.ColumnMetadataList
import com.ncc.neon.metadata.model.dataset.WidgetAndDatasetMetadataList
import com.ncc.neon.query.Query
import com.ncc.neon.query.QueryExecutor
import com.ncc.neon.query.QueryGroup
import com.ncc.neon.query.QueryOptions
import com.ncc.neon.query.QueryResult
import com.ncc.neon.result.AssembleClientData
import com.ncc.neon.result.ClientData
import com.ncc.neon.result.InitializingClientData
import com.ncc.neon.result.MetadataResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/*
 * ************************************************************************
 * Copyright (c), 2013 Next Century Corporation. All Rights Reserved.
 *
 * This software code is the exclusive property of Next Century Corporation and is
 * protected by United States and International laws relating to the protection
 * of intellectual property.  Distribution of this software code by or to an
 * unauthorized party, or removal of any of these notices, is strictly
 * prohibited and punishable by law.
 *
 * UNLESS PROVIDED OTHERWISE IN A LICENSE AGREEMENT GOVERNING THE USE OF THIS
 * SOFTWARE, TO WHICH YOU ARE AN AUTHORIZED PARTY, THIS SOFTWARE CODE HAS BEEN
 * ACQUIRED BY YOU "AS IS" AND WITHOUT WARRANTY OF ANY KIND.  ANY USE BY YOU OF
 * THIS SOFTWARE CODE IS AT YOUR OWN RISK.  ALL WARRANTIES OF ANY KIND, EITHER
 * EXPRESSED OR IMPLIED, INCLUDING, WITHOUT LIMITATION, IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, ARE HEREBY EXPRESSLY
 * DISCLAIMED.
 *
 * PROPRIETARY AND CONFIDENTIAL TRADE SECRET MATERIAL NOT FOR DISCLOSURE OUTSIDE
 * OF NEXT CENTURY CORPORATION EXCEPT BY PRIOR WRITTEN PERMISSION AND WHEN
 * RECIPIENT IS UNDER OBLIGATION TO MAINTAIN SECRECY.
 */

/**
 * Service for executing queries against an arbitrary data store.
 */

@Component
@Path("/queryservice")
class QueryService {

    @Autowired
    QueryExecutorFactory queryExecutorFactory

    @Autowired
    MetadataResolver metadataResolver

    /**
     * Executes a query against the data source the user is currently connected to.
     * This takes into account the user's current filters so the results will be limited by these if they exist.
     * @param query The neon representation of a query. This query is converted to a database specific query language.
     * @return An object that contains the query result data and optional metadata about the query.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{connectionId}/query")
    ClientData executeQuery(@PathParam("connectionId") String connectionId, Query query) {
        return execute(connectionId, query, QueryOptions.FILTERED_DATA)
    }

    /**
     * Executes a group of queries against the data source the user is currently connected to.
     * This takes into account the user's current filters so the results will be limited by these if they exist.
     * @param query A collection of queries. The results of the queries will be appended together.
     * @return An object that contains the query result data and optional metadata about the query.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{connectionId}/querygroup")
    ClientData executeQueryGroup(@PathParam("connectionId") String connectionId, QueryGroup query) {
        return execute(connectionId, query, QueryOptions.FILTERED_DATA)
    }

    /**
     * Executes a query for selected items against the data source the user is currently connected to.
     * This takes into account the user's current filters so the results will be limited by these if they exist.
     * @param query The neon representation of a query. This query is converted to a database specific query language.
     * @return An object that contains the query result data and optional metadata about the query.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{connectionId}/querywithselectiononly")
    ClientData executeQueryWithSelectionOnly(@PathParam("connectionId") String connectionId, Query query) {
        return execute(connectionId, query, QueryOptions.FILTERED_AND_SELECTED_DATA)
    }

    /**
     * Executes a group of queries for selected items against the data source the user is currently connected to.
     * This takes into account the user's current filters so the results will be limited by these if they exist.
     * @param query A collection of queries. The results of the queries will be appended together.
     * @return An object that contains the query result data and optional metadata about the query.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{connectionId}/querygroupwithselectiononly")
    ClientData executeQueryGroupWithSelectionOnly(@PathParam("connectionId") String connectionId, QueryGroup query) {
        return execute(connectionId, query, QueryOptions.FILTERED_AND_SELECTED_DATA)
    }


    /**
     * Executes a query against the data source the user is currently connected to ignoring the current filters and selection.
     * @param query The neon representation of a query
     * @return An object that contains the query result data and optional metadata about the query.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{connectionId}/querydisregardfilters")
    ClientData executeQueryDisregardFilters(@PathParam("connectionId") String connectionId, Query query) {
        return execute(connectionId, query, QueryOptions.ALL_DATA)
    }

    /**
     * Executes a group of queries against the data source the user is currently connected to ignoring the current filters and selection.
     * @param query The neon representation of a query
     * @return An object that contains the query result data and optional metadata about the query.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{connectionId}/querygroupdisregardfilters")
    ClientData executeQueryGroupDisregardFilters(@PathParam("connectionId") String connectionId, QueryGroup query) {
        return execute(connectionId, query, QueryOptions.ALL_DATA)
    }

    /**
     * Get all the columns for tabular datasets.
     * @param databaseName The database containing the data
     * @param tableName The table containing the data
     * @param widgetName The current widget name. If used, additional metadata may be passed down.
     * @return An object that contains all the column names and metadata about the columns.
     * Metadata includes element id -> field name mappings for automatic widget configuration and also type information
     * about the columns which is useful for generic widgets to configure against.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{connectionId}/fields")
    InitializingClientData getFields(@PathParam("connectionId") String connectionId,
                                     @QueryParam("databaseName") String databaseName,
                                     @QueryParam("tableName") String tableName,
                                     @QueryParam("widgetName") String widgetName) {

        QueryExecutor queryExecutor = queryExecutorFactory.getExecutor(connectionId)
        QueryResult queryResult = queryExecutor.getFieldNames(databaseName, tableName)
        WidgetAndDatasetMetadataList metadata = metadataResolver.getInitializationData(databaseName, tableName, widgetName)
        ColumnMetadataList columnMetadataList = metadataResolver.resolveQuery(databaseName, tableName)

        AssembleClientData assembler = new AssembleClientData(queryResult: queryResult, columnMetadataList: columnMetadataList, initDataList: metadata)
        return assembler.createClientData()
    }

    /**
     * Gets a list of all the databases for the current datastore
     * @return The list of database names
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{connectionId}/databasenames")
    List<String> getDatabaseNames(@PathParam("connectionId") String connectionId) {
        QueryExecutor queryExecutor = queryExecutorFactory.getExecutor(connectionId)
        return queryExecutor.showDatabases()
    }

    /**
     * Gets a list of all the tables
     * @param database The database that contains the tables
     * @return The list of table names
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{connectionId}/tablenames")
    List<String> getTableNames(@PathParam("connectionId") String connectionId, @FormParam("database") String database) {
        QueryExecutor queryExecutor = queryExecutorFactory.getExecutor(connectionId)
        return queryExecutor.showTables(database)
    }

    private final def execute = { String connectionId, def query, QueryOptions options ->
        QueryExecutor queryExecutor = queryExecutorFactory.getExecutor(connectionId)
        QueryResult queryResult = queryExecutor.execute(query, options)
        ColumnMetadataList columnMetadataList = metadataResolver.resolveQuery(query)

        AssembleClientData assembler = new AssembleClientData(queryResult: queryResult, columnMetadataList: columnMetadataList)
        return assembler.createClientData()
    }
}
