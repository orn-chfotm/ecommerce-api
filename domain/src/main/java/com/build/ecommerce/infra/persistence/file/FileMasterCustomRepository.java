package com.build.ecommerce.infra.persistence.file;

import com.build.ecommerce.infra.file.entity.FileMaster;

import java.util.List;

interface FileMasterCustomRepository {

    List<FileMaster> findAllWithDetailsByIdIn(List<Long> fileMasterIds);
}
