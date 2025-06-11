package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.MessageResponse;
import kh.gangnam.b2b.dto.project.request.*;
import kh.gangnam.b2b.dto.project.response.*;

import java.util.List;

public interface ProjectService {
    
    /**
     * 테스크 목록 조회
     * 
     * @param id 프로젝트의 id 값
     * @return  테스크 목록 및 성공 실패 여부 반환
     */
    List<GanttListResponse> getGantt(Long id);

    /**
     * task 정보 저장
     * 
     * @param dto 저장할 task 정보
     * @param employeeId 로그인 유저 id
     * @return 저장 성공 여부 반환
     */
    GanttSaveResponse saveGantt(GanttSaveRequest dto, Long employeeId);

    /**
     * task 정보 수정
     * 
     * @param dto 수정할 task 정보
     * @return 수정 성공 여부 반환
     */
    GanttUpdateResponse updateGantt(GanttUpdateRequest dto);

    /**
     * task 삭제
     * 
     * @param id 삭제할 task id
     * @return  삭제 성공 여부 반환
     */
    MessageResponse deleteGantt(Long id);

    /**
     * link 목록 조회
     *
     * @param id 해당 프로젝트 id
     * @return link 목록
     */
    List<LinkListResponse> getLink(Long id);

    /**
     * link 저장
     *
     * @param dto link 정보
     * @return 저장 성공 여부 반환
     */
    MessageResponse saveLink(LinkSaveRequest dto);

    /**
     * link 삭제
     *
     * @param id 삭제할 링크 id
     * @return 삭제 성공 여부 반환
     */
    MessageResponse deleteLink(Long id);

    /**
     * document 목록 조회
     *
     * @param id 해당 테스크 id
     * @return Document 목록
     */
    DocumentGetResponse getDocument(Long id);

    /**
     * document 수정
     *
     * @param dto link 정보
     * @return 수정 성공 여부 반환
     */
    MessageResponse updateDocument(DocumentUpdateRequest dto, Long employeeId);

    /**
     * department 부서 리스트 조회
     *
     * @return 부서 리스트 반환
     */
    List<departmentListResponse> getDepartment();

    /**
     * department 사원 리스트 조회
     *
     * @return 사원 리스트 반환
     */
    List<employeeListResponse> getEmployee(Long id);

    /**
     * project 리스트 조회
     *
     * @return project 리스트 반환
     */
    List<ProjectListResponse> getProject(Long id);

    /**
     * project 저장
     *
     * @return project 저장 성공 여부 반환
     */
    MessageResponse saveProject(ProjectSaveRequest dto, Long employeeId);

    /**
     * project 삭제
     *
     * @return project 삭제 성공 여부 반환
     */
    MessageResponse deleteProject(Long id);

    ProjectListResponse getProjectTitle(Long id);

    List<DocumentListResponse> getDocumentList(Long id);
}
