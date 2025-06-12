package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.work.request.leave.LeaveRequest;
import kh.gangnam.b2b.dto.work.response.leave.LeaveStatusResponse;
import kh.gangnam.b2b.entity.work.LeaveRequestEntity;

import java.util.List;


public interface LeaveRequestService {
    void applyLeave(Long employeeId, LeaveRequest dto); // 메서드명: applyLeave

    void approveLeave(Long requestId, Long approvedId);

    LeaveStatusResponse getLeaveStatus(Long employeeId);

    List<LeaveRequestEntity> getMyRequests(Long employeeId);


}