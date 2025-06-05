package kh.gangnam.b2b.controller;

import kh.gangnam.b2b.config.security.CustomEmployeeDetails;
import kh.gangnam.b2b.dto.MessageResponse;
import kh.gangnam.b2b.dto.project.request.DocumentUpdateRequest;
import kh.gangnam.b2b.dto.project.request.GanttSaveRequest;
import kh.gangnam.b2b.dto.project.request.GanttUpdateRequest;
import kh.gangnam.b2b.dto.project.request.LinkSaveRequest;
import kh.gangnam.b2b.dto.project.response.*;
import kh.gangnam.b2b.service.ServiceImpl.ProjectServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectServiceImpl projectService;

    @GetMapping("/get/{id}")
    public ResponseEntity<List<GanttListResponse>> getGantt(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.getGantt(id));
    }

    @PostMapping("/save")
    public ResponseEntity<GanttSaveResponse> saveGantt(@RequestBody GanttSaveRequest dto, @AuthenticationPrincipal CustomEmployeeDetails employeeDetails) {
        return ResponseEntity.ok(projectService.saveGantt(dto,employeeDetails.getEmployeeId()));
    }

    @PutMapping("/update")
    public ResponseEntity<GanttUpdateResponse> updateGantt(@RequestBody GanttUpdateRequest dto) {
        return ResponseEntity.ok(projectService.updateGantt(dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<MessageResponse> deleteGantt(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.deleteGantt(id));
    }

    @GetMapping("/link/get/{id}")
    public ResponseEntity<List<LinkListResponse>> getLink(@PathVariable("id") Long id) {
        System.out.println("get");
        return ResponseEntity.ok(projectService.getLink(id));
    }

    @PostMapping("/link/save")
    public ResponseEntity<MessageResponse> saveLink(@RequestBody LinkSaveRequest dto) {
        return ResponseEntity.ok(projectService.saveLink(dto));
    }

    @DeleteMapping("/link/delete/{id}")
    public ResponseEntity<MessageResponse> deleteLink(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.deleteLink(id));
    }

    @GetMapping("/document/get/{id}")
    public ResponseEntity<DocumentGetResponse> getDocument(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.getDocument(id));
    }

    @PostMapping("/document/save")
    public ResponseEntity<MessageResponse> updateDocument(@RequestBody DocumentUpdateRequest dto, @AuthenticationPrincipal CustomEmployeeDetails employeeDetails) {
        System.out.println(dto.docId());
        return ResponseEntity.ok(projectService.updateDocument(dto,employeeDetails.getEmployeeId()));
    }
}
