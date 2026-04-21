package com.build.ecommerce.domain.util.file.entity;

import com.build.ecommerce.common.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "FILE_DETAIL",
        uniqueConstraints = {
        @UniqueConstraint(name = "uk_file_order", columnNames = {"file_master_id", "sortOrder"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment(value = "File detail Table", on = "TABLE")
public class FileDetail extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_master_id", nullable = false)
    @Setter
    @Comment("파일 마스터 FK")
    private FileMaster fileMaster;

    @Column(nullable = false)
    @Comment("파일 정렬")
    private Integer sortOrder;

    @Column(nullable = false)
    @Comment("파일 저장 명")
    private String storedFileName;

    @Column(nullable = false)
    @Comment("파일 업로드 명(실제 이름)")
    private String originalFileName;

    @Column(nullable = false)
    @Comment("파일 확장자")
    private String extension;

    @Column(nullable = false)
    @Comment("파일 사이즈")
    private Long fileSize;

    @Column(nullable = false)
    @Comment("파일 경로")
    private String path;

    @Builder
    public FileDetail(FileMaster fileMaster, Integer sortOrder, String storedFileName, String originalFileName, String extension, Long fileSize, String path) {
        this.fileMaster = fileMaster;
        this.sortOrder = sortOrder;
        this.storedFileName = storedFileName;
        this.originalFileName = originalFileName;
        this.extension = extension;
        this.fileSize = fileSize;
        this.path = path;
    }
}
