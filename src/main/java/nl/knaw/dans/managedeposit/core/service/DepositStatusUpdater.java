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
import org.hibernate.SessionFactory;

import java.nio.file.Path;
import java.util.Optional;

public  class DepositStatusUpdater {
    private final DepositPropertiesDAO depositPropertiesDAO;
    private final SessionFactory hibernateSessionFactory;
    private final DepositPropertiesAssembler depositPropertiesAssembler;

    public DepositStatusUpdater(DepositPropertiesDAO depositPropertiesDAO, SessionFactory sessionFactory) {
        this.depositPropertiesDAO = depositPropertiesDAO;
        this.hibernateSessionFactory = sessionFactory;
        this.depositPropertiesAssembler = new DepositPropertiesAssembler();
    }

    @UnitOfWork
    public void onCreateDeposit(Path depositPropertiesPath) {
        Optional<DepositProperties> dpObject= depositPropertiesAssembler.assembleObject(depositPropertiesPath, false);
        if ( dpObject.isPresent() ) {
            depositPropertiesDAO.save(dpObject.get());
        }
    }

    @UnitOfWork
    public void onChangeDeposit(Path depositPropertiesPath) {
        Optional<DepositProperties> dpObject= depositPropertiesAssembler.assembleObject(depositPropertiesPath, false);
        if ( dpObject.isPresent() ) {
            depositPropertiesDAO.save(dpObject.get());
        }
    }

    @UnitOfWork
    public void onDeleteDeposit(Path depositPropertiesPath) {
        // At this stage, the deposit.properties file's handle is present but the content is null (impossible to read data of the file)
        depositPropertiesDAO.updateDeleteFlag(depositPropertiesPath.getName(depositPropertiesPath.getNameCount() - 2).toString(), true);
    }

}