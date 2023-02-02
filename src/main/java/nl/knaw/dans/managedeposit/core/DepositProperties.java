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

import org.hibernate.SessionFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "deposit_properties")
@NamedQuery(
    name = "nl.knaw.dans.managedeposit.core.DepositProperties.findAll",
    query = "SELECT dp FROM DepositProperties dp"
)
public class DepositProperties {
    @Id
    @Column(name = "deposit_id", nullable = false)
    private String depositId;

    @Column(name = "user_name")
    private String userName;

    @Column(name="on_disk")
    private boolean onDisk;

    @Column(name="deleted")
    private boolean deleted;
    @Column(name="created_date", nullable = false)
    private OffsetDateTime createdDate;

    @Column(name = "state")
    public String getDepositId() {
        return depositId;
    }

    public void setDepositId(String depositId) {
        this.depositId = depositId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isOnDisk() {
        return onDisk;
    }

    public void setOnDisk(boolean onDisk) {
        this.onDisk = onDisk;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public DepositProperties() {}

    public DepositProperties(String depositId) {
        this.depositId = depositId;

        // get the current UTC timestamp or creation.timestamp from
        this.createdDate = OffsetDateTime.now();
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
        return createdDate.equals(that.createdDate);
    }

    @Override
    public int hashCode() {
        int result = depositId.hashCode();
        result = 31 * result + createdDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DepositProperties{" +
            "depositId='" + depositId + '\'' +
            ", userName='" + userName + '\'' +
            ", onDisk=" + onDisk +
            ", deleted=" + deleted +
            ", createdDate=" + createdDate +
            '}';
    }
}