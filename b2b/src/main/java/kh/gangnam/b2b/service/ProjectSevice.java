package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.MessageResponse;
import kh.gangnam.b2b.dto.project.request.GanttSaveRequest;
import kh.gangnam.b2b.dto.project.request.GanttUpdateRequest;
import kh.gangnam.b2b.dto.project.response.GanttSaveResponse;
import kh.gangnam.b2b.dto.project.response.GanttUpdateResponse;

public interface ProjectSevice {
    
    /**
     * 테스크 목록 조회
     * 
     * @param id 프로젝트의 id 값
     * @return  테스크 목록 및 성공 실패 여부 반환
     */
    String getGantt(Long id);

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
     * @param employeeId 로그인 유저 id
     * @return 수정 성공 여부 반환
     */
    GanttUpdateResponse updateGantt(GanttUpdateRequest dto, Long employeeId);

    /**
     * task 삭제
     * 
     * @param id 삭제할 task id
     * @return  삭제 성공 여부 반환
     */
    MessageResponse deleteGantt(Long id);
}
