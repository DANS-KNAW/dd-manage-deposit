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
import org.apache.commons.io.filefilter.HiddenFileFilter;
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
    final long POLLING_INTERVAL = 3 * 1000;
    private final List<Path> toMonitorPaths;
    private final DepositStatusUpdater depositStatusUpdater;
    private FileAlterationMonitor monitor;

    public IngestPathMonitor(List<Path> depositBoxesPaths, DepositStatusUpdater depositStatusUpdater) {
        this.toMonitorPaths = new ArrayList<>(depositBoxesPaths);
//        this.monitorPath = Path.of(path);
        this.depositStatusUpdater = depositStatusUpdater;
    }

    private void startMonitor() throws Exception {
        IOFileFilter directories = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), HiddenFileFilter.VISIBLE);
        IOFileFilter files = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.nameFileFilter("deposit.properties", IOCase.INSENSITIVE));
        IOFileFilter filter = FileFilterUtils.or(directories, files);

        FileAlterationObserver observer = new FileAlterationObserver(toMonitorPaths.get(0).toFile(), filter);

        observer.addListener(this);

        monitor = new FileAlterationMonitor(this.POLLING_INTERVAL, observer);
        log.info("Starting IngestPathMonitor, file filter: deposit.properties");
        monitor.start();
    }

    @Override
    public void start() throws Exception {
        try {
            startMonitor();
        }
        catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new InvalidTransferItemException(e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopping IngestPathMonitor");
        monitor.stop();
    }

    @Override
    public void onFileCreate(File file) {
        log.debug("onFileCreate: '{}'", file.getName());
        depositStatusUpdater.onCreateDeposit(file.toPath());
    }

    @Override
    public void onFileDelete(File file) {
        log.debug("onFileDelete: '{}'", file.getName());
        depositStatusUpdater.onDeleteDeposit(file.toPath());
    }

    @Override
    public void onFileChange(File file) {
        log.debug("onFileChange: '{}'", file.getName());
        depositStatusUpdater.onChangeDeposit(file.toPath());
    }

}
