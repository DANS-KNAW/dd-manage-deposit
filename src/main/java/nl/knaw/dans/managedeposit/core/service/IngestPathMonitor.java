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
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IngestPathMonitor extends FileAlterationListenerAdaptor implements Managed {
    final long POLLING_INTERVAL = 3 * 1000;

    private static final Logger log = LoggerFactory.getLogger(IngestPathMonitor.class);
    private final Path monitorPath;
    private FileAlterationMonitor monitor;

    public IngestPathMonitor(String path) {
            this.monitorPath = Path.of(path);
    }


    private void startMonitor() throws Exception {
//        IOFileFilter fileFilter = FileFilterUtils.nameFileFilter("deposit.properties", IOCase.INSENSITIVE);

//        FileAlterationObserver observer = new FileAlterationObserver(monitorPath.toFile(), fileFilter);
        FileAlterationObserver observer = new FileAlterationObserver(monitorPath.toFile());
        observer.addListener(this);

        monitor = new FileAlterationMonitor(this.POLLING_INTERVAL, observer);
        monitor.start();
    }

    @Override
    public void start() throws Exception {
        try {
            // initial scan
//            log.info("Scanning path '{}' for first run", this.monitorPath);
            scanExistingFiles();

//            log.info("Starting file alteration monitor for path '{}'", this.monitorPath);
            startMonitor();
        }
        catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new InvalidTransferItemException(e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        monitor.stop();
    }

    @Override
    public void onFileCreate(File file) {
        Path expected = Path.of(this.monitorPath.toString(), file.getName());
//        log.debug("Comparing directories: '{}' vs '{}'", file.toPath(), expected);
        System.out.format("onFileCreate:  file.getName(): %s - file.getParent(): %s\n", file.getName(), file.getParent());
        if (!file.toPath().equals(expected)) {
//            log.warn("File found in non-root directory, ignoring");
            System.out.format("onFileCreate: file.getName(): %s - monitorPath %s - file.toPath(): %S\n", file.getName(), this.monitorPath.toString(), file.getName(), file.toPath().toString());
        }

        //  ALI_lookat-->      this.callback.onFileCreate(file, datastationName);
    }

    @Override
    public void onFileDelete (File file) {
        Path expected = Path.of(this.monitorPath.toString(), file.getName());
        //        log.debug("Comparing directories: '{}' vs '{}'", file.toPath(), expected);
        System.out.format("onFileDelete:  file.getName(): %s - file.getParent(): %s\n", file.getName(), file.getParent());
        if (!file.toPath().equals(expected)) {
            //            log.warn("File found in non-root directory, ignoring");
            System.out.format("onFileDelete: file.getName(): %s - monitorPath %s - file.toPath(): %S\n", file.getName(), this.monitorPath.toString(), file.getName(), file.toPath().toString());
        }

        //  ALI_lookat-->      this.callback.onFileCreate(file, datastationName);
    }

    @Override
    public void onFileChange(File file) {
        Path expected = Path.of(this.monitorPath.toString(), file.getName());
        //        log.debug("Comparing directories: '{}' vs '{}'", file.toPath(), expected);
        System.out.format("onFileChange:  file.getName(): %s - file.getParent(): %s\n", file.getName(), file.getParent());
        if (!file.toPath().equals(expected)) {
            //            log.warn("File found in non-root directory, ignoring");
            System.out.format("onFileChange: file.getName(): %s - monitorPath %s - file.toPath(): %S\n", file.getName(), this.monitorPath.toString(), file.getName(), file.toPath().toString());
        }

        //  ALI_lookat-->      this.callback.onFileCreate(file, datastationName);
    }

    private void scanExistingFiles() throws IOException {
        Files.list(this.monitorPath).forEach(f -> onFileCreate(f.toFile()));
    }


}
