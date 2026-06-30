package com.build.ecommerce.infra.persistence.file;

import com.build.ecommerce.infra.file.entity.FileMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMasterRepository extends JpaRepository<FileMaster, Long> {
}
