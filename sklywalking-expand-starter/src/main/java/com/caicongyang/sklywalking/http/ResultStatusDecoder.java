package com.caicongyang.sklywalking.http;

import feign.Response;
import feign.codec.Decoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * fegin 结果值代理
 */
public class ResultStatusDecoder implements Decoder {
    private static final Logger logger = LoggerFactory.getLogger(ResultStatusDecoder.class);

    final Decoder delegate;

    public ResultStatusDecoder(Decoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        String resultStr = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
        logger.debug("Feign response: {}", resultStr);
        return delegate.decode(response.toBuilder().body(resultStr, StandardCharsets.UTF_8).build(), type);
    }
}
