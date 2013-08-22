package com.ncc.neon.mongo
import com.mongodb.MongoClient
import com.ncc.neon.config.MongoConfigParser
import com.ncc.neon.connect.ConnectionInfo
import com.ncc.neon.connect.ConnectionState
import com.ncc.neon.connect.DataSources
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
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
 * Spring bean configuration to use for the mongo configuration test
 */
@Configuration
@ComponentScan(basePackages = ['com.ncc.neon'])
@Profile('mongo-integrationtest')
class MongoIntegrationTestContext {

    static final MongoClient MONGO

    static {
        def hostsString = System.getProperty("mongo.hosts", "localhost")
        def serverAddresses = MongoConfigParser.createServerAddresses(hostsString)
        MONGO = new MongoClient(serverAddresses)
    }

    @Bean
    ConnectionState connectionState() {
        def hostsString = System.getProperty("mongo.hosts", "localhost")
        ConnectionState connectionState = new ConnectionState()
        ConnectionInfo info = new ConnectionInfo(dataStoreName: DataSources.mongo.name(), connectionUrl: hostsString)
        connectionState.createConnection(info)
        return connectionState
    }

}