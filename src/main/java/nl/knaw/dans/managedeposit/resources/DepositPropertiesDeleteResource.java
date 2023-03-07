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
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/delete-deposit")
public class DepositPropertiesDeleteResource {
    private static final Logger log = LoggerFactory.getLogger(DepositPropertiesDeleteResource.class);
    private final DepositPropertiesDAO depositPropertiesDAO;

    public DepositPropertiesDeleteResource(DepositPropertiesDAO depositPropertiesDAO) {
        this.depositPropertiesDAO = depositPropertiesDAO;
    }

    @DELETE
    @UnitOfWork
    @Produces("text/plain")
    public String DeleteDepositPropertiesSelection(@Context UriInfo uriInfo) {
        int deletedNumber = depositPropertiesDAO.deleteSelection(uriInfo.getQueryParameters()).orElseThrow(() -> new NotFoundException(String.format("Not such deposit with given criteria")));
        return String.format("Deleted record(s): %d.", deletedNumber);
    }

    @POST
    @UnitOfWork
    @Produces("text/plain")
    @Consumes(MediaType.TEXT_PLAIN)
    public String DeleteDepositPropertiesUsingParams(@Context UriInfo uriInfo){
        int deletedNumber = depositPropertiesDAO.deleteSelection(uriInfo.getQueryParameters()).orElseThrow(() -> new NotFoundException(String.format("Not such deposit with given criteria")));
        return String.format("Deleted record(s): %d.", deletedNumber);
    }

    @POST
    @UnitOfWork
    @Produces("text/plain")
    @Consumes("application/json")
    public void DeleteDepositPropertiesUsingJsonObject(@Valid DepositProperties depositProperties) {
        depositPropertiesDAO.delete(depositProperties);
//
//        Response.Status status;
//        System.out.println(Response.serverError().build().getStatusInfo().getReasonPhrase().toString());
//        int deletedNumber = depositPropertiesDAO.delete(depositProperties).orElseThrow(() -> new NotFoundException(String.format("Not such deposit with given criteria")));
//        return String.format("Deleted record(s): %d.", deletedNumber);
    }

}
