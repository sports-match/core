/*
 *  Copyright 2019-2025 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.zhengjie.annotation.Query;
import java.sql.Timestamp;
import java.util.List;

/**
 * Log query class
 * @author Zheng Jie
 * @date 2019-6-4 09:23:07
 */
@Data
public class SysLogQueryCriteria {

    @ApiModelProperty(value = "Fuzzy search")
    @Query(blurry = "username,description,address,requestIp,method,params")
    private String blurry;

    @Query
    @ApiModelProperty(value = "Username")
    private String username;

    @Query
    @ApiModelProperty(value = "Log type")
    private String logType;

    @ApiModelProperty(value = "Creation time")
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
