package com.stone.db.proxy.test.db;

import com.stone.db.proxy.test.annotations.DB;
import com.stone.db.proxy.test.annotations.UpdateSql;

/**
 * <p>DatasourceProxy
 * <p>com.stone.db.proxy.test
 *
 * @author stony
 * @version 上午10:57
 * @since 2018/12/20
 */
@DB
public interface Dog extends Animal{

    @UpdateSql("update dog set say=say+1")
    void say();
}
