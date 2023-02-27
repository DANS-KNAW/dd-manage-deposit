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

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Watcher {
    WatchService watchService;
    Path path;

    public Watcher(String watchFolder)  {
        this.path = Paths.get(watchFolder);
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            registerAll(this.path);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void register(Path dir) throws IOException {
        dir.register (watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY);
    }

    void registerAll(Path parent_path) throws IOException {
        Files.walkFileTree(parent_path, new SimpleFileVisitor<Path>() {
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void startWatcher() {
        WatchKey key;
        while (true) {
            try {
                key = watchService.take();
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent<Path> pathEvent = (WatchEvent<Path>)event;

                Path filename = pathEvent.context();
                Path changedFile = path.resolve(filename);

                System.out.format("Event kind: %s - changedFile %s\n", pathEvent.kind().name(), changedFile);
            }
            key.reset();
        }
    }

}
