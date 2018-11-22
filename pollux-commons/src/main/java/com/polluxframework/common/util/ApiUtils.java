package com.polluxframework.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polluxframework.common.entity.Model;
import com.polluxframework.common.entity.ModelProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.reflections.Reflections;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

/**
 * @author zhumin0508
 * created in  2018/11/9 14:50
 * modified By:
 */
public class ApiUtils {
	private static final String METHOD_GET = "get";
	private static final String METHOD_IS = "is";

	private static Map<String, Model> modelMap = new HashMap<>(128);

	/**
	 * 获取所有已标注的实体类
	 *
	 * @param packageName 需要扫描的包
	 */
	public static void getModels(String packageName) throws Exception {
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> classesList = reflections.getTypesAnnotatedWith(ApiModel.class);
		for (Class classes : classesList) {
			getModel(classes);
		}
	}

	/**
	 * 获取的实体类
	 *
	 * @param cls 具体类
	 */
	public static Model getModel(Class<?> cls) throws Exception {
		ApiModel apiModel = cls.getAnnotation(ApiModel.class);
		if (apiModel != null) {
			Model model = new Model();
			model.setCls(cls.getName());
			model.setValue(apiModel.value());
			model.setDescription(apiModel.description());
			List<ModelProperty> fields = getModelProperty(cls);
			model.setFields(fields);
			model.setParent(cls.getSuperclass());
			modelMap.put(cls.getName(), model);
			return model;
		}
		return null;
	}

	/**
	 * 获取的实体类属性
	 *
	 * @param cls 具体类
	 */
	public static List<ModelProperty> getModelProperty(Class<?> cls) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String s = objectMapper.writeValueAsString(cls.newInstance());
		Map map = objectMapper.readValue(s, Map.class);
		Map<String, ModelProperty> result = new HashMap<>(map.size());
		for (Object key : map.keySet()) {
			ModelProperty modelProperty = new ModelProperty();
			modelProperty.setName(key.toString());
			modelProperty.setValue(key.toString());
			modelProperty.setNotes(key.toString());
			modelProperty.setDataType(String.class.getName());
			result.put(key.toString(), modelProperty);
		}
		getModelProperty(cls, result);
		return new ArrayList<>(result.values());
	}

	public static Map<String, Model> getModelMap() {
		return modelMap;
	}

	private static void getModelProperty(Class<?> cls, Map<String, ModelProperty> modelPropertyMap) {
		if (cls.getSuperclass() != Object.class) {
			Model model = modelMap.get(cls.getSuperclass().getName());
			if (model == null) {
				getModelProperty(cls.getSuperclass(), modelPropertyMap);
			} else {
				List<ModelProperty> list = model.getFields();
				for (ModelProperty bean : list) {
					ModelProperty modelProperty = modelPropertyMap.get(bean.getName());
					if (modelProperty != null) {
						modelPropertyMap.put(bean.getName(), bean);
					}
				}
			}
		}

		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			ModelProperty modelProperty = modelPropertyMap.get(field.getName());
			if (modelProperty != null) {
				ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
				modelProperty.setDataType(field.getType().getName());
				modelProperty.setName(field.getName());
				apiModelPropertyToBean(apiModelProperty, modelProperty);
			}
		}

		Method[] methods = cls.getDeclaredMethods();
		for (Method method : methods) {
			String name = getMethodField(method.getName());
			ModelProperty modelProperty = modelPropertyMap.get(name);
			boolean flag = Modifier.isPublic(method.getModifiers()) && org.apache.commons.lang3.StringUtils.isNotEmpty(name) && modelProperty != null;
			if (flag) {
				ApiModelProperty apiModelProperty = method.getAnnotation(ApiModelProperty.class);
				modelProperty.setDataType(method.getReturnType().getName());
				modelProperty.setName(name);
				apiModelPropertyToBean(apiModelProperty, modelProperty);
			}
		}
	}

	private static String getMethodField(String name) {
		String result = null;
		if (isGetMethod(name)) {
			if (name.startsWith(METHOD_GET)) {
				result = name.substring(METHOD_GET.length());

			} else if (name.startsWith(METHOD_IS)) {
				result = name.substring(METHOD_IS.length());
			}
			result = StringUtils.toLowerCaseFirstLetter(result);
		}
		return result;
	}

	private static boolean isGetMethod(String name) {
		return name.startsWith(METHOD_GET) || name.startsWith(METHOD_IS);
	}

	private static void apiModelPropertyToBean(ApiModelProperty apiModelProperty, ModelProperty modelProperty) {
		if (apiModelProperty != null) {
			modelProperty.setNotes(apiModelProperty.notes());
			modelProperty.setHidden(apiModelProperty.hidden());
			modelProperty.setReadOnly(apiModelProperty.readOnly());
			modelProperty.setRequired(apiModelProperty.required());
			modelProperty.setValue(apiModelProperty.value());
		}
	}

	public static void getAPIs(String packageName) {
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> classesList = reflections.getTypesAnnotatedWith(Controller.class);
		for (Class classes : classesList) {
			Api api = (Api) classes.getAnnotation(Api.class);
			RequestMapping requestMapping = (RequestMapping) classes.getAnnotation(RequestMapping.class);
			String[] rootPath;
			if (requestMapping != null) {
				rootPath = requestMapping.value();
				for (String bean : rootPath) {
					System.out.println(bean);
				}
			}
			Method[] methods = classes.getMethods();
			if (!isEmpty(methods)) {
				for (Method method : methods) {
					Annotation[] annotations = method.getAnnotations();
					Parameter[] parameters = method.getParameters();
					for (Parameter parameter : parameters) {
						System.out.println(parameter.getName() + ":" + parameter.getType());
					}
				}
			}
		}
	}

}
