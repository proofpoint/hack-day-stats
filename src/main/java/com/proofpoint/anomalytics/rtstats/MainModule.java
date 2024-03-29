/*
 * Copyright 2010 Proofpoint, Inc.
 *
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
 */
package com.proofpoint.anomalytics.rtstats;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

import static com.proofpoint.discovery.client.DiscoveryBinder.discoveryBinder;

public class MainModule
        implements Module
{
    public void configure(Binder binder)
    {
        binder.requireExplicitBindings();
        binder.disableCircularProxies();

        binder.bind(StatsResource.class).in(Scopes.SINGLETON);
        binder.bind(UIResource.class).in(Scopes.SINGLETON);
        binder.bind(IndexResource.class).in(Scopes.SINGLETON);
        binder.bind(Store.class).in(Scopes.SINGLETON);

        discoveryBinder(binder).bindHttpAnnouncement("realtime-stats");
    }
}
