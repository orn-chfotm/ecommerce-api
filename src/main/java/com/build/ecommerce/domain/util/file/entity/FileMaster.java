package com.build.ecommerce.domain.util.file.entity;

import com.build.ecommerce.core.util.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Comment("파일 리스트")
    @OneToMany(mappedBy = "fileMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    List<FileDetail> fileDetailList = new ArrayList<>();

    public void addFileDetail(FileDetail fileDetail) {
        this.fileDetailList.add(fileDetail);
        fileDetail.setFileMaster(this);
    }
}
