package com.ncc.neon.query.convert

import com.mongodb.BasicDBObject
import com.ncc.neon.query.mongo.MongoConversionStrategy

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

class MongoConvertQueryTest extends AbstractConversionTest {

    @Override
    def whenExecutingConvertQuery(query) {
        MongoConversionStrategy conversionStrategy = new MongoConversionStrategy(filterState)
        conversionStrategy.convertQuery(query)
    }

    @Override
    void assertSimplestConvertQuery(query) {
        assert query.query == simpleQuery
        assert query.whereClauseParams == new BasicDBObject()
        assert query.selectParams == null
    }

    /**
     * convertQuery does not care about the filter state, so this is the same as the SimplestConvertQuery
     */
    @Override
    void assertQueryWithOneFilterInFilterState(query) {
        assert query.query == simpleQuery
        assert query.whereClauseParams == new BasicDBObject()
        assert query.selectParams == null
    }

}

