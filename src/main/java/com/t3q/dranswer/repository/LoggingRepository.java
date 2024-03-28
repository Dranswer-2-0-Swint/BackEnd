package com.t3q.dranswer.repository;

import com.t3q.dranswer.entity.LoggingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LoggingRepository extends JpaRepository<LoggingEntity, Long> {
}
