package kh.gangnam.b2b.entity.project;

import jakarta.persistence.*;
import kh.gangnam.b2b.dto.project.request.GanttUpdateRequest;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class Task {

    @Id
    private Long taskId;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Employee createdBy;

    @Column(nullable = false)
    private String title;

    private Integer duration;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private double progress;

    private String type;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Task parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Task> children = new ArrayList<>();

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL)
    private Document document;

    public Task update(GanttUpdateRequest dto){
        this.title=dto.title();
        this.duration=dto.duration();
        this.startDate=dto.startDate();
        this.progress=dto.progress();
        return this;
    }

}
