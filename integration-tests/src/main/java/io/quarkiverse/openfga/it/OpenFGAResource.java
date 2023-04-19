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

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import io.quarkiverse.openfga.client.AuthorizationModelClient;
import io.quarkiverse.openfga.client.StoreClient;
import io.quarkiverse.openfga.client.model.AuthorizationModel;
import io.quarkiverse.openfga.client.model.Tuple;
import io.smallrye.mutiny.Uni;

@Path("/openfga")
@ApplicationScoped
public class OpenFGAResource {

    @Inject
    StoreClient storeClient;

    @Inject
    AuthorizationModelClient authorizationModelClient;

    @GET
    @Path("authorization-models")
    @Produces(APPLICATION_JSON)
    public Uni<List<AuthorizationModel>> listModels() {
        return storeClient.authorizationModels().listAll();
    }

    @GET
    @Path("authorization-tuples")
    @Produces(APPLICATION_JSON)
    public Uni<List<Tuple>> listTuples() {
        return authorizationModelClient.readAllTuples();
    }
}
