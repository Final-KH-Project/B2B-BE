package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.MessageResponse;
import kh.gangnam.b2b.dto.project.request.GanttSaveRequest;
import kh.gangnam.b2b.dto.project.request.GanttUpdateRequest;
import kh.gangnam.b2b.dto.project.response.GanttSaveResponse;
import kh.gangnam.b2b.dto.project.response.GanttUpdateResponse;
import kh.gangnam.b2b.service.ProjectSevice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectSeviceImpl implements ProjectSevice {

    @Override
    public String getGantt(Long id) {
        return "";
    }

    @Override
    public GanttSaveResponse saveGantt(GanttSaveRequest dto, Long employeeId) {
        return null;
    }

    @Override
    public GanttUpdateResponse updateGantt(GanttUpdateRequest dto, Long employeeId) {
        return null;
    }

    @Override
    public MessageResponse deleteGantt(Long id) {
        return null;
    }
}
