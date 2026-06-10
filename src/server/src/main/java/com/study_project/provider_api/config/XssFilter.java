package com.study_project.provider_api.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class XssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);
    }

    private static class XssRequestWrapper extends HttpServletRequestWrapper {
        private final String body;

        public XssRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            // Читаем тело входящего JSON запроса
            String rawBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            // Очистка всего JSON от HTML-тегов с помощью Jsoup
            this.body = Jsoup.clean(rawBody, Safelist.none());
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    body.getBytes(StandardCharsets.UTF_8));
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }

                @Override
                public int read() {
                    return byteArrayInputStream.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
        }
    }
}
