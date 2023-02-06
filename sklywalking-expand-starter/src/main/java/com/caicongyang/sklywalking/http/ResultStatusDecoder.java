package com.caicongyang.sklywalking.http;

import com.caicongyang.sklywalking.common.SpanConstant;
import feign.Response;
import feign.codec.Decoder;
import org.apache.commons.io.IOUtils;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * fegin 结果值代理
 */
public class ResultStatusDecoder implements Decoder {
    final Decoder delegate;

    public ResultStatusDecoder(Decoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        // 判断是否返回参数是否是异常
        String resultStr = IOUtils.toString(response.body().asInputStream(), "UTF-8");
        // 拿到返回值，进行自定义逻辑处理
        ActiveSpan.tag("server.response", resultStr);

        // 回写body,因为response的流数据只能读一次，这里回写后重新生成response
        return delegate.decode(response.toBuilder().body(resultStr, StandardCharsets.UTF_8).build(), type);
    }
}


