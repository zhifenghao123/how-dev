package com.howdev.framework.sqlcalculate.jdbc.udf;

import java.util.ArrayList;
import java.util.List;

/**
 * UdfRegistrar class
 *
 * @author haozhifeng
 * @date 2023/12/12
 */
public class UdfRegistrar {
    /**
     * 注册的UDF
     */
    private List<Udf> udfs;

    /**
     * 注册UDF
     *
     * @param udf udf
     * @return:
     * @author: haozhifeng
     */
    public void registerUdf(Udf udf) {
        if (null == this.udfs) {
            this.udfs = new ArrayList<>();
        }
        this.udfs.add(udf);
    }

    /**
     * 获取注册的UDF
     *
     * @param
     * @return:
     * @author: haozhifeng
     */
    public List<Udf> getUdfs() {
        return this.udfs;
    }
}
