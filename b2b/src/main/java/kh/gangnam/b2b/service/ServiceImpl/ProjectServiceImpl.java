package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.MessageResponse;
import kh.gangnam.b2b.dto.project.request.*;
import kh.gangnam.b2b.dto.project.response.*;
import kh.gangnam.b2b.entity.Dept;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.project.Document;
import kh.gangnam.b2b.entity.project.Project;
import kh.gangnam.b2b.entity.project.Task;
import kh.gangnam.b2b.repository.DeptRepository;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.repository.project.DocumentRepository;
import kh.gangnam.b2b.repository.project.LinkRepository;
import kh.gangnam.b2b.repository.project.ProjectRepository;
import kh.gangnam.b2b.repository.project.TaskRepository;
import kh.gangnam.b2b.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final LinkRepository linkRepository;
    private final DocumentRepository documentRepository;
    private final DeptRepository deptRepository;

    @Override
    public List<GanttListResponse> getGantt(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("project not found"));


        return project.getTasks().stream().map(GanttListResponse::fromEntity).toList();
    }

    @Override
    @Transactional
    public GanttSaveResponse saveGantt(GanttSaveRequest dto, Long employeeId) {

        // 작성 employee 가져오기
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new UsernameNotFoundException("project not found"));
        // 프로젝트 id 가져오기
        Project project = projectRepository.findById(dto.projectId())
                .orElseThrow(() -> new UsernameNotFoundException("해당 프로젝트가 존재하지 않습니다"));
        log.warn("error2----------------------------");
        // 부모 id 가져오기
        Task parentTask = Optional.ofNullable(dto.parent())
                .flatMap(taskRepository::findById)
                .orElse(null);

        log.warn("error3----------------------------");
        Task task = taskRepository.save(dto.toEntity(project,employee,parentTask));
        documentRepository.save(dto.toEntity(task,employee));
        System.out.println(task);

        return GanttSaveResponse.fromEntity(task);
    }

    @Override
    @Transactional
    public GanttUpdateResponse updateGantt(GanttUpdateRequest dto) {

        // 해당 task를 찾고 수정된 내용 저장
        Task task = taskRepository.findById(dto.taskId())
                .orElseThrow(() -> new UsernameNotFoundException("해당 테스크가 존재하지 않습니다"))
                .update(dto);

        return GanttUpdateResponse.fromEntity(task);
    }

    @Override
    public MessageResponse deleteGantt(Long id) {

        taskRepository.deleteById(id);

        return MessageResponse.sendMessage("삭제 성공");
    }

    @Override
    public List<LinkListResponse> getLink(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("project not found"));

        return project.getLinks().stream().map(LinkListResponse::fromEntity).toList();
    }

    @Override
    public MessageResponse saveLink(LinkSaveRequest dto) {

        Project project = projectRepository.findById(dto.projectId())
                .orElseThrow(() -> new UsernameNotFoundException("해당 프로젝트가 존재하지 않습니다"));

        linkRepository.save(dto.toEntity(project));

        return MessageResponse.sendMessage("저장 성공");
    }

    @Override
    public MessageResponse deleteLink(Long id) {

        linkRepository.deleteById(id);

        return MessageResponse.sendMessage("삭제 성공");
    }

    @Override
    public DocumentGetResponse getDocument(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("task not found"));

        return DocumentGetResponse.fromEntity(task.getDocument());
    }

    @Override
    @Transactional
    public MessageResponse updateDocument(DocumentUpdateRequest dto, Long employeeId) {

        Document document = documentRepository.findById(dto.docId())
                .orElseThrow(() -> new UsernameNotFoundException("document not found"));
        document.update(dto);
        return MessageResponse.sendMessage("수정 완료");
    }

    @Override
    public List<departmentListResponse> getDepartment() {

        return deptRepository.findAll().stream()
                .map(departmentListResponse::fromEntity).toList();
    }

    @Override
    public List<employeeListResponse> getEmployee(Long id) {

        return employeeRepository.findByDeptDeptId(id)
                .stream().map(employeeListResponse::fromEntity).toList();
    }

    @Override
    public List<ProjectListResponse> getProject(Long id) {

        return employeeRepository.findById(id).orElseThrow().getProjects().stream()
                .distinct().map(project -> ProjectListResponse.fromEntity(project, id)).toList();
    }

    @Override
    public MessageResponse saveProject(ProjectSaveRequest dto, Long employeeId) {

        // 부서 id
        Dept dept = deptRepository.findById(dto.departmentId())
                .orElseThrow(() -> new UsernameNotFoundException("department not found"));

        // 매니저 id
        Employee manager = employeeRepository.findById(dto.employeeId())
                .orElseThrow(() -> new UsernameNotFoundException("employee not found"));

        // 작성자
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new UsernameNotFoundException("employee not found"));

        // 담당 사원
        List<Employee> members = employeeRepository.findAllById(dto.members());

        Project project = projectRepository.save(dto.toEntity(dept,manager,members,employee));

        return MessageResponse.sendMessage(project.getProjectId() + "");
    }

    @Override
    public MessageResponse deleteProject(Long id) {
        
        projectRepository.deleteById(id);
        return MessageResponse.sendMessage("삭제 성공");
    }

    @Override
    public ProjectListResponse getProjectTitle(Long id) {
        return ProjectListResponse.fromEntity(projectRepository.findById(id).orElseThrow());
    }

    @Override
    public List<DocumentListResponse> getDocumentList(Long id) {

        List<Task> tasks = projectRepository.findById(id).orElseThrow().getTasks();

        return tasks.stream()
                .map(task -> DocumentListResponse.fromEntity(task, task.getDocument())).toList();
    }
}
