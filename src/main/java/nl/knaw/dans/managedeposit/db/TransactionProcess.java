
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;

public class TransactionProcess {
    private static final Logger log = LoggerFactory.getLogger(TransactionProcess.class);

    UriInfo uriInfo;
    RequestMethod requestMethod;
    
    String transaction;

    public TransactionProcess(UriInfo uriInfo, RequestMethod requestMethod) {
        this.uriInfo = uriInfo;
        this.requestMethod = requestMethod;
    }
    
    void buildTransactionFilter() {

        Map<String, List<String>> queryParameters = uriInfo.getQueryParameters();
        
        if (queryParameters.containsKey("createdDateBefore")) {
            String dateBefore = queryParameters.get("createdDateBefore").get(0);
            
            queryParameters.remove("createdDateBefore");
        }
        
        if(queryParameters.containsKey("createdDateAfter")) {
            String dateBefore = queryParameters.get("createdDateAfter").get(0);
            queryParameters.remove("createdDateAfter"); 
        }
        
        for (String key: queryParameters.keySet() ) {
            List<String> value = uriInfo.getQueryParameters().get(key);
            String addOr =  value.size() > 1 ? " OR " : "";
            for (String val:value) {

            }
            System.out.println(value);
        }
    }
    
    

    List<DepositProperties> performRequest() {

        switch (this.requestMethod) {
            case POST:

                break;

            case PUT:

                break;

            case GET:

                break;

            case DELETE:

                break;

            default:
                new NotAllowedException("Requseted method is not allowed");
                break;

        }
    return List.of(new DepositProperties());
    }

}
