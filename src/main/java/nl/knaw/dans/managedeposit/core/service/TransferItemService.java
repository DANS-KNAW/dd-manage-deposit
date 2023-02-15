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


import nl.knaw.dans.managedeposit.core.DepositProperties;

import java.nio.file.Path;

public interface TransferItemService {

    DepositProperties createDepositProperties(String datastationName, FilenameAttributes filenameAttributes, FilesystemAttributes filesystemAttributes, FileContentAttributes fileContentAttributes)
        throws InvalidTransferItemException;

    DepositProperties createDepositProperties(String datastationName, FilenameAttributes filenameAttributes, FilesystemAttributes filesystemAttributes)
        throws InvalidTransferItemException;

    DepositProperties moveDepositProperties(DepositProperties depositProperties, /*DepositProperties.TransferStatus newStatus,*/ Path newPath);

    void setArchiveAttemptFailed(String id, boolean increaseAttemptCount, int maxRetries);



}
