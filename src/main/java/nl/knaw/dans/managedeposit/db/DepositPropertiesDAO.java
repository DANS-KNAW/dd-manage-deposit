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
import nl.knaw.dans.managedeposit.core.service.InvalidRequestParameterException;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("resource")
public class DepositPropertiesDAO extends AbstractDAO<DepositProperties> {
    private static final Logger log = LoggerFactory.getLogger(DepositPropertiesDAO.class);

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

    public void delete(DepositProperties dp) {
        currentSession().delete(dp);
    }

    public List<DepositProperties> findAll() {
        return currentSession().createQuery("from DepositProperties", DepositProperties.class).list();
    }

    public List<DepositProperties> findSelection(Map<String, List<String>> queryParameters) {
        CriteriaBuilder criteriaBuilder = currentSession().getCriteriaBuilder();

        if (queryParameters.isEmpty())
            return findAll();

        CriteriaQuery<DepositProperties> criteriaQuery = criteriaBuilder.createQuery(DepositProperties.class);
        Root<DepositProperties> root = criteriaQuery.from(DepositProperties.class);
        Predicate predicate = buildQueryCriteria(queryParameters, criteriaBuilder, root);
        criteriaQuery.select(root).where(predicate);
        Query<DepositProperties> query = currentSession().createQuery(criteriaQuery);
        return query.getResultList();
    }

    public Optional<Integer> deleteSelection(Map<String, List<String>> queryParameters) {
        var criteriaBuilder = currentSession().getCriteriaBuilder();
        if (queryParameters.isEmpty()) {
            throw new InvalidRequestParameterException("Delete command without argument would make database empty. It is not accepted.");
        }

        CriteriaDelete<DepositProperties> deleteQuery = criteriaBuilder.createCriteriaDelete(DepositProperties.class);
        Root<DepositProperties> root = deleteQuery.from(DepositProperties.class);

        Predicate predicate = buildQueryCriteria(queryParameters, criteriaBuilder, root);

        deleteQuery.where(predicate);
        var query = currentSession().createQuery(deleteQuery);
        return Optional.of(query.executeUpdate());
    }

    private Predicate buildQueryCriteria(Map<String, List<String>> queryParameters, CriteriaBuilder criteriaBuilder, Root<DepositProperties> root) {
        List<Predicate> predicates = new ArrayList<>();
        boolean startDateSpecifiedAlready = false, endDateSpecifiedAlready = false;
        Predicate predicate;

        for (String key : queryParameters.keySet()) {
            List<String> values = queryParameters.get(key);
            String parameter = key.toLowerCase();
            if (values.isEmpty()) {
                throw new InvalidRequestParameterException(String.format("Empty value of parameter %s", parameter));
            }
            //javax.persistence.criteria
            Predicate orPredicateItem;
            List<Predicate> orPredicatesList = new ArrayList<>();
            for (String value : values) {
                switch (parameter) {
                    case "depositid":
                        orPredicateItem = criteriaBuilder.equal(root.get("depositId"), value);
                        break;

                    case "user":
                        orPredicateItem = criteriaBuilder.equal(root.get("depositor"), value);
                        break;

                    case "deleted":
                        if (Boolean.parseBoolean(value))
                            orPredicateItem = criteriaBuilder.isTrue(root.get("deleted"));
                        else
                            orPredicateItem = criteriaBuilder.isFalse(root.get("deleted"));
                        break;

                    case "state":
                        orPredicateItem = criteriaBuilder.equal(root.get("depositState"), value);
                        break;

                    case "startdate":
                    case "enddate":
                        if (value.isEmpty()) {
                            orPredicateItem = criteriaBuilder.isNull(root.get("depositCreationTimestamp"));
                        }
                        else {
                            try {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                LocalDate date = LocalDate.parse(value, formatter);
                                var asked_date = OffsetDateTime.of(date.atStartOfDay(), ZoneOffset.UTC);

                                if (parameter.equals("startdate")) {
                                    if (startDateSpecifiedAlready)
                                        throw new InvalidRequestParameterException(String.format("Duplicated startdate parameter %s = %s ", parameter, value));
                                    orPredicateItem = criteriaBuilder.greaterThan(root.get("depositCreationTimestamp"), asked_date);
                                    startDateSpecifiedAlready = true;
                                }
                                else {
                                    if (endDateSpecifiedAlready)
                                        throw new InvalidRequestParameterException(String.format("Duplicated enddate parameter %s = %s ", parameter, value));
                                    orPredicateItem = criteriaBuilder.lessThan(root.get("depositCreationTimestamp"), asked_date);
                                    endDateSpecifiedAlready = true;
                                }
                            }
                            catch (DateTimeException e) {
                                log.error("Invalid or incorrectly formatted date parameter {}", e.getMessage());
                                String error = String.format("Invalid or incorrectly formatted parameter %s = %s ", parameter, value);
                                throw new InvalidRequestParameterException(String.format("Invalid or incorrectly formatted parameter %s = %s ", parameter, value));
                            }
                        }
                        break;

                    default:
                        throw new InvalidRequestParameterException(String.format("Unknown parameter %s = %s ", parameter, value));
                }
                orPredicatesList.add(orPredicateItem);
            }

            orPredicateItem = criteriaBuilder.or(orPredicatesList.toArray(new Predicate[0]));
            predicates.add(orPredicateItem);
        }

        predicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        return predicate;
    }

    public Optional<Integer> updateDeleteFlag(String depositId, boolean deleted) {
        CriteriaBuilder criteriaBuilder = currentSession().getCriteriaBuilder();
        CriteriaUpdate<DepositProperties> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(DepositProperties.class);
        Root<DepositProperties> root = criteriaUpdate.from(DepositProperties.class);

        Predicate predicate = buildQueryCriteria(Map.of("depositId", List.of(depositId)), criteriaBuilder, root);
        criteriaUpdate.where(predicate);

        criteriaUpdate.set("deleted", deleted);

        var query = currentSession().createQuery(criteriaUpdate);
        return Optional.of(query.executeUpdate());
    }

}
