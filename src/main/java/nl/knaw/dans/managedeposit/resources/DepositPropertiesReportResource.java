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
import nl.knaw.dans.managedeposit.core.service.InvalidRequestParameterException;
import nl.knaw.dans.managedeposit.db.DepositPropertiesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

@Path("/report")
public class DepositPropertiesReportResource {
    private static final Logger log = LoggerFactory.getLogger(DepositPropertiesReportResource.class);
    private final DepositPropertiesDAO depositPropertiesDAO;

    public DepositPropertiesReportResource(DepositPropertiesDAO depositPropertiesDAO) {
        this.depositPropertiesDAO = depositPropertiesDAO;
    }

    @GET
    @UnitOfWork
    @Produces({ "application/json", "text/csv" })
    public Response listDepositProperties(@Context UriInfo uriInfo) {
        try {
            return Response.status(Response.Status.OK)
                .entity(depositPropertiesDAO.findSelection(uriInfo.getQueryParameters()))
                .build();
        }
        catch (InvalidRequestParameterException e) {
            log.error(e.getMessage());
            throw new ClientErrorException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            throw new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
        }

    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @GET
    @UnitOfWork
    @Produces("application/json")
    @Path("/{depositId}")
    public DepositProperties getDepositId(@PathParam("depositId") Optional<String> depositId) {
        if (depositId.isPresent())
            return depositPropertiesDAO.findById(depositId.get()).orElseThrow(() -> new NotFoundException(String.format("No such deposit: %s", depositId.orElse(""))));
        else {
            log.warn("Empty argument 'depositId'");
            throw new ClientErrorException("Empty argument 'depositId'", Response.Status.BAD_REQUEST);
        }
    }

}
