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
import lombok.extern.slf4j.Slf4j;
import nl.knaw.dans.managedeposit.Conversions;
import nl.knaw.dans.managedeposit.api.DepositPropertiesDto;
import nl.knaw.dans.managedeposit.core.DepositProperties;
import nl.knaw.dans.managedeposit.db.DepositPropertiesDao;
import org.mapstruct.factory.Mappers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DepositPropertiesReportResource implements ReportApi {
    private static final Conversions mapper = Mappers.getMapper(Conversions.class);
    private final DepositPropertiesDao depositPropertiesDao;

    public DepositPropertiesReportResource(DepositPropertiesDao depositPropertiesDao) {
        this.depositPropertiesDao = depositPropertiesDao;
    }

    @UnitOfWork
    @Override
    public Response reportGet(String user, String state, LocalDate startdate, LocalDate enddate, Boolean deleted, String depositid) {
        try {
            List<DepositProperties> result = depositPropertiesDao.findSelection(user, state, startdate, enddate, deleted, depositid);
            return Response.status(Response.Status.OK)
                .entity(result.stream().map(mapper::toDto).collect(Collectors.toList()))
                .build();
        }
        catch (Exception e) {
            log.error("Error generating report", e);
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

    @UnitOfWork
    @Override
    public Response reportPost(@Valid @NotNull DepositPropertiesDto depositPropertiesDto) {
        DepositProperties depositProperties = mapper.toEntity(depositPropertiesDto);
        DepositProperties created = depositPropertiesDao.create(depositProperties);
        return Response.ok(mapper.toDto(created)).build();
    }

    @UnitOfWork
    @Override
    public Response reportDepositIdGet(String depositId) {
        DepositProperties dp = depositPropertiesDao.findById(depositId)
            .orElseThrow(() -> new NotFoundException(String.format("No such deposit: %s", depositId)));
        return Response.ok(mapper.toDto(dp)).build();
    }
}
