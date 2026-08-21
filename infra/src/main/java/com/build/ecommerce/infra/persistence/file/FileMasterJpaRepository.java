package com.build.ecommerce.infra.persistence.file;

import com.build.ecommerce.domain.file.entity.FileMaster;
import org.springframework.data.jpa.repository.JpaRepository;

interface FileMasterJpaRepository extends JpaRepository<FileMaster, Long> {
}
