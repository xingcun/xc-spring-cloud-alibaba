
package com.xc.base;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.xc.pojo.BaseEntity;

/**
 * 仓库辅助类
 */
@Component
public class RepositoryHelper {

	@Autowired
	private  EntityManager entityManager;

	private static final String parameterKey="parameter";

//	@Autowired
//	private  SessionFactory sessionFactory;



//    private Class<?> entityClass;

    /**
     * @param entityClass 是否开启查询缓存
     */
    public RepositoryHelper() {
//        this.entityClass = entityClass;

    }

//    @Autowired
//    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
//    	sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
//        entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory,entityManagerFactory.getProperties());
//    }

    public  EntityManager getEntityManager() {
        Assert.notNull(entityManager, "entityManager must null, please see " +
                "[com.weiju.base.RepositoryHelper#setEntityManagerFactory]");
        return entityManager;
    }


    public  void flush() {
        getEntityManager().flush();
    }

    public  void clear() {
        flush();
        getEntityManager().clear();
    }


    public <M> List<M> findAll(final String ql,final Class cs,final Object... params) {
    	return findAll(ql, null, cs, params);
    }
    /**
     * <p>根据ql和按照索引顺序的params执行ql，pageable存储分页信息 null表示不分页<br/>
     *
     * @param sql
     * @param pageable null表示不分页
     * @param params
     * @param <M>
     * @return
     */
    private <M> List<M> findAll(String sql, Pageable pageable,final Class cs,final Object... params) {
    	sql = sql + prepareOrder(pageable != null ? pageable.getSort() : null);
    	sql = initSql(sql);
//    	System.out.println(sql);
        Query query = cs==null?getEntityManager().createNativeQuery(sql):getEntityManager().createNativeQuery(sql,cs);
        setParameters(query, params);
        if (pageable != null) {
            query.setFirstResult((int)pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
            if(pageable.getSort()==null && cs!=null && (BaseEntity.class.isAssignableFrom(cs))){
//            	pageable.getSortOr(sort)
            }
        }
        if(cs!=null){
        	query.setHint("org.hibernate.cacheable", "true");
        }
        return query.getResultList();
    }

    public <M> Page<M> findAllPage(final String sql,Pageable page,final Class cs, Object... params ) {
//		System.out.println(sql);
		List result = findAll(sql,page,cs,params );
		long total = result.size();
		if(page!=null && (page.getPageNumber()!=0 || page.getPageSize()==total)){
			total = count("select count(1) from ("+sql+") countFrom",  params);
		}

		if(page==null) {
			page = PageRequest.of(0,total>0? Integer.parseInt(String.valueOf(total)):10);
		}
		return new PageImpl(result, page, total);
	}

    public int update(String sql, Object... params ){
    	sql = initSql(sql);
    	Query query = getEntityManager().createNativeQuery(sql);
    	setParameters(query, params);

    	return query.executeUpdate();
    }


    /**
     * <p>根据ql和按照索引顺序的params查询一个实体<br/>
     *
     * @param sql
     * @param params
     * @param <M>
     * @return
     */
    public <M> M findOne(final String sql,final Class cs, final Object... params) {
        List<M> list = findAll(sql, PageRequest.of(0, 1),cs, params);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    private String initSql(String sql){
    	int i=0;
    	while(sql.indexOf("p_?")!=-1){
    		sql = sql.replaceFirst("p_\\?",":"+parameterKey+i++);
    	}
    	return sql;
    }

    /**
     * <p>根据ql和按照索引顺序的params执行ql统计<br/>
     * 具体使用请参考测试用例：com.core.common.repository.UserRepository2ImplIT#testCountAll()
     *
     * @param sql
     * @param params
     * @return
     */
    public long count(String sql,final Object... params) {
    	sql = initSql(sql);
        Query query =entityManager.createNativeQuery(sql);
        setParameters(query, params);
        Object obj = query.getSingleResult();
        if(obj instanceof Integer){
        	return ((Integer)obj).longValue();
        }
        if(obj instanceof BigInteger){
        	return ((BigInteger)obj).longValue();
        }
        if(obj instanceof BigDecimal) {
            return ((BigDecimal)obj).longValue();
        }
        if(obj == null){
            return 0L;
        }
        return ((Long)obj).longValue();
    }



    /**
     * 按顺序设置Query参数
     *
     * @param query
     * @param params
     */
    public void setParameters(Query query, Object[] params) {
        if (params != null && params.length>0) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(parameterKey+i, params[i]);
            }
        }
    }
    /**
     * 拼排序
     *
     * @param sort
     * @return
     */
    public String prepareOrder(Sort sort) {
        if (sort == null || !sort.iterator().hasNext()) {
            return "";
        }
        StringBuilder orderBy = new StringBuilder("");
        orderBy.append(" order by ");
        orderBy.append(sort.toString().replace(":", " "));
        return orderBy.toString();
    }


}
