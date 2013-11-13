package com.ncc.neon.services

import com.ncc.neon.query.QueryExecutor
import com.ncc.neon.query.QueryResult
import com.ncc.neon.query.filter.Filter
import com.ncc.neon.query.transform.ValueStringReplaceTransform
import groovy.mock.interceptor.MockFor
import org.json.JSONObject
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

class SelectionServiceTest {

    private SelectionService selectionService

    @Before
    void before() {
        selectionService = new SelectionService()
    }

    @Test
    void "get selection WHERE"() {
        def inputJson = /[ { "key1" : "val1" }, { "key2": "val2" }]/

        def filter = [] as Filter
        def queryExecutorMock = createQueryExecutorMockForGetSelection(filter, inputJson)
        def queryExecutor = queryExecutorMock.proxyInstance()
        setQueryServiceConnection(queryExecutor)

        def result = selectionService.getSelectionWhere(filter, null, null)
        queryExecutorMock.verify(queryExecutor)

        // in this case the input and output is the same because there is no transform
        def array = new JSONObject(result).getJSONArray("data")
        assert array.length() == 2
        assertKeyValue(array, 0, "key1", "val1")
        assertKeyValue(array, 1, "key2", "val2")

    }

    @Test
    void "get selection WHERE with no-arg transform"() {
        def inputJson = /[{"key1":"val1"},{"replaceMyValue":"abc"}]/
        def filter = [] as Filter
        def transform = ValueStringReplaceTransform.name
        def queryExecutorMock = createQueryExecutorMockForGetSelection(filter, inputJson)
        def queryExecutor = queryExecutorMock.proxyInstance()
        setQueryServiceConnection(queryExecutor)

        def result = selectionService.getSelectionWhere(filter, transform, null)
        queryExecutorMock.verify(queryExecutor)

        def array = new JSONObject(result).getJSONArray("data")
        assert array.length() == 2
        assertKeyValue(array, 0, "key1", "val1")
        // 10 is the default value
        assertKeyValue(array, 1, "replaceMyValue", 10)
    }


    @Test
    void "get selection WHERE with transform with args"() {
        def inputJson = /[{"key1":"val1"},{"notReplaced":"abc"}]/
        def filter = [] as Filter
        def transform = ValueStringReplaceTransform.name
        def queryExecutorMock = createQueryExecutorMockForGetSelection(filter, inputJson)
        def queryExecutor = queryExecutorMock.proxyInstance()
        setQueryServiceConnection(queryExecutor)

        def result = selectionService.getSelectionWhere(filter, transform, ["val1", "25"])
        queryExecutorMock.verify(queryExecutor)

        def array = new JSONObject(result).getJSONArray("data")
        assert array.length() == 2
        assertKeyValue(array, 0, "key1", 25)
        assertKeyValue(array, 1, "notReplaced", "abc")
    }

    /**
     * Creates a mock query executor that simulates a getSelectionWhere and returns this json
     * @param filter
     * @param inputJson
     * @return
     */
    private def createQueryExecutorMockForGetSelection(filter, inputJson) {
        def queryExecutorMock = new MockFor(QueryExecutor)
        def result = [toJson: { inputJson }] as QueryResult
        queryExecutorMock.demand.getSelectionWhere { f -> assert f.is(filter); result }
        return queryExecutorMock
    }

    private void setQueryServiceConnection(QueryExecutor executor) {
        selectionService.queryExecutorFactory = [create: {executor} ] as QueryExecutorFactory
    }

    private static assertKeyValue(array, index, key, value) {
        def jsonObject = array.getJSONObject(index)
        assert jsonObject.get(key) == value
    }
}
