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
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Optional;

class DepositPropertiesAssembler {
    private static final Logger log = LoggerFactory.getLogger(DepositPropertiesAssembler.class);

    DepositPropertiesAssembler() {
    }

    Optional<DepositProperties> assembleObject(Path depositPropertiesPath, boolean  updateModificationDateTime) {
        log.debug("assembleObject: '{}'", depositPropertiesPath.getNameCount() - 3);
        DepositProperties dp; // = null
        Configuration configuration;
        try {
            configuration = DepositPropertiesFileReader.readDepositProperties(depositPropertiesPath);
//            configuration = readDepositProperties(Path.of("/Users/alisheikhi/git/service/data-station/dd-manage-deposit/data/auto-ingest/inbox/2d56d760-0d6c-4ab1-993b-76b9ddb5c69e/test.txt"));
//            configuration = readDepositProperties(depositPropertiesPath);

            dp = new DepositProperties(depositPropertiesPath.getName(depositPropertiesPath.getNameCount() - 2).toString(),
                configuration.getString("depositor.userId", ""),
                configuration.getString("bag-store.bag-name", ""),
                configuration.getString("state.label", ""),
                configuration.getString("state.description", ""),
                OffsetDateTime.parse(configuration.getString("creation.timestamp", OffsetDateTime.now().toString())),
                depositPropertiesPath.getName(depositPropertiesPath.getNameCount() - 3).toString());

//            dp = new DepositProperties(depositPropertiesPath.getName(depositPropertiesPath.getNameCount() - 2).toString(),
//                "PANVU",
//                "67631",
//                "SUBMITTED",
//                "Ready to re-ingest",
//                OffsetDateTime.now(),
//                depositPropertiesPath.getName(depositPropertiesPath.getNameCount() - 3).toString());



            long size = calculateFolderSize(depositPropertiesPath.getParent().resolve(dp.getBagName()));
            dp.setStorageInBytes(size);
            if (updateModificationDateTime) {
                dp.setDepositUpdateTimestamp(OffsetDateTime.now());
            }
        }
        catch (ConfigurationException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return Optional.of(dp);
    }

    private long calculateFolderSize(Path path) {
        long size;
        try (var pathStream = Files.walk(path)) {
            size = pathStream
                .filter(p -> p.toFile().isFile())
                .mapToLong(p -> p.toFile().length())
                .sum();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return size;
    }

}