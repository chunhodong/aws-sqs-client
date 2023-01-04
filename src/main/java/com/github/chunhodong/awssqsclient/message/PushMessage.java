package com.github.chunhodong.awssqsclient.message;

import lombok.Getter;

@Getter
public class PushMessage {
    private String title;
    private String body;
    private String imgUrl;
}
