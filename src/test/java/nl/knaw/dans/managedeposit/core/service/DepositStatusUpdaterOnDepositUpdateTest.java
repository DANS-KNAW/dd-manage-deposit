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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import nl.knaw.dans.managedeposit.AbstractDatabaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.Files.createDirectories;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class DepositStatusUpdaterOnDepositUpdateTest extends AbstractDatabaseTest {
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    public void setup() throws Exception {
        super.setUp();
        var logger = (Logger) LoggerFactory.getLogger(DepositStatusUpdater.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.setLevel(Level.DEBUG);
        logger.addAppender(listAppender);
    }

    @Test
    public void onCreateDeposit_should_add_a_db_record() throws IOException {
        var depositStatusUpdater = new DepositStatusUpdater(dao);

        // Prepare test data
        var propertiesFile = testDir.resolve("bag/deposit.properties");
        createDirectories(propertiesFile.getParent());
        Files.writeString(propertiesFile, """
            bag-store.bag-id = a78c2cad-d473-401d-8751-a447ef573b7a
            dataverse.bag-id = urn:uuid:a78c2cad-d473-401d-8751-a447ef573b7a
            creation.timestamp = 2023-08-16T17:40:41.390209+02:00
            deposit.origin = SWORD2
            depositor.userId = user001
            bag-store.bag-name = revision03
            dataverse.sword-token = sword:a78c2cad-d473-401d-8751-a447ef573b7a
            """);

        // Call the method under test
        depositStatusUpdater.onDepositCreate(propertiesFile.toFile());

        // Check the logs
        var formattedMessage = listAppender.list.get(0).getFormattedMessage();
        assertThat(formattedMessage).startsWith("onDepositCreate: A new deposit has been registered '");
        assertThat(formattedMessage).endsWith("DepositStatusUpdaterOnDepositUpdateTest/bag'");

        // Check the database
        assertThat(dao.findById("bag")).isNotEmpty().get()
            .isEqualTo("revision03");
        assertThat(dao.findAll()).isNotEmpty();
    }

    // TODO: other scenario's and test classes for onChangeDeposit and onDeleteDeposit
}