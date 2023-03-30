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
package nl.knaw.dans.managedeposit.core.service;

import io.dropwizard.hibernate.UnitOfWork;
import nl.knaw.dans.managedeposit.core.DepositProperties;
import nl.knaw.dans.managedeposit.db.DepositPropertiesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

public class DepositStatusUpdater {
    private static final Logger log = LoggerFactory.getLogger(DepositPropertiesAssembler.class);
    private final DepositPropertiesDAO depositPropertiesDAO;
    private final DepositPropertiesAssembler depositPropertiesAssembler;

    public DepositStatusUpdater(DepositPropertiesDAO depositPropertiesDAO) {
        this.depositPropertiesDAO = depositPropertiesDAO;
        this.depositPropertiesAssembler = new DepositPropertiesAssembler();
    }

    @UnitOfWork
    public void onCreateDeposit(Path depositPropertiesPath) {
        Optional<DepositProperties> dpObject = depositPropertiesAssembler.assembleObject(depositPropertiesPath, false);
        dpObject.ifPresent(depositPropertiesDAO::save);
    }

    @UnitOfWork
    public void onChangeDeposit(Path depositPropertiesPath) {
        Optional<DepositProperties> dpObject = depositPropertiesAssembler.assembleObject(depositPropertiesPath, false);
        dpObject.ifPresent(depositPropertiesDAO::save);
    }

    @UnitOfWork
    public void onDeleteDeposit(Path depositPropertiesPath) {
        // At this stage, the deposit.properties file's handle is present but the content is null (impossible to read data of the file)
        Optional<Integer> deletedNumber = depositPropertiesDAO.updateDeleteFlag(depositPropertiesPath.getName(depositPropertiesPath.getNameCount() - 2).toString(), true);
        log.debug("onDeleteDeposit - 'deleted' mark is set to '{}' for '{}' ", deletedNumber.isPresent(), depositPropertiesPath);
    }

}