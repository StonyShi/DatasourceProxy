package com.stone.db.proxy.test.db;

import com.stone.db.proxy.test.annotations.*;

/**
 * <p>DatasourceProxy
 * <p>com.stone.db.proxy.test
 *
 * @author stony
 * @version 上午10:51
 * @since 2018/12/20
 */
@DB
public interface Cat extends Animal{

    @UpdateSql("update cat set say=say+1")
    void say();
}
