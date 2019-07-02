/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.xc.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SerializationException;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MySqlJsonType implements ParameterizedType, UserType {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	static{
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		objectMapper.setSerializationInclusion(Include.NON_EMPTY);
	}
	private static final ClassLoaderService classLoaderService = new ClassLoaderServiceImpl();

	public static final String JSONB_TYPE = "json";
	public static final String CLASS = "CLASS";
	public static final String ARRAY_CLASS = "ARRAY_CLASS";

	private Class jsonClassType;

	private Class listClassType;

	@Override
	public Class<Object> returnedClass() {
		return Object.class;
	}

	@Override
	public int[] sqlTypes() {
		return new int[]{Types.JAVA_OBJECT};
	}


	@Override
	public void setParameterValues(Properties parameters) {
		String clazz = (String) parameters.get(CLASS);
		if(clazz!=null){
			jsonClassType = classLoaderService.classForName(clazz);
		}

		clazz = (String) parameters.get(ARRAY_CLASS);
		if(clazz!=null){
			listClassType = classLoaderService.classForName(clazz);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object deepCopy(Object value) throws HibernateException {

		if (!(value instanceof Collection)) {
			return value;
		}

		Collection<?> collection = (Collection) value;
		Collection collectionClone = CollectionFactory.newInstance(collection.getClass());

		collectionClone.addAll(collection.stream().map(this::deepCopy).collect(Collectors.toList()));

		return collectionClone;
	}

	static final class CollectionFactory {
		@SuppressWarnings("unchecked")
		static <E, T extends Collection<E>> T newInstance(Class<T> collectionClass) {
			if (List.class.isAssignableFrom(collectionClass)) {
				return (T) new ArrayList<E>();
			} else if (Set.class.isAssignableFrom(collectionClass)) {
				return (T) new HashSet<E>();
			} else {
				throw new IllegalArgumentException("Unsupported collection type : " + collectionClass);
			}
		}
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y) {
			return true;
		}

		if ((x == null) || (y == null)) {
			return false;
		}

		return x.equals(y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		assert (x != null);
		return x.hashCode();
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return deepCopy(cached);
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		Object deepCopy = deepCopy(value);

		if (deepCopy!=null && !(deepCopy instanceof Serializable)) {
			throw new SerializationException(String.format("%s is not serializable class", value), null);
		}

		return (Serializable) deepCopy;
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return deepCopy(original);
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,
			SharedSessionContractImplementor session, Object owner)
			throws HibernateException, SQLException {
		try {
			final String json = rs.getString(names[0]);
			if(jsonClassType==null){
				return json;
			}
			if(listClassType!=null){
				JavaType javaType = objectMapper.getTypeFactory().constructParametricType(listClassType,jsonClassType);
				return json == null ? null : objectMapper.readValue(json, javaType);
			}
//			return json;
			return json == null ? null : objectMapper.readValue(json, jsonClassType);
		} catch (Exception e) {
			throw new HibernateException(e);
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SharedSessionContractImplementor session)
			throws HibernateException, SQLException {
		try {
			final String json = value == null ? null :value instanceof String? value.toString():CommonUtil.getJsonString(value);
			st.setObject(index, json);
		} catch (Exception e) {
			throw new HibernateException(e);
		}

	}
}
