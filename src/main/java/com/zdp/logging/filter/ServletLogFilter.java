package com.zdp.logging.filter;

import com.zdp.logging.consts.ParamEnum;
import com.zdp.logging.util.MDCUtil;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

public class ServletLogFilter implements Filter{
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String traceId = servletRequest.getParameter(ParamEnum.TRACE_ID);
        if (StringUtils.isEmpty(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        MDCUtil.put(MDCUtil.Type.TRACE_ID, traceId);
        filterChain.doFilter(servletRequest,servletResponse);
        MDCUtil.clear();
    }

    public void destroy() {

    }
}
