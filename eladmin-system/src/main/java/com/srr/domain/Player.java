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
package com.srr.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.srr.enumeration.Gender;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
* @description
* @author Chanheng
* @date 2025-05-18
**/
@Entity
@Getter
@Setter
@Table(name="player")
public class Player implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id", hidden = true)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "Name")
    private String name;

    @Column(name = "description")
    @ApiModelProperty(value = "Description")
    private String description;

    @Column(name = "latitude")
    @ApiModelProperty(value = "Latitude")
    private Double latitude;

    @Column(name = "longitude")
    @ApiModelProperty(value = "Longitude")
    private Double longitude;

    @Column(name = "profile_image")
    @ApiModelProperty(value = "Image")
    private String profileImage;

    @Column(name = "create_time")
    @CreationTimestamp
    @ApiModelProperty(value = "Creation time", hidden = true)
    private Timestamp createTime;

    @Column(name = "update_time")
    @UpdateTimestamp
    @ApiModelProperty(value = "Update time", hidden = true)
    private Timestamp updateTime;

    @Column(name = "user_id", nullable = false)
    @NotNull
    @ApiModelProperty(value = "userId")
    private Long userId;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "Gender")
    private Gender gender;

    @Column(name = "date_of_birth")
    @ApiModelProperty(value = "Date of Birth")
    private LocalDate dateOfBirth;

    public void copy(Player source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
