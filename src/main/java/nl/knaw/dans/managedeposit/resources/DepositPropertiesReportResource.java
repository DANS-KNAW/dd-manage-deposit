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
import nl.knaw.dans.managedeposit.Conversions;
import nl.knaw.dans.managedeposit.api.DepositPropertiesDto;
import nl.knaw.dans.managedeposit.core.DepositProperties;
import nl.knaw.dans.managedeposit.core.service.InvalidRequestParameterException;
import nl.knaw.dans.managedeposit.db.DepositPropertiesDao;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/report")
public class DepositPropertiesReportResource implements ReportApi {
    private static final Logger log = LoggerFactory.getLogger(DepositPropertiesReportResource.class);
    private static final Conversions mapper = Mappers.getMapper(Conversions.class);
    private final DepositPropertiesDao depositPropertiesDAO;

    public DepositPropertiesReportResource(DepositPropertiesDao depositPropertiesDAO) {
        this.depositPropertiesDAO = depositPropertiesDAO;
    }

    @GET
    @UnitOfWork
    @Produces({ "application/json", "text/csv" })
    public Response reportGet(@QueryParam("user") String user, @QueryParam("state") String state, @QueryParam("startdate") LocalDate startdate, @QueryParam("enddate") LocalDate enddate,
        @QueryParam("deleted") Boolean deleted, @QueryParam("depositid") String depositid) {
        Map<String, List<String>> queryParameters = new HashMap<>();
        if (user != null) queryParameters.put("user", List.of(user));
        if (state != null) queryParameters.put("state", List.of(state));
        if (startdate != null) queryParameters.put("startdate", List.of(startdate.toString()));
        if (enddate != null) queryParameters.put("enddate", List.of(enddate.toString()));
        if (deleted != null) queryParameters.put("deleted", List.of(deleted.toString()));
        if (depositid != null) queryParameters.put("depositid", List.of(depositid));

        try {
            List<DepositProperties> result = depositPropertiesDAO.findSelection(queryParameters);
            return Response.status(Response.Status.OK)
                .entity(result.stream().map(mapper::toDto).collect(Collectors.toList()))
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

    @Override
    @GET
    @UnitOfWork
    @Produces("application/json")
    @Path("/{depositId}")
    public Response reportDepositIdGet(@PathParam("depositId") String depositId) {
        DepositProperties dp = depositPropertiesDAO.findById(depositId)
            .orElseThrow(() -> new NotFoundException(String.format("No such deposit: %s", depositId)));
        return Response.ok(mapper.toDto(dp)).build();
    }
}
