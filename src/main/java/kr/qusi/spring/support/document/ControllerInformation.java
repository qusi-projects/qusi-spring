package kr.qusi.spring.support.document;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ControllerInformation {

    private String name;

    private String fileName;

    private String description;

    private String author;

    private List<RequestMappingInformation> requestMapping;

    private void setRequestMapping(List<RequestMappingInformation> list) {
        this.requestMapping = list;
    }

    public List<RequestMappingInformation> getRequestMapping() {
        if (requestMapping == null)
            requestMapping = new ArrayList<RequestMappingInformation>();
        return requestMapping;
    }

    public void addRequestMapping(RequestMappingInformation information) {
        this.getRequestMapping().add(information);
    }

}
