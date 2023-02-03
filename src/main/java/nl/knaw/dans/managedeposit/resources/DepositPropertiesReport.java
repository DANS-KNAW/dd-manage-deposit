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

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/report")
@Produces({MediaType.APPLICATION_JSON, "text/csv"})
@Consumes(value = MediaType.APPLICATION_JSON)
public class DepositPropertiesReport {

    private DepositPropertiesDAO depositPropertiesDAO;

    public DepositPropertiesReport(DepositPropertiesDAO depositPropertiesDAO) {
        this.depositPropertiesDAO = depositPropertiesDAO;
    }

    @POST
    @UnitOfWork
    public DepositProperties createDepositPropertiesRecord(@Valid DepositProperties depositProperties) {
        return depositPropertiesDAO.create(depositProperties);
    }

    @GET
    @Produces({"text/csv", MediaType.APPLICATION_JSON})
    @UnitOfWork
    public List<DepositProperties> listDepositProperties() {
        return depositPropertiesDAO.findAll();
    }
}
