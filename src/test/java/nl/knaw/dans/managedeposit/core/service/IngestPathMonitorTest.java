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

import nl.knaw.dans.managedeposit.AbstractTestWithTestDir;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Files;

import static java.nio.file.Files.createDirectories;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class IngestPathMonitorTest extends AbstractTestWithTestDir {

    private IngestPathMonitor startMonitor(DepositStatusUpdater mockUpdater, int pollingInterval) throws Exception {
        var monitor = new IngestPathMonitor(singletonList(testDir), mockUpdater, pollingInterval);
        monitor.start();
        Thread.sleep(60); // wait for the monitor to get ready
        return monitor;
    }

    @Test
    public void should_ignore_properties_in_root() throws Exception {
        var mockUpdater = Mockito.mock(DepositStatusUpdater.class);
        var monitor = startMonitor(mockUpdater, 50);

        createDirectories(testDir);
        var propertiesFile = Files.createFile(testDir.resolve("deposit.properties"));
        Thread.sleep(70);// Wait for the monitor to pick up the new file

        Mockito.verify(mockUpdater, Mockito.times(0))
            .onDepositCreate(propertiesFile.toFile());
        Mockito.verifyNoMoreInteractions(mockUpdater);

        monitor.stop();
    }

    @Test
    public void should_pick_up_new_properties_in_bag() throws Exception {
        var mockUpdater = Mockito.mock(DepositStatusUpdater.class);
        var monitor = startMonitor(mockUpdater, 50);

        var propertiesFile = testDir.resolve("bag/deposit.properties");
        createDirectories(propertiesFile.getParent());
        Files.createFile(propertiesFile);
        Thread.sleep(70);// Wait for the monitor to pick up the new file

        Mockito.verify(mockUpdater, Mockito.times(1)).onDepositCreate(propertiesFile.toFile());
        Mockito.verifyNoMoreInteractions(mockUpdater);

        monitor.stop();
    }

    @Test
    public void should_ignore_properties_in_bag_content() throws Exception {
        var mockUpdater = Mockito.mock(DepositStatusUpdater.class);
        var monitor = startMonitor(mockUpdater, 50);

        var propertiesFile = testDir.resolve("bag/content/deposit.properties");
        createDirectories(propertiesFile.getParent());
        Files.createFile(propertiesFile);
        Thread.sleep(70);// Wait for the monitor to pick up the new file

        Mockito.verify(mockUpdater, Mockito.times(0)).onDepositCreate(propertiesFile.toFile());
        Mockito.verifyNoMoreInteractions(mockUpdater);

        monitor.stop();
    }


    @Test
    public void should_pick_up_deleted_bag() throws Exception {
        var mockUpdater = Mockito.mock(DepositStatusUpdater.class);
        var monitor = startMonitor(mockUpdater, 50);

        var propertiesFile = testDir.resolve("bag/deposit.properties");
        createDirectories(propertiesFile.getParent());
        Files.createFile(propertiesFile);
        Thread.sleep(70);
        FileUtils.deleteDirectory(propertiesFile.getParent().toFile());
        Thread.sleep(70);

        Mockito.verify(mockUpdater, Mockito.times(1)).onDepositDelete(propertiesFile.toFile());

        monitor.stop();
    }

    @Test
    public void should_pick_up_changed_properties() throws Exception {
        var mockUpdater = Mockito.mock(DepositStatusUpdater.class);
        var monitor = startMonitor(mockUpdater, 20);

        var propertiesFile = testDir.resolve("bag/deposit.properties");
        createDirectories(propertiesFile.getParent());
        Files.createFile(propertiesFile);
        Thread.sleep(30);
        Files.writeString(propertiesFile, "just some garbage");
        Thread.sleep(30);

        Mockito.verify(mockUpdater, Mockito.times(1)).onDepositChange(propertiesFile.toFile());

        monitor.stop();
    }

    @Test
    public void should_pick_up_deleted_root() throws Exception {
        var mockUpdater = Mockito.mock(DepositStatusUpdater.class);
        var monitor = startMonitor(mockUpdater, 20);

        var propertiesFile = testDir.resolve("bag/deposit.properties");
        createDirectories(propertiesFile.getParent());
        Files.createFile(propertiesFile);
        Thread.sleep(30);
        FileUtils.deleteDirectory(testDir.toFile());
        Thread.sleep(30);

        Mockito.verify(mockUpdater, Mockito.times(1)).onDepositCreate(propertiesFile.toFile());
        Mockito.verifyNoMoreInteractions(mockUpdater);
        assumeNotYetFixed("The monitor should pick up the deletion of the root folder, it might imply deletion of many bags");
        monitor.stop();
    }

    @Test
    public void should_throw_when_stopping_a_stopped_monitor() throws Exception {
        var mockUpdater = Mockito.mock(DepositStatusUpdater.class);
        var monitor = startMonitor(mockUpdater, 20);
        monitor.stop();

        assertThatThrownBy(monitor::stop)
            .isInstanceOf(RuntimeException.class)
            .hasRootCauseInstanceOf(IllegalStateException.class);
    }
}