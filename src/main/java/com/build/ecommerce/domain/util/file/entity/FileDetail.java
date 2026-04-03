package com.build.ecommerce.domain.util.file.entity;

import com.build.ecommerce.core.util.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "FILE_DETAIL",
        uniqueConstraints = {
        @UniqueConstraint(name = "uk_file_order", columnNames = {"file_master_id", "sortOrder"})
        }
)
@Comment(value = "File detail Table", on = "TABLE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FileDetail extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_master_id", nullable = false)
    private FileMaster fileMaster;

    @Comment("파일 정렬")
    @Column(nullable = false)
    private Integer sortOrder;

    @Comment("파일 저장 명")
    @Column(nullable = false)
    private String storedFileName;

    @Comment("파일 업로드 명(실제 이름)")
    @Column(nullable = false)
    private String originalFileName;

    @Comment("파일 확장자")
    @Column(nullable = false)
    private String extension;

    @Comment("파일 사이즈")
    @Column(nullable = false)
    private Long fileSize;

    @Comment("파일 경로")
    @Column(nullable = false)
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
