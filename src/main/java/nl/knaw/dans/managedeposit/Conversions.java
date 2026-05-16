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

import nl.knaw.dans.managedeposit.api.DepositPropertiesDto;
import nl.knaw.dans.managedeposit.core.DepositProperties;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface Conversions {

    @Mapping(target = "depositState", source = "depositState")
    DepositPropertiesDto toDto(DepositProperties depositProperties);

    @Mapping(target = "depositState", source = "depositState")
    DepositProperties toEntity(DepositPropertiesDto depositPropertiesDto);

    default DepositPropertiesDto.DepositStateEnum toDtoState(String state) {
        if (state == null) return null;
        return DepositPropertiesDto.DepositStateEnum.fromValue(state);
    }

    default String toEntityState(DepositPropertiesDto.DepositStateEnum state) {
        if (state == null) return null;
        return state.toString();
    }
}
