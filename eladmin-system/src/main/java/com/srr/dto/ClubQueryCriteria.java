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
package com.srr.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.zhengjie.annotation.Query;

/**
* @website https://eladmin.vip
* @author Chanheng
* @date 2025-05-18
**/
@Data
public class ClubQueryCriteria{

    /** 精确 */
    @Query
    @ApiModelProperty(value = "id")
    private Long id;

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    @ApiModelProperty(value = "名称")
    private String name;

    /** 精确 */
    @Query
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;
}