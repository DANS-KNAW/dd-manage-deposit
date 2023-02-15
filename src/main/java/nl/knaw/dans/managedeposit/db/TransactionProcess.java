
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

import nl.knaw.dans.managedeposit.core.DepositProperties;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.ws.rs.NotAllowedException;
import java.util.List;
import java.util.Map;

public class TransactionProcess {
    private static final Logger log = LoggerFactory.getLogger(TransactionProcess.class);

    private final DepositPropertiesDAO depositPropertiesDAO;
    private final SessionFactory sessionFactory;
    CriteriaBuilder builder;

    public TransactionProcess(DepositPropertiesDAO depositPropertiesDAO, SessionFactory sessionFactory) {
        this.depositPropertiesDAO = depositPropertiesDAO;
        this.sessionFactory = sessionFactory;
    }

    private boolean buildQueryDate(Map<String, List<String>> queryParameters, StringBuilder queryStringBuildr) {
        boolean addedBeforeDate = false;
        boolean addedAfterDate = false;

        if (queryParameters.containsKey("createdDateBefore")) {
            String dateBefore = queryParameters.get("createdDateBefore").get(0);

            queryParameters.remove("createdDateBefore");
            addedBeforeDate = true;
        }

        if(queryParameters.containsKey("createdDateAfter")) {
            if (addedBeforeDate)
                queryStringBuildr.append(" AND ");
            String dateBefore = queryParameters.get("createdDateAfter").get(0);
            queryParameters.remove("createdDateAfter");
            addedAfterDate = true;
        }

        if (addedAfterDate)
            queryStringBuildr.append(" ");

        return addedBeforeDate || addedBeforeDate;
    }
    private void buildQuery(RequestMethod requestMethod, Map<String, List<String>> queryParameters, StringBuilder queryStringBuildr) {
        // A suggestion to sessionFactory.getCriteriaBuilder()
        switch (requestMethod) {
            case POST:
                return;
            case GET:
                queryStringBuildr.append("SELECT dp FROM DepositProperties dp");
                break;

            case PUT:
                queryStringBuildr.append("UPDATE * FROM DepositProperties");
                break;

            case DELETE:
                queryStringBuildr.append("DELETE FROM DepositProperties");
                break;
        }

        if (queryParameters.size() > 0)
            queryStringBuildr.append(" WHERE ");

        if ( buildQueryDate(queryParameters, queryStringBuildr) && queryParameters.size() > 0)
            queryStringBuildr.append(" AND ");

        int mapIndex = queryParameters.size();
        for (String key : queryParameters.keySet()) {
            List<String> value = queryParameters.get(key);

            queryStringBuildr.append(key).append("=");
            for (int k = 0; k < value.size(); k++) {
                queryStringBuildr.append("\'").append(value.get(k)).append("\'");
                if (k > 1)
                    queryStringBuildr.append(" OR ").append(key).append("=");
            }

            if (mapIndex > 1) {
                mapIndex--;
                queryStringBuildr.append(" AND ");
            }
        }
    }
    

    public List<DepositProperties> performRequest(RequestMethod requestMethod, Map<String, List<String>> queryParameters, DepositProperties depositProperties) {
        StringBuilder queryStringBuildr = new StringBuilder();
        switch (requestMethod) {
            case POST:
                return List.of(depositPropertiesDAO.create(depositProperties));

            case PUT:

                break;

            case GET:
                if (queryParameters.size() == 0)
                    return depositPropertiesDAO.findAll();

                buildQuery(requestMethod, queryParameters, queryStringBuildr);
                Query<DepositProperties> getQuery = sessionFactory.getCurrentSession().createQuery(queryStringBuildr.toString(), DepositProperties.class);
                List<DepositProperties> deposits = getQuery.getResultList();
                return deposits;
                //List<DepositProperties> deposits = sessionFactory.getCurrentSession().createQuery(queryStringBuildr.toString(), DepositProperties.class).getResultList();


            case DELETE:
                buildQuery(requestMethod, queryParameters, queryStringBuildr);
                Query<DepositProperties> deleteQuery = sessionFactory.getCurrentSession().createQuery(queryStringBuildr.toString());
                /*List<DepositProperties> deleteDeposits*/ int x  = deleteQuery.executeUpdate();// getResultList();
                //return  deleteDeposits;
                break;

            default:
                new NotAllowedException("Requested method is not allowed");
                break;

        }
    return List.of(new DepositProperties());
    }

}
