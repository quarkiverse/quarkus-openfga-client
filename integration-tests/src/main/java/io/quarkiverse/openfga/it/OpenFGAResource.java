/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quarkiverse.openfga.it;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Collection;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import io.quarkiverse.openfga.client.*;
import io.quarkiverse.openfga.client.model.*;
import io.quarkiverse.openfga.client.utils.PaginatedList;
import io.smallrye.mutiny.Uni;

@Path("/openfga")
@ApplicationScoped
public class OpenFGAResource {

    @Inject
    OpenFGAClient client;

    @Inject
    StoreClient storeClient;

    @Inject
    AuthorizationModelsClient authorizationModelsClient;

    @Inject
    AuthorizationModelClient authorizationModelClient;

    @Inject
    AssertionsClient assertionsClient;

    @GET
    @Path("stores")
    @Produces(APPLICATION_JSON)
    public Uni<List<Store>> listStores() {
        return client.listAllStores();
    }

    @GET
    @Path("changes")
    @Produces(APPLICATION_JSON)
    public Uni<Collection<RelTupleChange>> readChanges() {
        return storeClient.readChanges().map(PaginatedList::getItems);
    }

    @GET
    @Path("authorization-models")
    @Produces(APPLICATION_JSON)
    public Uni<List<AuthorizationModel>> listModels() {
        return authorizationModelsClient.listAll();
    }

    @GET
    @Path("authorization-tuples")
    @Produces(APPLICATION_JSON)
    public Uni<List<RelTuple>> listTuples() {
        return authorizationModelClient.readAll();
    }

    @GET
    @Path("objects")
    @Produces(APPLICATION_JSON)
    public Uni<List<RelTuple>> listObjects() {
        return authorizationModelClient.readAll();
    }

    @GET
    @Path("assertions")
    @Produces(APPLICATION_JSON)
    public Uni<List<Assertion>> listAssertions() {
        return assertionsClient.list();
    }
}
