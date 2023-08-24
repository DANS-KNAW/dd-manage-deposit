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

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import nl.knaw.dans.managedeposit.core.service.TextTruncation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DdManageDepositConfiguration extends Configuration {
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    private long pollingInterval;

    private List<Path> depositBoxes = new ArrayList<>();

    public List<Path> getDepositBoxes() {
        return depositBoxes;
    }

    public void setDepositBoxes(List<Path> depositBoxes) {
        this.depositBoxes = depositBoxes;
    }

    public DataSourceFactory getDepositPropertiesDatabase() {
        return database;
    }

    public void setDepositPropertiesDatabase(DataSourceFactory database) {
        this.database = database;
    }

    public long getPollingInterval() {
        return pollingInterval > 0 ? pollingInterval : TextTruncation.pollingInterval;
    }

    public void setPollingInterval(long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }
}
