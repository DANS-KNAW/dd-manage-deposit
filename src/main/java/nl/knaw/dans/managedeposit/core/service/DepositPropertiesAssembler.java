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

import nl.knaw.dans.managedeposit.core.DepositProperties;
import nl.knaw.dans.managedeposit.core.State;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.nio.file.Path;
import java.util.Optional;

class DepositPropertiesAssembler {
    DepositPropertiesAssembler() {
     }

     Optional<DepositProperties> assembleObject(Path depositPropertiesPath, boolean deleted) {
         Path statusPath = depositPropertiesPath.getName(depositPropertiesPath.getNameCount() - 3);
         System.out.println("assembleObject: " + statusPath);
         DepositProperties dp = null;
         Configuration configuration;
         try {
             configuration = ReadDepositProperties.readDepositProperties(depositPropertiesPath);
             dp = new DepositProperties(configuration.getString("depositId"),
                 configuration.getString("userName"),
                 //State.valueOf(configuration.getString("state").toUpperCase()),
                 State.INBOX,
                 statusPath.toString(),
                 depositPropertiesPath.getParent().toString(),
                 deleted);

         }
         catch (ConfigurationException e) {
             throw new RuntimeException(e);
         }
         return Optional.ofNullable(dp);
     }

     }