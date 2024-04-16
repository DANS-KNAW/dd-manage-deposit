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
package nl.knaw.dans.managedeposit;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class AbstractTestWithTestDir {
    protected final Path testDir = Path.of("target/test")
        .resolve(getClass().getSimpleName());

    @BeforeEach
    public void setUp() throws Exception {
        FileUtils.deleteDirectory(testDir.toFile());
    }

    /**
     * Assume that a bug is not yet fixed. This allows to skip assertions while still showing the code covered by the test.
     *
     * @param message the message to display
     */
    public void assumeNotYetFixed (String message) {
        assumeTrue(false, message);
    }
}
