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
package me.zhengjie.modules.maint.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.maint.domain.ServerDeploy;
import me.zhengjie.modules.maint.service.ServerDeployService;
import me.zhengjie.modules.maint.service.dto.ServerDeployDto;
import me.zhengjie.modules.maint.service.dto.ServerDeployQueryCriteria;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
* @author zhanghouying
* @date 2019-08-24
*/
@RestController
@Api(tags = "Ops: Server Management")
@RequiredArgsConstructor
@RequestMapping("/api/serverDeploy")
public class ServerDeployController {

    private final ServerDeployService serverDeployService;

    @ApiOperation("Export server data")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('serverDeploy:list')")
    public void exportServerDeploy(HttpServletResponse response, ServerDeployQueryCriteria criteria) throws IOException {
        serverDeployService.download(serverDeployService.queryAll(criteria), response);
    }

    @ApiOperation(value = "Query server")
    @GetMapping
    @PreAuthorize("@el.check('serverDeploy:list')")
    public ResponseEntity<PageResult<ServerDeployDto>> queryServerDeploy(ServerDeployQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(serverDeployService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("Add server")
    @ApiOperation(value = "Add server")
    @PostMapping
    @PreAuthorize("@el.check('serverDeploy:add')")
    public ResponseEntity<Object> createServerDeploy(@Validated @RequestBody ServerDeploy resources){
        serverDeployService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("Update server")
    @ApiOperation(value = "Update server")
    @PutMapping
    @PreAuthorize("@el.check('serverDeploy:edit')")
    public ResponseEntity<Object> updateServerDeploy(@Validated @RequestBody ServerDeploy resources){
        serverDeployService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Delete server")
    @ApiOperation(value = "Delete Server")
    @DeleteMapping
    @PreAuthorize("@el.check('serverDeploy:del')")
    public ResponseEntity<Object> deleteServerDeploy(@RequestBody Set<Long> ids){
        serverDeployService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("Test server connection")
    @ApiOperation(value = "Test server connection")
    @PostMapping("/testConnect")
    @PreAuthorize("@el.check('serverDeploy:add')")
    public ResponseEntity<Object> testConnectServerDeploy(@Validated @RequestBody ServerDeploy resources){
        return new ResponseEntity<>(serverDeployService.testConnect(resources),HttpStatus.CREATED);
    }
}
