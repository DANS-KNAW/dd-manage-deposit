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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    public void onCreateDeposit(File depositPropertiesFile) {
        Optional<DepositProperties> dp = depositPropertiesDAO.findById(depositPropertiesFile.getParentFile().getName());

        if (dp.isPresent()) {
            // The 'move deposit' action is processed in two steps: 1. `create` deposit in the new location; 2. `delete` it from the old location. Update the location column.
//            Path currentFolder = depositPropertiesPath.getName(depositPropertiesPath.getNameCount() - 3);
            Path depositLocationFolder = Path.of(depositPropertiesFile.getParentFile().getParentFile().getAbsolutePath());
            Optional<Integer> updatedNumber = depositPropertiesDAO.updateDepositLocation(dp.get().getDepositId(), depositLocationFolder);
            if (updatedNumber.isPresent())
                log.debug("onCreateDeposit - `location` of deposit '{}' has been updated to '{}' ", dp.get().getDepositId(), depositLocationFolder);
        }
        else {
            Optional<DepositProperties> dpObject = depositPropertiesAssembler.assembleObject(depositPropertiesFile, false);
            dpObject.ifPresent(depositPropertiesDAO::save);
            log.debug("onCreateDeposit: A new deposit has been registered `{}`", depositPropertiesFile.getParentFile().getAbsolutePath());
        }
    }

    @UnitOfWork
    public void onChangeDeposit(File depositPropertiesFile) {
        Optional<DepositProperties> dpObject = depositPropertiesAssembler.assembleObject(depositPropertiesFile, true);
        dpObject.ifPresent(depositPropertiesDAO::save);
        log.debug("onChangeDeposit: deposit.properties has been changed `{}`", depositPropertiesFile.getParentFile().getAbsolutePath());
    }

    @UnitOfWork
    public void onDeleteDeposit(File depositPropertiesFile) {
        // At this stage, the deposit.properties file's handle is present but the content is null (impossible to read data of the file)
        Optional<DepositProperties> dp = depositPropertiesDAO.findById(depositPropertiesFile.getParentFile().getName());

        try {
            // The 'move deposit' action is processed in two steps: 1. `create` deposit in the new location; 2. `delete` it from the old location. Ignore delete step
            if (dp.isPresent() && Files.isSameFile(Path.of(dp.get().getLocation()), Path.of(depositPropertiesFile.getParentFile().getParentFile().getAbsolutePath()))) {
                String depositId = depositPropertiesFile.getParentFile().getName();
                Optional<Integer> deletedNumber = depositPropertiesDAO.updateDeleteFlag(depositId, true);
                log.debug("onDeleteDeposit - 'deleted' mark has been set to '{}' for deposit.properties from '{}' ", deletedNumber.isPresent(), depositId);
            }
        }
        catch (IOException e) {
            log.debug("The 'move deposit' action is processed in two steps: 1. `create` deposit in the new location; 2. `delete` it from the old location. Ignore delete step for {}", depositPropertiesFile.getParentFile().getAbsolutePath());
        }
    }

}