
package com.xc.service.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import com.xc.base.BaseRepository;
import com.xc.base.RepositoryHelper;
import com.xc.pojo.BaseEntity;
import com.xc.service.BaseService;
import com.xc.util.CommonUtil;
import com.xc.vo.ModelVo;
import com.xc.vo.ModelVo.Code;

/**
 * <p>抽象service层基类 提供一些简便方法
 * <p/>
 * <p>泛型 ： M 表示实体类型；ID表示主键类型
 * <p/>
 * <p>User: pengxinxin
 * <p>Date: 13-1-12 下午4:43
 * <p>Version: 1.0
 */
@Transactional
public abstract class BaseServiceImpl<M extends BaseEntity<ID>, ID extends Serializable> implements BaseService<M, ID>{ 
	
	@Autowired
	protected RepositoryHelper repositoryHelper;
	
	protected boolean isFullCached = false;
	
    public boolean isFullCached() {
		return isFullCached;
	}

	public void setFullCached(boolean isFullCached) {
		this.isFullCached = isFullCached;
	}

	private BaseRepository<M, ID> baseRepository;
    
    @Autowired
    public void setBaseRepository(BaseRepository<M, ID> baseRepository) {
        this.baseRepository = baseRepository;
    }
    
    public BaseRepository<M, ID> getBaseRepository(){
    	return this.baseRepository;
    }
    
    /**
     * 保存单个实体
     *
     * @param m 实体
     * @return 返回保存的实体
     */
    public boolean save(M m) {
    	M m1 =  baseRepository.save(m);
    	if(m1 != null){
    		return true;
    	}
        return false;
    }
    
    public Iterable<M> save(Iterable<M> entities) {
    	Iterable<M> m1 =  baseRepository.saveAll(entities);
    	return m1;
    }
    
    public M saveObj(M m) {
    	m =  baseRepository.save(m);
    	if(m != null){
    		return m;
    	}
    	return null;
    }

    public M saveAndFlush(M m) {
        m = baseRepository.saveAndFlush(m);
        return m;
    }

    /**
     * 更新单个实体
     *
     * @param m 实体
     * @return 返回更新的实体
     */
    public boolean update(M m) {
    	M m1 = baseRepository.save(m);
    	if(m1.equals(m)){
    		return false;
    	}
    	return true;
    }

    /**
     * 根据主键删除相应实体
     *
     * @param id 主键
     */
    @Transactional
    public boolean delete(ID id) {
        baseRepository.deleteById(id);
        return true;
    }
    
    /**
     * 批量删除实体
     * @param entities
     */
    @Transactional
    public void deleteInBatch(final Iterable<M> entities){
    	baseRepository.deleteInBatch(entities);
    }

    /**
     * 删除实体
     *
     * @param m 实体
     */
    public void delete(M m) {
        baseRepository.delete(m);
    }

    /**
     * 逻辑删除实体
     *
     * @param id 主键
     */
    public ModelVo deleteStatus(ID id,String userId){
    	ModelVo vo = new ModelVo();
    	M m =findOne(id);
    	if(m!=null){
    		m.setDeleteStatus(true);
    		m.setUpdateTime();
    		m.setUpdateUserId(userId);
    		baseRepository.save(m);
    		vo.setCode(Code.SUCCESS);
    	}else{
    		vo.setCodeEnum(Code.ERROR,"删除失败,实体不存在");
    	}
    	return  vo;
    }

    /**
     * 按照主键查询
     *
     * @param id 主键
     * @return 返回id对应的实体
     */
    public M findOne(ID id) {
       Optional<M> m = baseRepository.findById(id);
       
        return m.isPresent()?m.get():null;
    }
    

    /**
     * 实体是否存在
     *
     * @param id 主键
     * @return 存在 返回true，否则false
     */
    public boolean exists(ID id) {
        return baseRepository.existsById(id);
    }

    /**
     * 统计实体总数
     *
     * @return 实体总数
     */
    public long count() {
        return baseRepository.count();
    }

    /**
     * 查询所有实体
     *
     * @return
     */
    public List<M> findAll() {
        return baseRepository.findAll();
    }

    public Page<M> findAll(Specification<M> spec, Pageable pageable){
    	return getBaseRepository().findAll(spec, pageable);
    }
    
    public Page<M> findAll(Specification<M> spec,int page,int size){
    	return getBaseRepository().findAll(spec, new PageRequest(page,size));
    }
    
    public List<M> findAll(@Nullable Specification<M> spec, Sort sort) {
    	return getBaseRepository().findAll(spec, sort);
    }
    
    public List<M> findAll(Specification<M> spec){
    	return getBaseRepository().findAll(spec);
    }
    
    
    public Page<M> findAll(Pageable pageable){
    	return getBaseRepository().findAll(pageable);
    }
    
    public M findOne(Specification<M> spec){
    	Optional<M> obj = getBaseRepository().findOne(spec);
    	return obj.isPresent()?obj.get():null;
    }
    
    
    public Page<M> findAll(Example<M> spec, Pageable pageable){
    	return getBaseRepository().findAll(spec, pageable);
    }
    
    public Page<M> findAll(Example<M> spec,int page,int size){
    	return getBaseRepository().findAll(spec, new PageRequest(page,size));
    }
    
    
    public List<M> findAll(Example<M> spec){
    	return getBaseRepository().findAll(spec);
    }
    
  
    public M findOne(Example<M> example){
    	Optional<M> obj = getBaseRepository().findOne(example);
    	return obj.isPresent()?obj.get():null;
    }
    
    
    

	public RepositoryHelper getRepositoryHelper() {
		return repositoryHelper;
	}

	public void setRepositoryHelper(RepositoryHelper repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}
	
		
		
	public ModelVo saveObject(M obj,String userId,String...filters){
		ModelVo vo = new ModelVo();
		vo.setCode(Code.SUCCESS);
		if(userId!=null && userId.trim().length()==0){
			userId = null;
		}
		if(obj.getId()==null){
//			if(ID instanceof String) {
//				
//			}
			Type t =obj.getClass().getGenericSuperclass();
			if(((ParameterizedType) t).getActualTypeArguments()[0].getTypeName().equals(String.class.getTypeName())){
				obj.setId((ID)UUID.randomUUID().toString());
			}
			
			obj.initDate();
			obj.initUser(userId);
			this.save(obj);			
			
		}else{
			M dbObj = this.findOne(obj.getId());
			CommonUtil.mergeObject(obj, dbObj,true,filters);
			dbObj.setUpdateTime();
			dbObj.setUpdateUserId(userId);
			this.update(dbObj);
		}
		vo.getResult().put("id", obj.getId());
		
		return vo;
		
	}
	
	public ModelVo getObject(ID id){
		ModelVo vo = new ModelVo();
		M obj = this.findOne(id);
		if(obj==null){
			vo.setCodeEnum(Code.ERROR, "查找不到数据");
		}else{
			vo.getResult().put("obj", obj);
			vo.setCode(Code.SUCCESS);
		}
		return vo;
	}
}
