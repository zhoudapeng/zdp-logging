package com.zdp.logging.filter;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.*;
import com.zdp.logging.consts.ParamEnum;
import com.zdp.logging.util.MDCUtil;

import java.util.UUID;

public class DubboLogFilter implements Filter {
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (RpcContext.getContext().isConsumerSide()) {
            String traceId = MDCUtil.get(MDCUtil.Type.TRACE_ID);
            if (StringUtils.isBlank(traceId)) {
                traceId = UUID.randomUUID().toString();
            }
            RpcContext.getContext().setAttachment(ParamEnum.TRACE_ID,traceId);
        }
        if (RpcContext.getContext().isProviderSide()) {
            String traceId = RpcContext.getContext().getAttachment(ParamEnum.TRACE_ID);
            if (StringUtils.isBlank(traceId)) {
                traceId = UUID.randomUUID().toString();
            }
            MDCUtil.put(MDCUtil.Type.TRACE_ID,traceId);
        }
        return invoker.invoke(invocation);
    }
}
