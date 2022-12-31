package com.github.chunhodong.awssqsclient.client;

public class AwsSQSClientTemplate {

    public AwsSQSClientTemplate(AwsSQSClientTemplateBuilder builder) {

    }

    public static AwsSQSClientTemplateBuilder builder() {
        return new AwsSQSClientTemplateBuilder();
    }

    public static class AwsSQSClientTemplateBuilder {

        private Integer maxPoolSize;
        private String channel;

        public AwsSQSClientTemplateBuilder maxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        public AwsSQSClientTemplateBuilder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public AwsSQSClientTemplate build() {
            return new AwsSQSClientTemplate(this);
        }
    }
}
