package com.build.ecommerce.domain.util.file.entity;

import com.build.ecommerce.core.util.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "FILE_DETAIL")
@Comment(value = "File detail Table", on = "TABLE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FileDetail extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_master_id")
    private FileMaster fileMaster;

    @Comment("파일 정렬")
    private int ord;
    @Comment("파일 저장 명")
    private String saveName;
    @Comment("파일 업로드 명(실제 이름)")
    private String originalName;
    @Comment("파일 확장자")
    private String extension;
    @Comment("파일 사이즈")
    private Integer size;
    @Comment("파일 경로")
    private String path;

}
