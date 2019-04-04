package com.xc.base;

import java.io.Serializable;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@NoRepositoryBean
public interface BaseRepository<M, ID extends Serializable> extends JpaRepository<M, ID>,JpaSpecificationExecutor<M> {
	
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })  
	Page<M> findAll(Specification<M> spec, Pageable pageable);
	
//	@Modifying
//	@Query("update #{#entityName} u set u.deleteStatus=true where u.id = ?1")
//	void deleteEntity(ID id);
}
