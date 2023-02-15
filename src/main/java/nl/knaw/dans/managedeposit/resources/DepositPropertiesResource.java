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
import nl.knaw.dans.managedeposit.db.RequestMethod;
import nl.knaw.dans.managedeposit.db.TransactionProcess;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/deposits")
public class DepositPropertiesResource {
    private static final Logger log = LoggerFactory.getLogger(DepositPropertiesResource.class);
    //log.info("Returning service document for user {} and collections {}", depositor, collectionIds);
    private final DepositPropertiesDAO depositPropertiesDAO;
    private final TransactionProcess transactionProcess;

    public DepositPropertiesResource(DepositPropertiesDAO depositPropertiesDAO, SessionFactory sessionFactory) {
        this.depositPropertiesDAO = depositPropertiesDAO;
        this.transactionProcess = new TransactionProcess(depositPropertiesDAO, sessionFactory);
    }

    @POST
    @UnitOfWork
    @Consumes("application/json")
    @Produces("application/json")
    public DepositProperties createDepositPropertiesRecord(@Valid DepositProperties depositProperties, @Context UriInfo uriInfo) {
        return transactionProcess.performRequest(RequestMethod.POST,  uriInfo.getQueryParameters(), depositProperties).get(0);
    }

    @GET
    @UnitOfWork
    @Produces({ "application/json", "text/csv" })
    @Path("/report")
    public List<DepositProperties> listDepositProperties(@Context UriInfo uriInfo) {
       return transactionProcess.performRequest(RequestMethod.GET,  uriInfo.getQueryParameters(), null);

    }

    @GET
    @UnitOfWork
    @Produces("application/json")
    @Path("/{depositId}")
    public List<DepositProperties> getDepositId(@PathParam("depositId") Optional<String> depositId) {
        Map<String, List<String>> queryParam= new HashMap();
        queryParam.put("depositId", List.of(depositId.get()));
        return  transactionProcess.performRequest(RequestMethod.GET, queryParam, null);
//        return depositPropertiesDAO.findById(depositId.get()).orElseThrow(() -> new NotFoundException(String.format("No such deposit: %s", depositId.get())));
    }

    @DELETE
    @UnitOfWork
    @Produces("application/json")
    public void DeleteDepositPropertiesRecord(@Context UriInfo uriInfo) {
        for (String key: uriInfo.getQueryParameters().keySet() ){
            List<String> value = uriInfo.getQueryParameters().get(key);
            System.out.println(value);

        }
      transactionProcess.performRequest(RequestMethod.DELETE, uriInfo.getQueryParameters(), null);
    }

//        @DELETE //TO_DO : must be replaced with above commented block + need changes in TransactionProcess class
//        @UnitOfWork
//        @Produces("application/json")
//        public DepositProperties DeleteDepositPropertiesRecord(@QueryParam("id") Optional<String> id) {
//            if (id.isEmpty())
//                new ClientErrorException("id parameter is empty", Response.Status.BAD_REQUEST);
//            DepositProperties depositProperties = depositPropertiesDAO.findById(id.get()).orElseThrow(() -> new NotFoundException(String.format("No such deposit: %s", id.get())));
//            depositPropertiesDAO.delete(depositProperties);
//            return depositProperties;
//        }

}
