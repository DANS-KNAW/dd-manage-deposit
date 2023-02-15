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
import nl.knaw.dans.managedeposit.db.DepositPropertiesDAO;

import java.nio.file.Path;

public class TransferItemServiceImpl implements TransferItemService {
    private final DepositPropertiesDAO depositPropertiesDAO;

    public TransferItemServiceImpl(DepositPropertiesDAO depositPropertiesDAO) {
        this.depositPropertiesDAO = depositPropertiesDAO;
    }

    @Override
    public DepositProperties createDepositProperties(String datastationName, FilenameAttributes filenameAttributes, FilesystemAttributes filesystemAttributes,
        FileContentAttributes fileContentAttributes) throws InvalidTransferItemException {
        return null;
    }

    @Override
    public DepositProperties createDepositProperties(String datastationName, FilenameAttributes filenameAttributes, FilesystemAttributes filesystemAttributes) throws InvalidTransferItemException {
        return null;
    }

    @Override
    public DepositProperties moveDepositProperties(DepositProperties depositProperties, Path newPath) {
        return null;
    }

    @Override
    public void setArchiveAttemptFailed(String id, boolean increaseAttemptCount, int maxRetries) {

    }

    //    @Override
//    @UnitOfWork
//    public DepositProperties createTransferItem(String datastationName, FilenameAttributes filenameAttributes, FilesystemAttributes filesystemAttributes,
//        FileContentAttributes fileContentAttributes)
//        throws InvalidTransferItemException {
//        var depositProperties = new DepositProperties();
//
//        depositProperties.setTransferStatus(depositProperties.TransferStatus.COLLECTED);
//        depositProperties.setQueueDate(LocalDateTime.now());
//        depositProperties.setDatasetDvInstance(datastationName);
//
//        // filename attributes
//        depositProperties.setDveFilePath(filenameAttributes.getDveFilePath());
//        depositProperties.setDatasetPid(filenameAttributes.getDatasetPid());
//        depositProperties.setVersionMajor(filenameAttributes.getVersionMajor());
//        depositProperties.setVersionMinor(filenameAttributes.getVersionMinor());
//
//        // filesystem attributes
//        depositProperties.setCreationTime(filesystemAttributes.getCreationTime());
//        depositProperties.setBagSize(filesystemAttributes.getBagSize());
//
//        // file content attributes
//        depositProperties.setDatasetVersion(fileContentAttributes.getDatasetVersion());
//        depositProperties.setBagId(fileContentAttributes.getBagId());
//        depositProperties.setNbn(fileContentAttributes.getNbn());
//        depositProperties.setOaiOre(fileContentAttributes.getOaiOre());
//        depositProperties.setPidMapping(fileContentAttributes.getPidMapping());
//        depositProperties.setBagChecksum(fileContentAttributes.getBagChecksum());
//
//        // check if an item with this ID already exists
//        var existing = depositPropertiesDAO.findByDatasetPidAndVersion(depositProperties.getDatasetPid(), depositProperties.getVersionMajor(), depositProperties.getVersionMinor());
//
//        if (existing.isPresent()) {
//            throw new InvaliddepositPropertiesException(
//                String.format("depositProperties with datasetPid=%s, versionMajor=%s, versionMinor=%s already exists in database", depositProperties.getDatasetPid(), depositProperties.getVersionMajor(),
//                    depositProperties.getVersionMinor()));
//
//        }
//
//        depositPropertiesDAO.save(depositProperties);
//
//        return depositProperties;
//    }
//
//    @Override
//    @UnitOfWork
//    public depositProperties createdepositProperties(String datastationName, FilenameAttributes filenameAttributes, FilesystemAttributes filesystemAttributes)
//        throws InvalidTransferItemException {
//
//        // check if an item with this ID already exists
//        var existing = depositPropertiesDAO.findByDatasetPidAndVersion(
//            filenameAttributes.getDatasetPid(),
//            filenameAttributes.getVersionMajor(),
//            filenameAttributes.getVersionMinor()
//        );
//
//        if (existing.isPresent()) {
//            throw new InvalidTransferItemException(
//                String.format("depositProperties with datasetPid=%s, versionMajor=%s, versionMinor=%s already exists in database",
//                    filenameAttributes.getDatasetPid(),
//                    filenameAttributes.getVersionMajor(),
//                    filenameAttributes.getVersionMinor()
//                )
//            );
//        }
//
//        var depositProperties = new DepositProperties();
//
//        depositProperties.setTransferStatus(depositProperties.TransferStatus.COLLECTED);
//        depositProperties.setQueueDate(LocalDateTime.now());
//        depositProperties.setDatasetDvInstance(datastationName);
//        depositProperties.setBagDepositDate(LocalDateTime.now());
//
//        // filename attributes
//        depositProperties.setDveFilePath(filenameAttributes.getDveFilePath());
//        depositProperties.setDatasetPid(filenameAttributes.getDatasetPid());
//        depositProperties.setVersionMajor(filenameAttributes.getVersionMajor());
//        depositProperties.setVersionMinor(filenameAttributes.getVersionMinor());
//
//        // filesystem attributes
//        depositProperties.setCreationTime(filesystemAttributes.getCreationTime());
//        depositProperties.setBagSize(filesystemAttributes.getBagSize());
//
//        depositPropertiesDAO.save(depositProperties);
//        return depositProperties;
//    }
//
//    @Override
//    @UnitOfWork
//    public DepositProperties movedepositProperties(DepositProperties depositProperties, depositProperties.TransferStatus newStatus, Path newPath) {
//        depositProperties.setDveFilePath(newPath.toString());
//        depositProperties.setTransferStatus(newStatus);
//        depositPropertiesDAO.merge(depositProperties);
//        return depositProperties;
//    }
//
//    @UnitOfWork
//    public void saveAlldepositProperties(List<DepositProperties> depositPropertiess) {
//        for (var depositProperties : depositProperties) {
//            depositPropertiesDAO.merge(depositProperties);
//        }
//    }
//
//
//    @Override
//    @UnitOfWork
//    public Optional<DepositProperties> getdepositPropertiesByFilenameAttributes(FilenameAttributes filenameAttributes) {
//        return depositPropertiesDAO.findByDatasetPidAndVersion(
//            filenameAttributes.getDatasetPid(),
//            filenameAttributes.getVersionMajor(),
//            filenameAttributes.getVersionMinor()
//        );
//    }
//
//    @Override
//    public DepositProperties addMetadata(DepositProperties depositProperties, FileContentAttributes fileContentAttributes) {
//
//        Objects.requireNonNull(depositProperties, "depositProperties cannot be null");
//        Objects.requireNonNull(fileContentAttributes, "fileContentAttributes cannot be null");
//
//        // file content attributes
//        depositProperties.setDatasetVersion(fileContentAttributes.getDatasetVersion());
//        depositProperties.setBagId(fileContentAttributes.getBagId());
//        depositProperties.setNbn(fileContentAttributes.getNbn());
//        depositProperties.setOaiOre(fileContentAttributes.getOaiOre());
//        depositProperties.setPidMapping(fileContentAttributes.getPidMapping());
//        depositProperties.setBagChecksum(fileContentAttributes.getBagChecksum());
//        depositProperties.setOtherId(fileContentAttributes.getOtherId());
//        depositProperties.setOtherIdVersion(fileContentAttributes.getOtherIdVersion());
//        depositProperties.setSwordToken(fileContentAttributes.getSwordToken());
//        depositProperties.setSwordClient(fileContentAttributes.getSwordClient());
//
//        return depositProperties;
//    }

}
