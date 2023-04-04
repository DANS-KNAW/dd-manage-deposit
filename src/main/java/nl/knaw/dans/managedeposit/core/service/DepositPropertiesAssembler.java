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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

class DepositPropertiesAssembler {
    private static final Logger log = LoggerFactory.getLogger(DepositPropertiesAssembler.class);

    DepositPropertiesAssembler() {
    }

    Optional<DepositProperties> assembleObject(Path depositPropertiesPath, boolean deleted) {
        log.debug("assembleObject: '{}'", depositPropertiesPath.getNameCount() - 3);
        DepositProperties dp; // = null
        Configuration configuration;
        try {
            configuration = DepositPropertiesFileReader.readDepositProperties(depositPropertiesPath);
            dp = new DepositProperties(configuration.getString("depositId"),
                configuration.getString("userName"),
                //State.valueOf(configuration.getString("state").toUpperCase()),
                State.INBOX,
                depositPropertiesPath.getName(depositPropertiesPath.getNameCount() - 3).toString(),
                depositPropertiesPath.getName(depositPropertiesPath.getNameCount() - 2).toString(),
                deleted);
        }
        catch (ConfigurationException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return Optional.of(dp);
    }

}