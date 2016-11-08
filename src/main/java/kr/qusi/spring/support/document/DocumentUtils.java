package kr.qusi.spring.support.document;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 문서작성 유틸
 *
 * @author yongseoklee
 * @since 1.0.0
 */
public class DocumentUtils {

    public static List<ControllerInformation> getControllerComments(ApplicationContext context) throws Exception {
        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();

        Map<String, ControllerInformation> controllers = new LinkedHashMap<String, ControllerInformation>();

        for (RequestMappingInfo key : handlerMethods.keySet()) {
            HandlerMethod handlerMethod = handlerMethods.get(key);
            Class<?> beanType = handlerMethod.getBeanType();
            String controllerName = beanType.getSimpleName();

            ControllerDocument controllerDocument = beanType.getAnnotation(ControllerDocument.class);
            RequestMappingDocument requestMappingDocument = handlerMethod.getMethod()
                    .getAnnotation(RequestMappingDocument.class);

            // 컨트롤러 정보
            ControllerInformation cInfo = controllers.get(controllerName);

            if (cInfo == null) {
                cInfo = new ControllerInformation();
                cInfo.setName(controllerName);
                cInfo.setFileName(beanType.getName());
                cInfo.setAuthor(getAuthor(controllerDocument, null));
                cInfo.setDescription(getDescription(controllerDocument));
            }

            // RequestMapping 정보
            RequestMappingInformation rInfo = new RequestMappingInformation();
            rInfo.setName(handlerMethod.getMethod().getName());
            rInfo.setUrl(replaceArrayChar(key.getPatternsCondition()));
            rInfo.setProduces(replaceArrayChar(key.getProducesCondition()));
            rInfo.setConsumes(replaceArrayChar(key.getConsumesCondition()));
            rInfo.setHttpMethod(replaceArrayChar(key.getMethodsCondition()));
            rInfo.setAuthor(getAuthor(controllerDocument, requestMappingDocument));
            rInfo.setDescription(getDescription(requestMappingDocument));

            cInfo.addRequestMapping(rInfo);

            controllers.put(controllerName, cInfo);
        }

        return new ArrayList<ControllerInformation>(controllers.values());
    }

    public static String getAuthor(ControllerDocument controllerDocument,
                                   RequestMappingDocument requestMappingDocument) {
        if (requestMappingDocument != null && StringUtils.isNotEmpty(requestMappingDocument.author()))
            return requestMappingDocument.author();

        if (controllerDocument != null && StringUtils.isNotEmpty(controllerDocument.author()))
            return controllerDocument.author();

        return "";
    }

    public static String getDescription(RequestMappingDocument requestMappingDocument) {
        if (requestMappingDocument != null && StringUtils.isNotEmpty(requestMappingDocument.description()))
            return requestMappingDocument.description();

        return "";
    }

    public static String getDescription(ControllerDocument controllerDocument) {
        if (controllerDocument != null && StringUtils.isNotEmpty(controllerDocument.description()))
            return controllerDocument.description();

        return "";
    }

    public static String replaceArrayChar(Object src) {
        if (src == null)
            return "";

        String dest = src.toString();

        dest = StringUtils.replace(dest, "[", "");
        dest = StringUtils.replace(dest, "]", "");
        dest = StringUtils.replace(dest, " ||", ",");

        return dest;
    }

}