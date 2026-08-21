package com.build.ecommerce.domain.file.repository;

import com.build.ecommerce.domain.file.entity.FileMaster;

import java.util.List;

public interface FileMasterRepository {

    FileMaster save(FileMaster fileMaster);

    List<FileMaster> findAllWithDetailsByIdIn(List<Long> fileMasterIds);
}
