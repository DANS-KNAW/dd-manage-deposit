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

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.List;
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
    public List<DepositProperties> listDepositProperties(@Context UriInfo uriInfo) {
        return depositPropertiesDAO.findSelection(uriInfo.getQueryParameters());
    }

    @GET
    @UnitOfWork
    @Produces("application/json")
    @Path("/{depositId}")
    public DepositProperties getDepositId(@PathParam("depositId") String depositId) {
        Optional<String> toFindDeposit = Optional.of(depositId);
        return depositPropertiesDAO.findById(toFindDeposit.orElse("")).orElseThrow(() -> new NotFoundException(String.format("No such deposit: %s", toFindDeposit.orElse(""))));
    }

}
