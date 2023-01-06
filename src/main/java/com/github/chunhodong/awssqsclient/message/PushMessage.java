package com.github.chunhodong.awssqsclient.message;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class PushMessage implements Serializable {
    private static final long serialVersionUID = 362498820763181265L;
    private String title;
    private String body;
    private String imgUrl;
}
