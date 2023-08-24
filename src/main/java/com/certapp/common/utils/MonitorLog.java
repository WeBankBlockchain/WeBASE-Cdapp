package com.certapp.common.utils;


public class MonitorLog {
    private String status;// OnSuccess,OnError,start
    private String methodName;// 业务码、方法名
    private String bizSeqNo;// 请求流水号
    private String id;// Id
    private String res_code;// 错误号、返回码
    private Long usedTime;// 方法耗时(为监控日志时，打印耗时)
    private String data;// 返回结果

    public MonitorLog(String status, String methodName, String bizSeqNo, String id,
                      String res_code, Long usedTime, String data) {
        this.status = status;
        this.methodName = methodName;
        this.bizSeqNo = bizSeqNo;
        this.id = id;
        this.res_code = res_code;
        this.usedTime = usedTime;
        this.data = data;
    }

    public static String monitorStr(String status, String methodName, String bizSeqNo,
            String data) {
        return new MonitorLog(status, methodName, bizSeqNo, null, null, null, data).toString();
    }

    public static String monitorStr(String status, String methodName, String bizSeqNo,
            String id, String data) {
        return new MonitorLog(status, methodName, bizSeqNo, id, null, null, data).toString();
    }

    public static String monitorStr(String status, String methodName, String bizSeqNo,
            String id, String res_code, Long usedTime, String data) {
        return new MonitorLog(status, methodName, bizSeqNo, id, res_code, usedTime, data)
                .toString();
    }

    /**
     * 增加字段需要同步修改 awk -F"|" '{print $4}' | awk -F":" '{print $2}' 输出用户userId的脚本的变量下表
     * 
     * @return
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        sb.append("status:").append(status).append('|');
        sb.append("methodName:").append(methodName).append('|');
        sb.append("bizSeqNo:").append(bizSeqNo).append('|');
        sb.append("id:").append(id).append('|');
        sb.append("res_code:").append(res_code).append('|');
        sb.append("usedTime:").append(usedTime).append('|');
        sb.append("data:").append(data);
        sb.append("]");
        return sb.toString();
    }
}
