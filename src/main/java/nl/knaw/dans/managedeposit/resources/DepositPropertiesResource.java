/*
 * Copyright (C) 2023 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.managedeposit.resources;

import io.dropwizard.hibernate.UnitOfWork;
import nl.knaw.dans.managedeposit.core.DepositProperties;
import nl.knaw.dans.managedeposit.db.DepositPropertiesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;

@Path("/deposits")
public class DepositPropertiesResource {
    private static final Logger log = LoggerFactory.getLogger(DepositPropertiesResource.class);
    //log.info("Returning service document for user {} and collections {}", depositor, collectionIds);
    private final DepositPropertiesDAO depositPropertiesDAO;

    public DepositPropertiesResource(DepositPropertiesDAO depositPropertiesDAO) {
        this.depositPropertiesDAO = depositPropertiesDAO;
    }

    @POST
    @UnitOfWork
    @Consumes("application/json")
    @Produces("application/json")
    public DepositProperties createDepositPropertiesRecord(@Valid DepositProperties depositProperties, @Context UriInfo uriInfo) {
        return depositPropertiesDAO.create(depositProperties);
    }

    @GET
    @UnitOfWork
    @Produces({ "application/json", "text/csv" })
    @Path("/report")
    public List<DepositProperties> listDepositProperties(@Context UriInfo uriInfo) {
        for (String key: uriInfo.getQueryParameters().keySet() ){
            List<String> value = uriInfo.getQueryParameters().get(key);
            System.out.println(value);

        }
        return depositPropertiesDAO.findAll();
    }

    @GET
    @UnitOfWork
    @Produces("application/json")
    @Path("/{depositId}")
    public List<DepositProperties> getDepositId(@PathParam("depositId") Optional<String> depositId) {
        DepositProperties depositProperties = depositPropertiesDAO.findById(depositId.get()).orElseThrow(() -> new NotFoundException(String.format("No such deposit: %s", depositId.get())));
        return List.of(depositProperties);
    }

    @DELETE
    @UnitOfWork
    @Produces("application/json")
    public void DeleteDepositPropertiesRecord(@Context UriInfo uriInfo) {
        for (String key: uriInfo.getQueryParameters().keySet() ){
            List<String> value = uriInfo.getQueryParameters().get(key);
            System.out.println(value);

        }

    }

}