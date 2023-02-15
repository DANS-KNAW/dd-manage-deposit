
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

    private boolean buildQueryDate(Map<String, List<String>> queryParameters, StringBuilder qureyBuffer) {
        boolean addedBeforeDate = false;
        boolean addedAfterDate = false;

        if (queryParameters.containsKey("createdDateBefore")) {
            String dateBefore = queryParameters.get("createdDateBefore").get(0);

            queryParameters.remove("createdDateBefore");
            addedBeforeDate = true;
        }

        if(queryParameters.containsKey("createdDateAfter")) {
            if (addedBeforeDate)
                qureyBuffer.append(" AND ");
            String dateBefore = queryParameters.get("createdDateAfter").get(0);
            queryParameters.remove("createdDateAfter");
            addedAfterDate = true;
        }

        if (addedAfterDate)
            qureyBuffer.append(" ");

        return addedBeforeDate || addedBeforeDate;
    }
    private void buildQuery(RequestMethod requestMethod, Map<String, List<String>> queryParameters, StringBuilder qureyBuffer) {
        // A suggestion to sessionFactory.getCriteriaBuilder()
        switch (requestMethod) {
            case POST:
                return;
            case GET:
                qureyBuffer.append("SELECT dp FROM DepositProperties dp");
                break;

            case PUT:
                qureyBuffer.append("UPDATE * FROM DepositProperties\n");
                break;

            case DELETE:
                qureyBuffer.append("DELETE * FROM DepositProperties\n");
                break;
        }

        if (queryParameters.size() > 0)
            qureyBuffer.append(" WHERE ");

        if ( buildQueryDate(queryParameters, qureyBuffer) && queryParameters.size() > 0)
            qureyBuffer.append(" AND ");

        int mapIndex = queryParameters.size();
        for (String key : queryParameters.keySet()) {
            List<String> value = queryParameters.get(key);

            qureyBuffer.append(key).append("=");
            for (int k = 0; k < value.size(); k++) {
                qureyBuffer.append("\'").append(value.get(k)).append("\'");
                if (k > 1)
                    qureyBuffer.append(" OR ").append(key).append("=");
            }

            if (mapIndex > 1) {
                mapIndex--;
                qureyBuffer.append(" AND ");
            }
        }
    }
    

    public List<DepositProperties> performRequest(RequestMethod requestMethod, Map<String, List<String>> queryParameters, DepositProperties depositProperties) {
        StringBuilder qureyBuffer = new StringBuilder();
        switch (requestMethod) {
            case POST:
                return List.of(depositPropertiesDAO.create(depositProperties));

            case PUT:

                break;

            case GET:
                if (queryParameters.size() == 0)
                    return depositPropertiesDAO.findAll();

                buildQuery(requestMethod, queryParameters, qureyBuffer);
                Query<DepositProperties> query = sessionFactory.getCurrentSession().createQuery(qureyBuffer.toString(), DepositProperties.class);
                List<DepositProperties> deposits = query.getResultList();
                return deposits;
                //List<DepositProperties> deposits = sessionFactory.getCurrentSession().createQuery(qureyBuffer.toString(), DepositProperties.class).getResultList();


            case DELETE:
                buildQuery(requestMethod, queryParameters, qureyBuffer);
                List<DepositProperties> depositList = sessionFactory.getCurrentSession().createQuery(qureyBuffer.toString(), DepositProperties.class).getResultList();
                break;

            default:
                new NotAllowedException("Requested method is not allowed");
                break;

        }
    return List.of(new DepositProperties());
    }

}
