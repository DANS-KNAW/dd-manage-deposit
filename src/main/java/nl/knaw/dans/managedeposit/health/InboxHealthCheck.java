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
package nl.knaw.dans.managedeposit.health;

import com.codahale.metrics.health.HealthCheck;
import nl.knaw.dans.managedeposit.DdManageDepositConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Files;
import java.nio.file.Path;

public class InboxHealthCheck extends HealthCheck {
    private static final Logger log = LoggerFactory.getLogger(InboxHealthCheck.class);

    private final DdManageDepositConfiguration configuration;

    public InboxHealthCheck(DdManageDepositConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected Result check() throws Exception {
        var valid = true;

        for (String folder: configuration.getDepositBoxes()) {
            var exists = Files.exists(Path.of(folder));
            var canRead = Files.isReadable(Path.of(folder));

            if (exists && canRead) {
                log.debug("Inbox path '{}' exists and is readable", folder);
            }
            else {
                valid = false;

                if (!exists) {
                    log.debug("Inbox path '{}' does not exist", folder);
                }
                else {
                    log.debug("Inbox path '{}' is not readable", folder);
                }
            }
        }

        if (valid) {
            return Result.healthy();
        }
        else {
            return Result.unhealthy("InboxPaths are not accessible");
        }
    }
}
