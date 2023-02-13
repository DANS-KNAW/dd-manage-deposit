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
package nl.knaw.dans.managedeposit.db;

import io.dropwizard.hibernate.AbstractDAO;
import nl.knaw.dans.managedeposit.core.DepositProperties;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class DepositPropertiesDAO extends AbstractDAO<DepositProperties> {

    public DepositPropertiesDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }


    public Optional<DepositProperties> findById(String depositId) {
        return Optional.ofNullable(get(depositId));
    }

    public DepositProperties create(DepositProperties dp) {
        return persist(dp);
    }

    public void delete(DepositProperties dp) {
        currentSession().delete(dp);
    }


    public List<DepositProperties> findAllDefaultQuery() {
        return list(namedTypedQuery("nl.knaw.dans.managedeposit.core.DepositProperties.findAll"));
    }

    public List<DepositProperties> findAll() {
        return currentSession().createQuery("from DepositProperties", DepositProperties.class).list();
    }
}
