package com.srr.repository;

import com.srr.domain.Format;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FormatRepository extends JpaRepository<Format, Long>, JpaSpecificationExecutor<Format> {
}
