package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.dto.MessageResponse;
import kh.gangnam.b2b.dto.auth.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.project.request.GanttSaveRequest;
import kh.gangnam.b2b.dto.project.request.GanttUpdateRequest;
import kh.gangnam.b2b.dto.project.response.GanttSaveResponse;
import kh.gangnam.b2b.dto.project.response.GanttUpdateResponse;
import kh.gangnam.b2b.service.ServiceImpl.ProjectSeviceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectSeviceImpl projectSevice;

    @GetMapping("/get/{id}")
    public ResponseEntity<String> getGantt(@PathVariable("id") Long id) {
        System.out.println("get");
        return ResponseEntity.ok(projectSevice.getGantt(id));
    }

    @PostMapping("/save")
    public ResponseEntity<GanttSaveResponse> saveGantt(@RequestBody GanttSaveRequest dto, @AuthenticationPrincipal CustomEmployeeDetails employeeDetails) {
        System.out.println("save");
        return ResponseEntity.ok(projectSevice.saveGantt(dto,employeeDetails.getEmployeeId()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GanttUpdateResponse> updateGantt(@RequestBody GanttUpdateRequest dto, @AuthenticationPrincipal CustomEmployeeDetails employeeDetails) {
        System.out.println("update");
        return ResponseEntity.ok(projectSevice.updateGantt(dto, employeeDetails.getEmployeeId()));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<MessageResponse> deleteGantt(@PathVariable("id") Long id) {
        System.out.println("delete");
        return ResponseEntity.ok(projectSevice.deleteGantt(id));
    }
}
