package kh.gangnam.b2b.entity.project;

import jakarta.persistence.*;
import kh.gangnam.b2b.dto.project.request.DocumentUpdateRequest;
import kh.gangnam.b2b.entity.auth.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long docId;

    @OneToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Employee author;

    private String title;
    private String subTitle;
    private String content;

    @Version
    private Long version;

    public Document update(DocumentUpdateRequest dto){
        this.title=dto.title();
        this.subTitle= dto.subTitle();
        this.content=dto.content();
        return this;
    }
}
