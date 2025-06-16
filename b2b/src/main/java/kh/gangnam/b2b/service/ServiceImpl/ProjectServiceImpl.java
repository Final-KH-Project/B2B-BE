package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.MessageResponse;
import kh.gangnam.b2b.dto.project.request.*;
import kh.gangnam.b2b.dto.project.response.*;
import kh.gangnam.b2b.entity.Dept;
import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.entity.project.Document;
import kh.gangnam.b2b.entity.project.Project;
import kh.gangnam.b2b.entity.project.Task;
import kh.gangnam.b2b.exception.NotFoundException;
import kh.gangnam.b2b.repository.DeptRepository;
import kh.gangnam.b2b.repository.project.DocumentRepository;
import kh.gangnam.b2b.repository.project.LinkRepository;
import kh.gangnam.b2b.repository.project.ProjectRepository;
import kh.gangnam.b2b.repository.project.TaskRepository;
import kh.gangnam.b2b.service.ProjectService;
import kh.gangnam.b2b.service.shared.EmployeeCommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final LinkRepository linkRepository;
    private final DocumentRepository documentRepository;
    private final DeptRepository deptRepository;
    private final EmployeeCommonService employeeCommonService;

    @Override
    public List<GanttListResponse> getGantt(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("프로젝트를 찾을 수 없습니다"));

        return project.getTasks().stream().map(GanttListResponse::fromEntity).toList();
    }

    @Override
    @Transactional
    public GanttSaveResponse saveGantt(GanttSaveRequest dto, Long employeeId) {

        Long parentId = dto.parent();
        Task parentTask = null;

        // 작성 employee 가져오기
        Employee employee = employeeCommonService.getEmployeeOrThrow(employeeId, "작성자가 존재하지 않습니다.");

        // 프로젝트 id 가져오기
        Project project = projectRepository.findById(dto.projectId())
                .orElseThrow(() -> new NotFoundException("프로젝트가 존재하지 않습니다"));

        // 부모 id 가져오기
        System.out.println(parentId);
        if (parentId != null) {
            parentTask = taskRepository.findById(parentId)
                    .orElseThrow(() -> new NotFoundException("부모 태스크를 찾을 수 없습니다"));
        }

        Task task = taskRepository.save(dto.toEntity(project,employee,parentTask));
        documentRepository.save(dto.toEntity(task,employee));

        return GanttSaveResponse.fromEntity(task);
    }

    @Override
    @Transactional
    public GanttUpdateResponse updateGantt(GanttUpdateRequest dto) {

        // 해당 task를 찾고 수정된 내용 저장
        Task task = taskRepository.findById(dto.taskId())
                .orElseThrow(() -> new NotFoundException("테스크가 존재하지 않습니다"))
                .update(dto);

        return GanttUpdateResponse.fromEntity(task);
    }

    @Override
    public MessageResponse deleteGantt(Long id) {

        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("삭제할 테스크가 존재하지 않습니다");
        }

        taskRepository.deleteById(id);

        return MessageResponse.sendMessage("삭제 성공");
    }

    @Override
    public List<LinkListResponse> getLink(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("프로젝트가 존재하지 않습니다"));

        return project.getLinks().stream().map(LinkListResponse::fromEntity).toList();
    }

    @Override
    public MessageResponse saveLink(LinkSaveRequest dto) {

        Project project = projectRepository.findById(dto.projectId())
                .orElseThrow(() -> new NotFoundException("프로젝트가 존재하지 않습니다"));

        linkRepository.save(dto.toEntity(project));

        return MessageResponse.sendMessage("저장 성공");
    }

    @Override
    public MessageResponse deleteLink(Long id) {

        if (!linkRepository.existsById(id)) {
            throw new NotFoundException("삭제할 링크가 존재하지 않습니다");
        }

        linkRepository.deleteById(id);

        return MessageResponse.sendMessage("삭제 성공");
    }

    @Override
    public DocumentGetResponse getDocument(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("테스크를 찾을 수 없습니다"));

        return DocumentGetResponse.fromEntity(task.getDocument());
    }

    @Override
    @Transactional
    public MessageResponse updateDocument(DocumentUpdateRequest dto, Long employeeId) {

        Document document = documentRepository.findById(dto.docId())
                .orElseThrow(() -> new NotFoundException("문서를 찾을 수 없습니다"));
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

        return employeeCommonService.getEmployeesInDept(id)
                .stream().map(employeeListResponse::fromEntity).toList();
    }

    @Override
    public List<ProjectListResponse> getProject(Long id) {

        return employeeCommonService.getEmployeeOrThrow(id, "사원을 찾을 수 없습니다.")
                .getProjects().stream()
                .distinct().map(project -> ProjectListResponse.fromEntity(project, id)).toList();
    }

    @Override
    public MessageResponse saveProject(ProjectSaveRequest dto, Long employeeId) {

        // 부서 id
        Dept dept = deptRepository.findById(dto.departmentId())
                .orElseThrow(() -> new NotFoundException("부서를 찾을 수 없습니다"));

        // 매니저 id
        Employee manager = employeeCommonService.getEmployeeOrThrow(dto.employeeId(), "관리자를 찾을 수 없습니다.");

        // 작성자
        Employee employee = employeeCommonService.getEmployeeOrThrow(dto.employeeId(), "사원을 찾을 수 없습니다.");

        // 담당 사원
        List<Employee> members = employeeCommonService.getEmployeesInList(dto.members());

        Project project = projectRepository.save(dto.toEntity(dept,manager,members,employee));

        return MessageResponse.sendMessage(project.getProjectId() + "");
    }

    @Override
    public MessageResponse deleteProject(Long id) {

        if (!projectRepository.existsById(id)) {
            throw new NotFoundException("삭제할 프로젝트가 존재하지 않습니다");
        }

        projectRepository.deleteById(id);
        return MessageResponse.sendMessage("삭제 성공");
    }

    @Override
    public ProjectListResponse getProjectTitle(Long id) {
        return ProjectListResponse.fromEntity(
                projectRepository.findById(id).orElseThrow(()-> new NotFoundException("프로젝트를 찾을 수 없습니다")));
    }

    @Override
    public List<DocumentListResponse> getDocumentList(Long id) {

        List<Task> tasks = projectRepository.findById(id).orElseThrow(()-> new NotFoundException("프로젝트를 찾을 수 없습니다")).getTasks();

        return tasks.stream()
                .map(task -> DocumentListResponse.fromEntity(task, task.getDocument())).toList();
    }
}
