package com.ncc.neon.services
import com.ncc.neon.connect.ConnectionState
import com.ncc.neon.query.filter.Filter
import com.ncc.neon.query.filter.FilterEvent
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
 *
 * 
 * @author tbrooks
 */

@Component
@Path("/filterservice")
class FilterService{

    @Autowired
    ConnectionState connectionState

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("hostnames")
    List<String> getHostnames() {
        return ["localhost"]
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("connect")
    void connect(@FormParam("datastore") String datastore, @FormParam("hostname") String hostname){
        connectionState.createConnection(datastore,hostname)
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("databasenames")
    List<String> getDatabaseNames() {
        connectionState.queryExecutor.showDatabases()
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("tablenames")
    List<String> getTableNames(@FormParam("database") String database) {
        connectionState.queryExecutor.showTables(database)
    }


    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("columnnames")
    List<String> getColumnNames(@FormParam("database") String database, @FormParam("table") String table) {
        connectionState.queryExecutor.getFieldNames(database, table).collect{ it }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("updatefilter")
    FilterEvent updateFilter(Filter filter) {
        String uuid = connectionState.queryExecutor.addFilter(filter).toString()
        return new FilterEvent(addedId: uuid)
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("updatefilter/{filterId}")
    FilterEvent updateFilter(Filter filter, @PathParam("filterId") String replaceId) {
        connectionState.queryExecutor.removeFilter(UUID.fromString(replaceId))
        if(filter.whereClause){
            String uuid = connectionState.queryExecutor.addFilter(filter).toString()
            return new FilterEvent(addedId: uuid)
        }

        return new FilterEvent(addedIds: [])

    }
}
