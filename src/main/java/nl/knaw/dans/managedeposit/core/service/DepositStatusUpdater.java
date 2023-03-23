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
    public void onCreateDeposit(Path filePath) {
        Optional<DepositProperties> dpObject= depositPropertiesAssembler.assembleObject(filePath, false);
        if ( dpObject.isPresent() ) {
            depositPropertiesDAO.save(dpObject.get());
        }
    }

    @UnitOfWork
    public void onChangeDeposit(Path filePath) {
        Optional<DepositProperties> dpObject= depositPropertiesAssembler.assembleObject(filePath, false);
        if ( dpObject.isPresent() ) {
            depositPropertiesDAO.save(dpObject.get());
        }
    }

    @UnitOfWork
    public void onDeleteDeposit(Path filePath) {
        Optional<DepositProperties> dpObject= depositPropertiesAssembler.assembleObject(filePath, true);
        if ( dpObject.isPresent() ) {
            depositPropertiesDAO.updateDeleteFlag(dpObject.get().getDepositPath(), dpObject.get().isDeleted());
        }
    }

}