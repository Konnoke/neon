package com.ncc.neon.query.convert

import com.ncc.neon.query.Query
import com.ncc.neon.query.clauses.SingularWhereClause
import com.ncc.neon.query.filter.Filter
import com.ncc.neon.query.filter.FilterState
import org.junit.After
import org.junit.Before
import org.junit.Test

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

abstract class AbstractConversionTest {

    protected static final String DATABASE_NAME = "database"
    protected static final String TABLE_NAME = "table"
    protected static final String COLUMN_NAME = "column"
    protected static final String COLUMN_VALUE = "value"

    protected FilterState filterState
    private Filter simpleFilter
    protected Query simpleQuery

    @Before
    void setup() {
        simpleFilter = new Filter(databaseName: DATABASE_NAME, tableName: TABLE_NAME)
        simpleQuery = new Query(filter: simpleFilter)
        filterState = new FilterState()
    }

    @After
    void teardown(){
        simpleFilter = new Filter(databaseName: DATABASE_NAME, tableName: TABLE_NAME)
        simpleQuery = new Query(filter: simpleFilter)
        filterState = new FilterState()
    }

    @Test(expected = NullPointerException)
    void "a query to be converted must have a filter"() {
        Query query = new Query()
        whenExecutingConvertQuery(query)
    }

    @Test
    void testSimplestConvertQuery() {
        def query = whenExecutingConvertQuery(simpleQuery)
        assertSimplestConvertQuery(query)
    }

    @Test
    void "test convertQuery does not care about the FilterState"() {
        givenFilterStateHasOneFilter()
        def query = whenExecutingConvertQuery(simpleQuery)
        assertQueryWithOneFilterInFilterState(query)
    }

    private void givenFilterStateHasOneFilter() {
        SingularWhereClause whereClause = new SingularWhereClause(lhs: COLUMN_NAME, operator: "=", rhs: COLUMN_VALUE)
        Filter filterWithWhere = new Filter(databaseName: simpleFilter.databaseName, tableName: simpleFilter.tableName, whereClause: whereClause)
        filterState.addFilter(filterWithWhere)
    }

    protected abstract def whenExecutingConvertQuery(query)

    protected abstract void assertSimplestConvertQuery(query)

    protected abstract void assertQueryWithOneFilterInFilterState(query)
}
