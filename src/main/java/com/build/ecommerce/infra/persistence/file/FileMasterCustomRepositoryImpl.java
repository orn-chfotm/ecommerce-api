package com.build.ecommerce.infra.persistence.file;

import com.build.ecommerce.infra.file.entity.FileMaster;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.build.ecommerce.infra.file.entity.QFileDetail.fileDetail;
import static com.build.ecommerce.infra.file.entity.QFileMaster.fileMaster;

@RequiredArgsConstructor
class FileMasterCustomRepositoryImpl implements FileMasterCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<FileMaster> findAllWithDetailsByIdIn(List<Long> fileMasterIds) {
        return jpaQueryFactory.selectFrom(fileMaster)
                .distinct()
                .leftJoin(fileMaster.fileDetailList, fileDetail).fetchJoin()
                .where(fileMaster.id.in(fileMasterIds))
                .fetch();
    }
}
