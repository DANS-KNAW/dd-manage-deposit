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
import nl.knaw.dans.managedeposit.core.State;
import nl.knaw.dans.managedeposit.db.DepositPropertiesDAO;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.hibernate.SessionFactory;

import java.io.File;

public  class PathUpdate {
    private final DepositPropertiesDAO depositPropertiesDAO;
    private final SessionFactory hibernateSessionFactory;

    public PathUpdate(DepositPropertiesDAO depositPropertiesDAO, SessionFactory sessionFactory) {
        this.depositPropertiesDAO = depositPropertiesDAO;
        this.hibernateSessionFactory = sessionFactory;
    }

    @UnitOfWork
    public void onCreateDeposit(File file) {
        Configuration configuration;
        try {
            configuration = ReadDepositProperties.readDepositProperties(file.toPath());
            DepositProperties dp = new DepositProperties(configuration.getString("depositId"),
                configuration.getString("userName"), State.INBOX, false);

            DepositProperties dp2 = depositPropertiesDAO.create(dp);
        }
        catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @UnitOfWork
    public void onMoveDeposit(File file) {
        Configuration configuration;
        try {
            configuration = ReadDepositProperties.readDepositProperties(file.toPath());
            DepositProperties dp = new DepositProperties(configuration.getString("depositId"),
                configuration.getString("userName"), null /*State.INBOX*/, false);

        }
        catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @UnitOfWork
    public void onDeleteDeposit(File file) {
        Configuration configuration;
        try {
            configuration = ReadDepositProperties.readDepositProperties(file.toPath());
            DepositProperties dp = new DepositProperties(configuration.getString("depositId"),
                configuration.getString("userName"), null /*State.INBOX*/, false);

        }
        catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

//    @Override
//    public void onFileCreate(File file) {
//    }
//
//    @Override
//    public void onFileChange(File file) {
//    }
//
//
//    @Override
//    public void onFileDelete(File file) {
//    }
//
//    @Override
//    public void onStart(FileAlterationObserver fileAlterationObserver) {
//
//    }
//
//    @Override
//    public void onDirectoryCreate(File file) {
//
//    }
//
//    @Override
//    public void onDirectoryChange(File file) {
//
//    }
//
//    @Override
//    public void onDirectoryDelete(File file) {
//
//    }
//
//    @Override
//    public void onStop(FileAlterationObserver fileAlterationObserver) {
//
//    }
}
