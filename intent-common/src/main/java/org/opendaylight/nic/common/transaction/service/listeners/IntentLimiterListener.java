/*
 * Copyright (c) 2017 Serro LLC. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.nic.common.transaction.service.listeners;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.nic.common.transaction.api.IntentCommonService;
import org.opendaylight.nic.common.transaction.utils.InstanceIdentifierUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.intent.limiter.rev170310.IntentsLimiter;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Created by yrineu on 28/06/17.
 */
public class IntentLimiterListener extends AbstractListener<IntentsLimiter>
        implements IntentTreeChangesListener<IntentsLimiter> {
    private static final Logger LOG = LoggerFactory.getLogger(IntentLimiterListener.class);
    private final DataBroker dataBroker;
    private final IntentCommonService intentCommonService;
    private ListenerRegistration<IntentLimiterListener> registration;

    public IntentLimiterListener(final DataBroker dataBroker,
                                 final IntentCommonService intentCommonService) {
        super();
        this.dataBroker = dataBroker;
        this.intentCommonService = intentCommonService;
    }

    @Override
    public void start() {
        LOG.info("\nIntentLimiterListener started with success");
        final DataTreeIdentifier<IntentsLimiter> dataTreeIdentifier = new DataTreeIdentifier<>(
                LogicalDatastoreType.CONFIGURATION, InstanceIdentifierUtils.INTENTS_LIMITER_IDENTIFIER);
        registration = dataBroker.registerDataTreeChangeListener(dataTreeIdentifier, this);
    }

    @Override
    public void handleIntentCreated(IntentsLimiter intents) {
        intents.getIntentLimiter().forEach(intent -> intentCommonService.resolveAndApply(intent));
    }

    @Override
    public void handleIntentUpdated(IntentsLimiter intents) {
        //TODO: Implement an update for IntentLimiters
    }

    @Override
    public void handleIntentRemoved(IntentsLimiter intents) {
        intents.getIntentLimiter().forEach(intent -> intentCommonService.resolveAndRemove(intent));
    }

    @Override
    public void onDataTreeChanged(@Nonnull Collection<DataTreeModification<IntentsLimiter>> collection) {
        super.handleIntentTreeEvent(collection);
    }

    @Override
    public void stop() {
        registration.close();
    }
}
