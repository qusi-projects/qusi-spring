package kr.qusi.spring.support.document;

import lombok.Data;

@Data
public class RequestMappingInformation {

    private String name;

    private String url;

    private String produces;

    private String consumes;

    private String httpMethod;

    private String description;

    private String author;

}
