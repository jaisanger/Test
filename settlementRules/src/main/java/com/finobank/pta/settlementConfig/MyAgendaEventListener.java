/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.finobank.pta.settlementConfig;

//import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

import org.drools.core.event.DefaultAgendaEventListener;
//import org.jboss.logging.Logger;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;

@Singleton
public class MyAgendaEventListener extends DefaultAgendaEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(MyAgendaEventListener.class);
    
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        Rule rule = event.getMatch().getRule();
        String ruleName = rule.getName();
        LOG.info("Rule Name firing is '{}'",ruleName);
    }


    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        Rule rule = event.getMatch().getRule();
        String ruleName = rule.getName();
        LOG.info("Rule Name fired is '{}'",ruleName);
    }
}
