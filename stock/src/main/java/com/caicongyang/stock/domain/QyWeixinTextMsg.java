package com.caicongyang.stock.domain;

import java.util.List;

public class QyWeixinTextMsg extends QyWeixinMsg {


    private QyWeixinTextMsgContext text;

    public QyWeixinTextMsgContext getText() {
        return text;
    }

    public void setText(QyWeixinTextMsgContext text) {
        this.text = text;
    }


    public static class QyWeixinTextMsgContext {
        private String content;

        private List<String> mentioned_list;

        private List<String> mentioned_mobile_list;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<String> getMentioned_list() {
            return mentioned_list;
        }

        public void setMentioned_list(List<String> mentioned_list) {
            this.mentioned_list = mentioned_list;
        }

        public List<String> getMentioned_mobile_list() {
            return mentioned_mobile_list;
        }

        public void setMentioned_mobile_list(List<String> mentioned_mobile_list) {
            this.mentioned_mobile_list = mentioned_mobile_list;
        }
    }

}

