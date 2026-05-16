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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("resource")
public class DepositPropertiesDao extends AbstractDAO<DepositProperties> {

    public DepositPropertiesDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<DepositProperties> findById(String depositId) {
        return Optional.ofNullable(get(depositId));
    }

    public DepositProperties create(DepositProperties dp) {
        return persist(dp);
    }

    public DepositProperties save(DepositProperties dp) {
        return persist(dp);
    }

    public void merge(DepositProperties dp) {
        currentSession().merge(dp);
    }

    public void delete(DepositProperties dp) {
        currentSession().delete(dp);
    }

    public List<DepositProperties> findAll() {
        return currentSession().createQuery("from DepositProperties", DepositProperties.class).list();
    }

    public List<DepositProperties> findSelection(String user, String state, LocalDate startdate, LocalDate enddate, Boolean deleted, String depositid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<DepositProperties> cq = cb.createQuery(DepositProperties.class);
        Root<DepositProperties> root = cq.from(DepositProperties.class);
        List<Predicate> predicates = new ArrayList<>();

        if (user != null) {
            predicates.add(cb.equal(root.get("depositor"), user));
        }
        if (state != null) {
            predicates.add(cb.equal(root.get("depositState"), state));
        }
        if (deleted != null) {
            predicates.add(cb.equal(root.get("deleted"), deleted));
        }
        if (depositid != null) {
            predicates.add(cb.equal(root.get("depositId"), depositid));
        }
        if (startdate != null) {
            var start = OffsetDateTime.of(startdate.atStartOfDay(), ZoneOffset.UTC);
            predicates.add(cb.greaterThan(root.get("depositCreationTimestamp"), start));
        }
        if (enddate != null) {
            var end = OffsetDateTime.of(enddate.atStartOfDay(), ZoneOffset.UTC);
            predicates.add(cb.lessThan(root.get("depositCreationTimestamp"), end));
        }

        if (predicates.isEmpty()) {
            return findAll();
        }

        cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
        return currentSession().createQuery(cq).getResultList();
    }

    public Optional<Integer> updateDeleteFlag(String depositId, boolean deleted) {
        CriteriaBuilder criteriaBuilder = currentSession().getCriteriaBuilder();
        CriteriaUpdate<DepositProperties> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(DepositProperties.class);
        Root<DepositProperties> root = criteriaUpdate.from(DepositProperties.class);

        criteriaUpdate.where(criteriaBuilder.equal(root.get("depositId"), depositId));

        criteriaUpdate.set("deleted", deleted);

        var query = currentSession().createQuery(criteriaUpdate);
        return Optional.of(query.executeUpdate());
    }

}
