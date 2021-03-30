package com.xc.service.impl.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import com.alibaba.fastjson.JSON;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import org.apache.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.xc.util.jwt.JwtTokenUtil;
import com.xc.pojo.user.User;
import com.xc.service.impl.BaseServiceImpl;
import com.xc.service.user.UserService;
import com.xc.util.CommonUtil;
import com.xc.util.jwt.JWTInfo;
import com.xc.vo.BaseModelVo.Code;
import com.xc.vo.ModelVo;

@Service
@org.springframework.stereotype.Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<User, String> implements UserService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	public ModelVo getUsers(ModelVo pageVo, String userId) {
		JSONObject input = pageVo.getInput();
		System.out.println(jwtTokenUtil.getUserSecret());

		ModelVo modelVo = new ModelVo();
		if(userId==null) {
			return getUsersSql(pageVo,userId);
		}
		Page<User> pages = this.findAll((root, query, cb) -> {

			List<Predicate> predicates = new ArrayList();
			if (CommonUtil.isNotNull(input.getString("keyword"))) {
				String keyword = "%"+input.getString("keyword")+"%";
				predicates.add(cb.or(cb.like(root.get("userName"), keyword),
						cb.like(root.get("mobile"), keyword),cb.like(root.get("nickName"),keyword)));
			}

			if (CommonUtil.isNotNull(input.getString("source"))) {
				predicates.add(cb.equal(root.get("source"), input.getInteger("source")));
			}
			if (input.getDate("beginTime") != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"),
						CommonUtil.formatDateStartPlus(input.getDate("beginTime"))));
			}

			if (input.getDate("endTime") != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("createTime"),
						CommonUtil.formatDatePlus(input.getDate("endTime"))));
			}

			predicates.add(cb.equal(root.get("deleteStatus"), false));
			query.where(predicates.toArray(new Predicate[predicates.size()]));
			return null;
		}, pageVo.getPage(Direction.DESC, "createTime"));

		modelVo.setPageObje(pages);

		List<User> records = pages.getContent();

		modelVo.getResult().clear();
		modelVo.getResult().put("list", records);
		modelVo.setCodeEnum(Code.SUCCESS);
		return modelVo;
	}

	public ModelVo getUsersSql(ModelVo pageVo, String userId) {
		JSONObject input = pageVo.getInput();

		ModelVo modelVo = new ModelVo();

		Page<User> pages = null;// this.findAll();

		List params = new ArrayList();

		StringBuilder sql = new StringBuilder("select * from base_user where delete_status=false ");

		if (CommonUtil.isNotNull(input.get("keyword"))) {
			sql.append(" and (userName like '%" + input.getString("keyword") + "%' or mobile like '%"
					+ input.getString("keyword") + "%' or nick_name like '%" + input.getString("keyword") + "%')");
		}
		if (CommonUtil.isNotNull(input.get("beginTime"))) {
			sql.append(" and create_time >= p_? ");
			params.add(CommonUtil.formatDateStartPlus(input.getDate("beginTime")));
		}
		if (CommonUtil.isNotNull(input.get("endTime"))) {
			sql.append(" and create_time <= p_? ");
			params.add(CommonUtil.formatDateStartPlus(input.getDate("endTime")));
		}

		if (CommonUtil.isNotNull(input.getString("source"))) {
			sql.append(" and source = p_? ");
			params.add(input.getIntValue("source"));
		}
		pages = this.repositoryHelper.findAllPage(sql.toString(), pageVo.getPage(Direction.DESC, "create_time"), User.class, params.toArray());

		modelVo.setPageObje(pages);

		List<User> records = pages.getContent();

		modelVo.getResult().clear();
		modelVo.getResult().put("list", records);
		modelVo.setCodeEnum(Code.SUCCESS);
		return modelVo;
	}

	@Override
	public ModelVo saveUser(User user,String userId,String...filters) {
		ModelVo vo = new ModelVo();
		if(!CommonUtil.isNotNull(user.getId())){
			if(!CommonUtil.isNotNull(user.getUserName()) ) {
				user.setUserName(user.getMobile());
			}
			if(!CommonUtil.isNotNull(user.getUserName()) ) {
				vo.setCodeEnum(Code.ERROR, "手机号和用户名不能同时为空");
				return vo;
			}

			if(!CommonUtil.isNotNull(user.getPassword())) {
				vo.setCodeEnum(Code.ERROR, "密码不能为空");
				return vo;
			}
			user.setValid(true);
		}

		String t_username = user.getUserName();
		String mobile = user.getMobile();
		List<User> users = this.findAll((root, query, cb) -> {
			List<Predicate> predicates = new ArrayList();
			if(CommonUtil.isNotNull(mobile)) {
				predicates.add(cb.or(cb.equal(root.get("userName"), t_username),
						cb.equal(root.get("mobile"), mobile)));
			}else {
				predicates.add(cb.equal(root.get("userName"), t_username));
			}
			if(CommonUtil.isNotNull(user.getId())){
				predicates.add(cb.notEqual(root.get("id"), user.getId()));
			}
			predicates.add(cb.equal(root.get("deleteStatus"), false));
			query.where(predicates.toArray(new Predicate[predicates.size()]));
			return null;
		}, Sort.by(Direction.DESC, "createTime"));

		if(users!=null && users.size()>0) {
			vo.setCodeEnum(Code.ERROR, "用户名或手机号已存在");
			return vo;
		}

		if(CommonUtil.isNotNull(user.getPassword())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		return this.saveObject(user, userId, filters);
	}


	@Override
	public ModelVo login(String username,String password,int loginType,String loginSystem) {
		this.findOne("3d3a8382-dcfb-4e9f-b2ea-8c7138f469ca");
		ModelVo vo = new ModelVo();
		List<User> users = this.findAll((root, query, cb) -> {

			List<Predicate> predicates = new ArrayList();

			if(loginType==0) {
				predicates.add(cb.equal(root.get("userName"), username));
			}
			if(loginType==1) {
				predicates.add(cb.equal(root.get("mobile"), username));
			}

			predicates.add(cb.equal(root.get("deleteStatus"), false));
			query.where(predicates.toArray(new Predicate[predicates.size()]));
			return null;
		}, Sort.by(Direction.DESC, "createTime"));
		if(users!=null && !users.isEmpty()) {
			User user  = users.get(0);

			if(loginType==0 && !passwordEncoder.matches(password, user.getPassword())) {
				vo.setCodeEnum(Code.ERROR, "密码不正确");
			}else {
				JWTInfo info = JWTInfo.of(user);
				try {
					String token = jwtTokenUtil.generateToken(info);
					vo.getResult().put("token", token);
					vo.setCodeEnum(Code.SUCCESS);
				} catch (Exception e) {
					e.printStackTrace();
					vo.setCodeEnum(Code.ERROR, "用户获取token失败:"+e.getMessage());
				}
			}

		}else {
			vo.setCodeEnum(Code.ERROR, username+"用户不存在");
		}

		JSONObject dbObj = new JSONObject();
		List<Ignite> ignites = Ignition.allGrids();
		for(Ignite ignite : ignites){
			JSONObject cacheObj = new JSONObject();

			ignite.cacheNames().forEach((key)->{

				cacheObj.put(key, JSON.toJSONString(ignite.cache(key)));
				ignite.cache(key).forEach((entry)->{
					System.out.println(entry.getKey()+"================"+ JSON.toJSONString(entry.getValue()));
				});
			});
			dbObj.put(ignite.name()+"",cacheObj);

		}

		return vo;
	}

	@Override
	public ModelVo getJwtUserPubKey() {
		ModelVo vo = new ModelVo();
		vo.setCodeEnum(Code.SUCCESS);
		vo.getResult().put("pubKey", jwtTokenUtil.getUserPubKey());
		return vo;
	}
}
