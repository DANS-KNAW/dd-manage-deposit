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
    private static final Logger log = LoggerFactory.getLogger(DepositStatusUpdater.class);
    private final DepositPropertiesDAO depositPropertiesDAO;
    private final DepositPropertiesAssembler depositPropertiesAssembler;

    public DepositStatusUpdater(DepositPropertiesDAO depositPropertiesDAO) {
        this.depositPropertiesDAO = depositPropertiesDAO;
        this.depositPropertiesAssembler = new DepositPropertiesAssembler();
    }

    @UnitOfWork
    public void onDepositCreate(File depositPropertiesFile) {
        // Note: the 'move deposit' action is processed by the system in two steps: 1. `create` deposit in the new location; 2. `delete` it from the old location.
        Optional<DepositProperties> record = depositPropertiesDAO.findById(depositPropertiesFile.getParentFile().getName());

        if (record.isPresent()) {
            // Update the location column and other fields
            Optional<DepositProperties> dpObject = depositPropertiesAssembler.assembleObject(depositPropertiesFile, true, record.get().getStorageInBytes());
            dpObject.ifPresent(depositPropertiesDAO::merge);
            log.debug("onDepositCreate - The deposit '{}' location and/or state has been updated to '{}' ", record.get().getDepositId(), depositPropertiesFile.getParentFile().getAbsolutePath());
        }
        else {
            Optional<DepositProperties> dpObject = depositPropertiesAssembler.assembleObject(depositPropertiesFile, false, 0);
            dpObject.ifPresent(depositPropertiesDAO::save);
            log.debug("onDepositCreate: A new deposit has been registered '{}'",
                depositPropertiesFile.getParentFile().getAbsolutePath());//log.info("onCreateDeposit: A new deposit has been registered `{}`", depositPropertiesFile.getParentFile().getAbsolutePath());
        }
    }

    @UnitOfWork
    public void onDepositChange(File depositPropertiesFile) {
        Optional<DepositProperties> record = depositPropertiesDAO.findById(depositPropertiesFile.getParentFile().getName());
        long folder_size = record.isPresent() ? record.get().getStorageInBytes() : 0;
        Optional<DepositProperties> dpObject = depositPropertiesAssembler.assembleObject(depositPropertiesFile, true, folder_size);
        dpObject.ifPresent(depositPropertiesDAO::merge);
        log.debug("onDepositChange: deposit.properties has been changed '{}'", depositPropertiesFile.getParentFile().getAbsolutePath());
    }

    @UnitOfWork
    public void onDepositDelete(File depositPropertiesFile) {
        // Note: if delete notify is part of a folder moving, then at this stage, the deposit.properties file's handle is present but the content is null (impossible to read data of the file)
        Optional<DepositProperties> record = depositPropertiesDAO.findById(depositPropertiesFile.getParentFile().getName());

        // The 'move deposit' action is processed in two steps: 1. `create` deposit in the new location; 2. `delete` it from the old location. Ignore delete step
        if (record.isPresent()) {
            log.debug("OnDepositDelete: \n record.getLocation(): {} \n deposit-path-argument: {}\n depositPropertiesFile.exists() : {}\n", record.get().getLocation(),
                depositPropertiesFile.getParentFile().getParentFile().getAbsolutePath(), depositPropertiesFile.exists());
            try {
                if (Files.isSameFile(Path.of(record.get().getLocation()), Path.of(depositPropertiesFile.getParentFile().getParentFile().getAbsolutePath())) && !depositPropertiesFile.exists()) {
                    String depositId = depositPropertiesFile.getParentFile().getName();
                    Optional<Integer> deletedNumber = depositPropertiesDAO.updateDeleteFlag(depositId, true);
                    log.debug("onDepositDelete - 'deleted' mark has been set to '{}' for deposit.properties from '{}' ", deletedNumber.isPresent() && deletedNumber.get() > 0, depositId);
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}