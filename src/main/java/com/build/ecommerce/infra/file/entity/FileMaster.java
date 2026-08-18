package com.build.ecommerce.infra.file.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import com.build.ecommerce.infra.file.enums.FileMasterType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "FILE_MASTER")
@Comment(value = "File Master Table", on = "TABLE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FileMaster extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILE_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("파일 참조 도메인 타입")
    private FileMasterType referenceType;

    @OneToMany(mappedBy = "fileMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Comment("파일 리스트")
    List<FileDetail> fileDetailList = new ArrayList<>();

    @Builder
    public FileMaster(FileMasterType referenceType) {
        this.referenceType = referenceType;
    }

    public void addFileDetail(FileDetail fileDetail) {
        this.fileDetailList.add(fileDetail);
        fileDetail.setFileMaster(this);
    }
}
