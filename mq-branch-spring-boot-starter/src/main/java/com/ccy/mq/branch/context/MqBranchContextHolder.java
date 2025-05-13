package com.ccy.mq.branch.context;

public class MqBranchContextHolder {
    // 线程局部变量
    private static final ThreadLocal<String> BRANCH_CONTEXT_HOLDER = new ThreadLocal<>();

    // 往线程里边set分支
    public static void set(String branch)
    {
        if (branch == null)
        {
            throw new NullPointerException();
        }
        BRANCH_CONTEXT_HOLDER.set(branch);
    }

    /**
     * 容器中获取分支
     *
     * @return
     */
    public static String get()
    {
        return BRANCH_CONTEXT_HOLDER.get();
    }

    /**
     * 清空容器中的分支
     */
    public static void remove()
    {
        BRANCH_CONTEXT_HOLDER.remove();
    }
}
