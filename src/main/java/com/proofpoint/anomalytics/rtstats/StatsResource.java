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

import com.google.common.base.Preconditions;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("/v1/stats")
public class StatsResource
{
    private Map<String, Object> store = new ConcurrentHashMap<String, Object>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll()
    {
        return Response.ok(store).build();
    }

    @GET
    @Path("{key: \\w+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("key") String key)
    {
        Preconditions.checkNotNull(key, "key must not be null");

        Object data = store.get(key);

        if (data == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("[" + key + "]").build();
        }

        return Response.ok(data).build();
    }

    @PUT
    @Path("{key: \\w+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(@PathParam("key") String key, Object data)
    {
        Preconditions.checkNotNull(key, "key must not be null");
        Preconditions.checkNotNull(data, "person must not be null");

        boolean added = store.put(key, data) == null;
        if (added) {
            UriBuilder uri = UriBuilder.fromResource(StatsResource.class);
            return Response.created(uri.build(key)).build();
        }

        return Response.noContent().build();
    }

    @DELETE
    @Path("{key: \\w+}")
    public Response delete(@PathParam("key") String key)
    {
        Preconditions.checkNotNull(key, "key must not be null");

        if (store.remove(key) != null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.noContent().build();
    }
}
