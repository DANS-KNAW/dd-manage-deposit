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

import org.apache.commons.io.filefilter.AbstractFileFilter;

import java.io.File;
import java.nio.file.Path;

public class DepthFileFilter extends AbstractFileFilter {
    private final Path baseFolder;
    private final int depthLimit;

    public DepthFileFilter(Path baserFolder, int depthLimit) {
        this.baseFolder = baserFolder;
        this.depthLimit = depthLimit;
    }

    private boolean confirmParent(File file) {
        Path parent = file.toPath();
        for (int depth = 0; depth < this.depthLimit; depth++)
            parent = parent.getParent();

        return parent.endsWith(this.baseFolder);
    }

    @Override
    public boolean accept(File file) {
        return confirmParent(file);
    }

    @Override
    public boolean accept(File dir, String name) {
        return confirmParent(dir);
    }
}
