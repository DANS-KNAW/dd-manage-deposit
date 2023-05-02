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
package nl.knaw.dans.managedeposit.core;

import nl.knaw.dans.managedeposit.core.service.Limiter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "deposit_properties")
@NamedQuery(
    name = "showAll",
    query = "SELECT dp FROM DepositProperties dp"
)
public class DepositProperties {
    @Id
    @Column(name = "deposit_id", nullable = false)                         // deposit directory name
    private String depositId;

    @Column(name = "depositor", nullable = false)                          // depositor.userId
    private String depositor;

    @Column(name = "bag_name", nullable = false)                           // Bag directory name
    private String bagName;

    @Column(name = "deposit_state")                                        // state.label
    private String depositState;

    @Column(name = "location", length = Limiter.maxDirectoryLength)        // full parent-path on disk
    private String location;

    @Column(name = "deposit_creation_timestamp")                           // creation.timestamp
    private OffsetDateTime depositCreationTimestamp;

    @Column(name = "deposit_update_timestamp")                             // modified timestamp of deposit.properties
    private OffsetDateTime depositUpdateTimestamp;

    @Column(name = "description", length = Limiter.maxDescriptionLength)   // state.description
    private String description;

    @Column(name = "storage_in_bytes")                                     // Total storage of deposit directory
    private long storageInBytes;

    @Column(name = "deleted")                                              // deposit is deleted from inbox - archived
    private boolean deleted;
    public String getDepositId() {
        return depositId;
    }

    public DepositProperties() {
    }

    public DepositProperties(String depositId, String depositor, String bagName, String depositState,
        String description, OffsetDateTime depositCreationTimestamp, String location, long storageInBytes) {
        this.depositId = depositId;
        this.depositor = depositor;
        this.bagName = bagName;
        this.depositState = depositState;
        this.description = description;
        this.depositCreationTimestamp = depositCreationTimestamp;
        this.location = location;
        this.storageInBytes = storageInBytes;
    }

    public String getDepositor() {
        return depositor;
    }

    public void setDepositor(String depositor) {
        this.depositor = depositor;
    }

    public String getBagName() {
        return bagName;
    }

    public void setBagName(String bagName) {
        this.bagName = bagName;
    }

    public String getDepositState() {
        return depositState;
    }

    public void setDepositState(String depositState) {
        this.depositState = depositState;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public OffsetDateTime getDepositCreationTimestamp() {
        return depositCreationTimestamp;
    }

    public void setDepositCreationTimestamp(OffsetDateTime depositCreationTimestamp) {
        this.depositCreationTimestamp = depositCreationTimestamp;
    }

    public OffsetDateTime getDepositUpdateTimestamp() {
        return depositUpdateTimestamp;
    }

    public void setDepositUpdateTimestamp(OffsetDateTime depositUpdateTimestamp) {
        this.depositUpdateTimestamp = depositUpdateTimestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean setDeleted(boolean deleted) {
        return this.deleted = deleted;
    }

    public long getStorageInBytes() {
        return storageInBytes;
    }

    public void setStorageInBytes(long storageInBytes) {
        this.storageInBytes = storageInBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DepositProperties that = (DepositProperties) o;

        if (!depositId.equals(that.depositId))
            return false;
        return depositCreationTimestamp.equals(that.depositCreationTimestamp);
    }

    @Override
    public int hashCode() {
        return 31 * depositId.hashCode() + depositCreationTimestamp.hashCode();
    }
}