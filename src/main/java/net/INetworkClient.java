package net;

import java.util.concurrent.Future;

public interface INetworkClient {
    Future<IResponse> submit(IRequest request, Priority priority);

    void submit(IRequest request, Priority priority, INetworkCallBack networkCallBack);

    Future<IResponse> submit(IRequest request);

    void submit(IRequest request, INetworkCallBack networkCallBack);

    enum Priority {
        High(1),
        Mid(2),
        Low(3);

        private int value;
        Priority(int value) {
            this.value = value;
        }

        public final int value() {
            return this.value;
        }
    }

    public interface INetworkCallBack {

        void onResponse(IRequest request, IResponse response);

        void onException(Exception e);
    }
}