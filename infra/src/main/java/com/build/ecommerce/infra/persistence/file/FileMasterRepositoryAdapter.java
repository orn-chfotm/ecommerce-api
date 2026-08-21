package com.build.ecommerce.infra.persistence.file;

import com.build.ecommerce.domain.file.entity.FileMaster;
import com.build.ecommerce.domain.file.repository.FileMasterRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.build.ecommerce.domain.file.entity.QFileDetail.fileDetail;
import static com.build.ecommerce.domain.file.entity.QFileMaster.fileMaster;

@Repository
@RequiredArgsConstructor
class FileMasterRepositoryAdapter implements FileMasterRepository {

    private final FileMasterJpaRepository jpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public FileMaster save(FileMaster fileMasterEntity) {
        return jpaRepository.save(fileMasterEntity);
    }

    @Override
    public List<FileMaster> findAllWithDetailsByIdIn(List<Long> fileMasterIds) {
        return jpaQueryFactory.selectFrom(fileMaster)
                .distinct()
                .leftJoin(fileMaster.fileDetailList, fileDetail).fetchJoin()
                .where(fileMaster.id.in(fileMasterIds))
                .fetch();
    }
}
