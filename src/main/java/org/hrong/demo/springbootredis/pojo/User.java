package org.hrong.demo.springbootredis.pojo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class User implements Serializable {
	private static final long serialVersionUID = 2603221101368594195L;
	private String userName;
	private Integer age;
	private String gender;
}
