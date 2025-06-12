package kh.gangnam.b2b.service;

import kh.gangnam.b2b.dto.work.request.leave.LeaveRequestRequest;
import kh.gangnam.b2b.dto.work.response.leave.LeaveRequestResponse;
import kh.gangnam.b2b.dto.work.response.leave.LeaveStatusResponse;

import java.util.List;


public interface LeaveRequestService {
    void applyLeave(Long employeeId, LeaveRequestRequest dto); // 메서드명: applyLeave

    void approveLeave(Long requestId, Long approvedId);

    LeaveStatusResponse getLeaveStatus(Long employeeId);

    List<LeaveRequestResponse> getMyRequests(Long employeeId);


}