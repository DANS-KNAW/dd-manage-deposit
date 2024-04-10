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

import io.dropwizard.lifecycle.Managed;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class IngestPathMonitor extends FileAlterationListenerAdaptor implements Managed {
    private static final Logger log = LoggerFactory.getLogger(IngestPathMonitor.class);
    private final long pollingInterval;
    private final List<Path> toMonitorPaths;
    private final List<FileAlterationMonitor> fileAlterationMonitors;
    private final DepositStatusUpdater depositStatusUpdater;

    public IngestPathMonitor(List<Path> depositBoxesPaths, DepositStatusUpdater depositStatusUpdater, long pollingInterval) {
        this.toMonitorPaths = new ArrayList<>(depositBoxesPaths);
        this.depositStatusUpdater = depositStatusUpdater;
        this.fileAlterationMonitors = new ArrayList<>();
        this.pollingInterval = pollingInterval;
    }

    private void startMonitors() throws Exception {
        log.info("Starting 'IngestPathMonitor', with file filter: deposit.properties");

        var observers = new ArrayList<>();
        for (Path folder : toMonitorPaths) {
            IOFileFilter directories = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(),/*HiddenFileFilter.VISIBLE*/new DepthFileFilter(folder, 1));
            IOFileFilter files = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.nameFileFilter("deposit.properties", IOCase.INSENSITIVE));
            IOFileFilter filter = FileFilterUtils.or(directories, files);

            FileAlterationObserver observer = new FileAlterationObserver(folder.toFile(), filter);
            observer.addListener(this);
            observers.add(observer);

        }

        FileAlterationMonitor monitor = new FileAlterationMonitor(this.pollingInterval, observers.toArray(new FileAlterationObserver[0]));
        fileAlterationMonitors.add(monitor);
        log.debug("'IngestPathMonitor' is going to monitor the folders\n '{}'", toMonitorPaths);
        monitor.start();
    }

    @Override
    public void start() throws Exception {
        try {
            startMonitors();
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new InvalidTransferItemException(e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopping IngestPathMonitor");

        fileAlterationMonitors.forEach(monitor -> {
            try {
                monitor.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void onFileCreate(File file) {
        log.debug("onFileCreate: '{}'", file.getAbsolutePath());
        depositStatusUpdater.onDepositCreate(file);
    }

    @Override
    public void onFileDelete(File file) {
        log.debug("onFileDelete: '{}'", file.getAbsolutePath());
        depositStatusUpdater.onDepositDelete(file);
    }

    @Override
    public void onFileChange(File file) {
        log.debug("onFileChange: '{}'", file.getAbsolutePath());
        depositStatusUpdater.onDepositChange(file);
    }

}
