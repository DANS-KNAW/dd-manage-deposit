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
import nl.knaw.dans.managedeposit.core.State;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public DepositProperties save(DepositProperties dp) {
        return persist(dp);
    }

    public void merge(DepositProperties dp) {
        currentSession().merge(dp);
    }

//    public void updateDeleteFlag(String depositPath, boolean deleted) {
//        int r = currentSession()
//            .createQuery("DELETE FROM ExpectedDataset WHERE doi = :doi")
//            .setParameter("doi", doi)
//            .executeUpdate();
//    }

    public void delete(DepositProperties dp) {
        currentSession().delete(dp);
        //        if (Optional.ofNullable(currentSession().find(DepositProperties.class, dp)) != null) {
        //            currentSession().delete(dp);
        //            Optional.ofNullable(0);
        //        }
        //        return Optional.ofNullable(1);
    }

    public List<DepositProperties> findAllDefaultQuery() {
        return list(namedTypedQuery("showAll"));
    }

    public List<DepositProperties> findAll() {
        return currentSession().createQuery("from DepositProperties", DepositProperties.class).list();
    }

    public List<DepositProperties> findSelection(Map<String, List<String>> queryParameters) {
        CriteriaBuilder criteriaBuilder = currentSession().getCriteriaBuilder();

        if (queryParameters.size() == 0)
            return findAll();

        CriteriaQuery<DepositProperties> criteriaQuery = criteriaBuilder.createQuery(DepositProperties.class);
        Root<DepositProperties> root = criteriaQuery.from(DepositProperties.class);
        Predicate predicate = buildQueryCriteria(queryParameters, criteriaBuilder, root);
        criteriaQuery.select(root).where(predicate);
        Query<DepositProperties> query = currentSession().createQuery(criteriaQuery);
        return query.getResultList();

        //CriteriaUpdate<DepositProperties> cu = criteriaBuilder.createCriteriaUpdate(DepositProperties.class);

        //                Query<DepositProperties> getQuery = currentSession().createQuery(queryStringBuildr.toString(), DepositProperties.class);
        //                List<DepositProperties> deposits = getQuery.getResultList();

        //              List<DepositProperties> deposits = sessionFactory.getCurrentSession().createQuery(queryStringBuildr.toString(), DepositProperties.class).getResultList();
    }

    public Optional<Integer> deleteSelection(Map<String, List<String>> queryParameters) {
        CriteriaBuilder criteriaBuilder = currentSession().getCriteriaBuilder();
        CriteriaDelete<DepositProperties> deleteQuery = criteriaBuilder.createCriteriaDelete(DepositProperties.class);
        Root<DepositProperties> root = deleteQuery.from(DepositProperties.class);

        Predicate predicate = buildQueryCriteria(queryParameters, criteriaBuilder, root);

        deleteQuery.where(predicate);
        Query<DepositProperties> query = currentSession().createQuery(deleteQuery);
        int x = query.executeUpdate();
        return Optional.ofNullable(x);
    }

    private Predicate buildQueryCriteria(Map<String, List<String>> queryParameters, CriteriaBuilder criteriaBuilder, Root<DepositProperties> root) {
        List<Predicate> predicates = new ArrayList<>();
        Predicate predicate;

        for (String key : queryParameters.keySet()) {
            List<String> values = queryParameters.get(key);
            String parameter = key.toLowerCase();
            //javax.persistence.criteria
            Predicate orPredicate;
            List<Predicate> orPredicates = new ArrayList<>();
            for (String value : values) {
                switch (parameter) {
                    case "depositid":
                        orPredicate = criteriaBuilder.equal(root.get("depositId"), value);
                        break;

                    case "user":
                        orPredicate = criteriaBuilder.equal(root.get("userName"), value);
                        break;

                    case "deleted":
                        if (Boolean.parseBoolean(value))
                            orPredicate = criteriaBuilder.isTrue(root.get("deleted"));
                        else
                            orPredicate = criteriaBuilder.isFalse(root.get("deleted"));
                        break;

                    case "state":
                        State requestedState = State.valueOf(value.toUpperCase());
                        orPredicate = criteriaBuilder.equal(root.get("state"), requestedState);
                        break;

                    case "startdate":
                    case "enddate":
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate date = LocalDate.parse(value, formatter);
                        var asked_date = OffsetDateTime.of(date.atStartOfDay(), ZoneOffset.UTC);

                        if (parameter.equals("startdate"))
                            orPredicate = criteriaBuilder.greaterThan(root.get("createdDate"), asked_date);
                        else
                            orPredicate = criteriaBuilder.lessThan(root.get("createdDate"), asked_date);
                        break;

                    default:
                        orPredicate = criteriaBuilder.equal(root.get(key), value);
                }

                orPredicates.add(orPredicate);
            }
            orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
            predicates.add(orPredicate);
        }

        predicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        return predicate;
    }

//    public List<DepositProperties> UpdatePath(Map<String, List<String>> queryParameters, String path) {
    public List<DepositProperties> updateDeleteFlag(String depositPath, boolean deleted) {
        CriteriaBuilder criteriaBuilder = currentSession().getCriteriaBuilder();
        CriteriaUpdate<DepositProperties> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(DepositProperties.class);
        Root<DepositProperties> root = criteriaUpdate.from(DepositProperties.class);

        Predicate predicate = buildQueryCriteria(Map.of("depositPath",List.of(depositPath)), criteriaBuilder, root);
        criteriaUpdate.where(predicate);

        criteriaUpdate.set("deleted", true);

        Transaction transaction = currentSession().beginTransaction();

        Query<DepositProperties> query = currentSession().createQuery(criteriaUpdate);
        List<DepositProperties> deposits = query.getResultList();

        currentSession().createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

        return deposits;
    }

    public List<DepositProperties> UpdateSelection(Map<String, List<String>> queryParameters) {
        CriteriaBuilder criteriaBuilder = currentSession().getCriteriaBuilder();
        CriteriaUpdate<DepositProperties> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(DepositProperties.class);
        Root<DepositProperties> root = criteriaUpdate.from(DepositProperties.class);

        Predicate predicate = buildQueryCriteria(queryParameters, criteriaBuilder, root);
        criteriaUpdate.where(predicate);

        //        criteriaUpdate.set("", "");

        Transaction transaction = currentSession().beginTransaction();

        Query<DepositProperties> query = currentSession().createQuery(criteriaUpdate);
        List<DepositProperties> deposits = query.getResultList();

        currentSession().createQuery(criteriaUpdate).executeUpdate();
        transaction.commit();

        return deposits;
    }

}
