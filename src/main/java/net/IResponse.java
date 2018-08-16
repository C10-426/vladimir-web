/*
 * <p>Title: IResponse</p>
 *
 * <p>Description: </p>
 *    网络响应接口
 * <p/>
 * <p>Copyright (c) 2015-present, Alibaba, Inc. All rights reserved. </p>
 * @author Junkun <junkun.hjk@alibaba-inc.com>
 * @date 17-6-14 下午6:47.
 *
 */

package net;

public interface IResponse {

    int getResponseCode();

    interface Factory {
        IResponse create(int responseCode, byte[] data);
    }
}
