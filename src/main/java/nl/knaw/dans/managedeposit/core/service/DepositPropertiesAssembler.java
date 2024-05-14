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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.util.Optional;

class DepositPropertiesAssembler {
    private static final Logger log = LoggerFactory.getLogger(DepositPropertiesAssembler.class);

    DepositPropertiesAssembler() {
    }

    Optional<DepositProperties> assembleObject(File depositPropertiesFile, long CalculatedFolderSize) {
        Path depositPath = depositPropertiesFile.getParentFile().toPath();
        log.debug("assembleObject(depositPropertiesPath:Path): '{}'", depositPropertiesFile.getAbsolutePath());
        DepositProperties dp = null;
        Configuration configuration;
        try {
            configuration = DepositPropertiesFileReader.readDepositProperties(depositPropertiesFile);
            String creationTime = configuration.getString("creation.timestamp");

            dp = new DepositProperties(depositPath.getFileName().toString(),
                configuration.getString("depositor.userId", ""),
                configuration.getString("bag-store.bag-name", ""),
                configuration.getString("state.label", ""),
                TextTruncation.stripEnd(configuration.getString("state.description", ""), TextTruncation.MAX_DESCRIPTION_LENGTH),
                (creationTime == null || creationTime.isEmpty()) ? null : OffsetDateTime.parse(creationTime),
                TextTruncation.stripBegin(depositPropertiesFile.getParentFile().getParentFile().getAbsolutePath(), TextTruncation.MAX_DIRECTORY_LENGTH),
                CalculatedFolderSize == 0 ? calculateFolderSize(depositPath) : CalculatedFolderSize,
                OffsetDateTime.now());
        }
        catch (ConfigurationException | DateTimeException e) {
            log.error("Error reading deposit.properties file: {}", e.getMessage());
        }
        catch (RuntimeException e) {
            log.error("Error accessing deposit files : {}", e.getMessage());
        }
        return Optional.ofNullable(dp);
    }

    private long calculateFolderSize(Path path) {
        long size = 0;
        if (Files.exists(path)) {
            try (var pathStream = Files.walk(path)) {
                size = pathStream
                    .filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return size;
    }

}