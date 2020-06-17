package io.choerodon.kb.api.controller.v1;

import io.choerodon.kb.infra.constants.EncryptConstants;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.kb.api.vo.*;
import io.choerodon.kb.app.service.WorkSpaceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.starter.keyencrypt.mvc.EncryptDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Created by Zenger on 2019/4/30.
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/work_space")
public class WorkSpaceOrganizationController {

    private WorkSpaceService workSpaceService;

    public WorkSpaceOrganizationController(WorkSpaceService workSpaceService) {
        this.workSpaceService = workSpaceService;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织下创建页面和空页面")
    @PostMapping
    public ResponseEntity<WorkSpaceInfoVO> createWorkSpaceAndPage(
            @ApiParam(value = "组织id", required = true)
            @PathVariable(value = "organization_id") Long organizationId,
            @ApiParam(value = "页面信息", required = true)
            @RequestBody @Valid @EncryptDTO PageCreateWithoutContentVO pageCreateVO) {
        return new ResponseEntity<>(workSpaceService.createWorkSpaceAndPage(organizationId, null, pageCreateVO), HttpStatus.CREATED);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "查询组织下工作空间节点页面")
    @GetMapping(value = "/{id}")
    public ResponseEntity<WorkSpaceInfoVO> query(
            @ApiParam(value = "组织id", required = true)
            @PathVariable(value = "organization_id") Long organizationId,
            @ApiParam(value = "工作空间目录id", required = true)
            @PathVariable @Encrypt(EncryptConstants.TN_KB_WORKSPACE) Long id,
            @ApiParam(value = "应用于全文检索时，对单篇文章，根据检索内容高亮内容")
            @RequestParam(required = false) String searchStr) {
        //组织层设置成permissionLogin=true，因此需要单独校验权限
        workSpaceService.checkOrganizationPermission(organizationId);
        return new ResponseEntity<>(workSpaceService.queryWorkSpaceInfo(organizationId, null, id, searchStr), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "更新组织下工作空间节点页面")
    @PutMapping(value = "/{id}")
    public ResponseEntity<WorkSpaceInfoVO> update(@ApiParam(value = "组织id", required = true)
                                                  @PathVariable(value = "organization_id") Long organizationId,
                                                  @ApiParam(value = "工作空间目录id", required = true)
                                                  @PathVariable @Encrypt(EncryptConstants.TN_KB_WORKSPACE) Long id,
                                                  @ApiParam(value = "应用于全文检索时，对单篇文章，根据检索内容高亮内容")
                                                  @RequestParam(required = false) String searchStr,
                                                  @ApiParam(value = "空间信息", required = true)
                                                  @RequestBody @Valid PageUpdateVO pageUpdateVO) {
        return new ResponseEntity<>(workSpaceService.updateWorkSpaceAndPage(organizationId, null, id, searchStr, pageUpdateVO), HttpStatus.CREATED);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "移动文章")
    @PostMapping(value = "/to_move/{id}")
    public ResponseEntity moveWorkSpace(@ApiParam(value = "组织id", required = true)
                                        @PathVariable(value = "organization_id") Long organizationId,
                                        @ApiParam(value = "工作空间目录id", required = true)
                                        @PathVariable @Encrypt(EncryptConstants.TN_KB_WORKSPACE) Long id,
                                        @ApiParam(value = "移动信息", required = true)
                                        @RequestBody @Valid @EncryptDTO MoveWorkSpaceVO moveWorkSpaceVO) {
        workSpaceService.moveWorkSpace(organizationId, null, id, moveWorkSpaceVO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "查询空间树形结构")
    @GetMapping(value = "/all_tree")
    public ResponseEntity<Map<String, Object>> queryAllTreeList(@ApiParam(value = "组织id", required = true)
                                                                             @PathVariable(value = "organization_id") Long organizationId,
                                                                             @ApiParam(value = "知识库的id")
                                                                             @RequestParam(required = false) @Encrypt(EncryptConstants.TN_KB_KNOWLEDGE_BASE) Long baseId,
                                                                             @ApiParam(value = "展开的空间id")
                                                                             @RequestParam(required = false) @Encrypt(EncryptConstants.TN_KB_WORKSPACE) Long expandWorkSpaceId) {
        //组织层设置成permissionLogin=true，因此需要单独校验权限
        workSpaceService.checkOrganizationPermission(organizationId);
        return new ResponseEntity<>(workSpaceService.queryAllTreeList(organizationId, null, expandWorkSpaceId,baseId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询组织下的所有空间")
    @GetMapping
    public ResponseEntity<List<WorkSpaceVO>> queryAllSpaceByOptions(@ApiParam(value = "组织id", required = true)
                                                                    @PathVariable(value = "organization_id") Long organizationId,
                                                                    @RequestParam @Encrypt(EncryptConstants.TN_KB_KNOWLEDGE_BASE) Long baseId) {
        return new ResponseEntity<>(workSpaceService.queryAllSpaceByOptions(organizationId, null,baseId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "移除组织下工作空间及页面（管理员权限）")
    @PutMapping(value = "/remove/{id}")
    public ResponseEntity removeWorkSpaceAndPage(@ApiParam(value = "组织id", required = true)
                                                 @PathVariable(value = "organization_id") Long organizationId,
                                                 @ApiParam(value = "工作空间目录id", required = true)
                                                 @PathVariable @Encrypt(EncryptConstants.TN_KB_WORKSPACE) Long id) {
        workSpaceService.removeWorkSpaceAndPage(organizationId, null, id, true);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "移除组织下工作空间及页面（删除自己的空间）")
    @PutMapping(value = "/remove_my/{id}")
    public ResponseEntity removeWorkSpaceAndPageMyWorkSpace(@ApiParam(value = "组织id", required = true)
                                                            @PathVariable(value = "organization_id") Long organizationId,
                                                            @ApiParam(value = "工作空间目录id", required = true)
                                                            @PathVariable @Encrypt(EncryptConstants.TN_KB_WORKSPACE) Long id) {
        workSpaceService.removeWorkSpaceAndPage(organizationId, null, id, false);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "查询最近更新的空间列表")
    @GetMapping(value = "/recent_update_list")
    public ResponseEntity<List<WorkSpaceRecentInfoVO>> recentUpdateList(@ApiParam(value = "组织id", required = true)
                                                                        @PathVariable(value = "organization_id") Long organizationId,
                                                                        @RequestParam @Encrypt(EncryptConstants.TN_KB_KNOWLEDGE_BASE) Long baseId) {
        //组织层设置成permissionLogin=true，因此需要单独校验权限
        workSpaceService.checkOrganizationPermission(organizationId);
        return new ResponseEntity<>(workSpaceService.recentUpdateList(organizationId, null,baseId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询空间所属知识库是否存在")
    @GetMapping(value = "/belong_base_exist/{id}")
    public ResponseEntity<Boolean> belongToBaseDelete(@ApiParam(value = "组织id", required = true)
                                                      @RequestParam Long organizationId,
                                                      @ApiParam(value = "工作空间目录id", required = true)
                                                      @PathVariable @Encrypt(EncryptConstants.TN_KB_WORKSPACE) Long id) {
        return new ResponseEntity<>(workSpaceService.belongToBaseExist(organizationId, null,id), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("组织层复制当前页面")
    @PostMapping("/clone_page")
    public ResponseEntity<WorkSpaceInfoVO> clonePage(@ApiParam(value = "组织id", required = true)
                                                     @RequestParam Long organizationId,
                                                     @ApiParam(value = "目录Id", required = true)
                                                     @RequestParam @Encrypt(EncryptConstants.TN_KB_WORKSPACE) Long workSpaceId) {
        return new ResponseEntity<>(workSpaceService.clonePage(organizationId, null, workSpaceId), HttpStatus.OK);
    }

}
