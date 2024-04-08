package com.t3q.dranswer.common.trace;

public interface LogTrace {
    TraceStatus begin(String message);

    void end(TraceStatus status);
    void exception(TraceStatus status, Throwable e);
}
