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
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/")
public class DepositPropertiesResource {
    private static final Logger log = LoggerFactory.getLogger(DepositPropertiesResource.class);
    private final DepositPropertiesDAO depositPropertiesDAO;
    public DepositPropertiesResource(DepositPropertiesDAO depositPropertiesDAO) {
        this.depositPropertiesDAO = depositPropertiesDAO;
    }

    @GET
    @UnitOfWork
    @Produces("text/plain" )
    public Response getApiInformation() {
            String response =  "DD Manage Deposit is running. Usage: \n" +
                "- POST path: basePath/" +
                "- GET path: basePath/report \n" +
                "- DELETE path: basePath/-delete-deposit \n" +
                "Parameters: user, state, startdate, enddate";

        return Response
            .status(Response.Status.OK)
            .entity(response)
            .type(MediaType.TEXT_PLAIN)
            .build();
    }

    @POST
    @UnitOfWork
    @Consumes("application/json")
    @Produces("application/json")
    public DepositProperties createDepositPropertiesRecord(@Valid DepositProperties depositProperties) {
        return depositPropertiesDAO.create(depositProperties);
    }

    @PUT
    @UnitOfWork
    @Produces({ "application/json", "text/csv" })
    public String updateDeposit(@Context UriInfo uriInfo) {
        int updatedNumber = depositPropertiesDAO.UpdateSelection(uriInfo.getQueryParameters()).orElseThrow(() -> new NotFoundException(String.format("Not such deposit with given criteria")));
        return String.format("Updated record(s): %d.", updatedNumber);
    }

}
