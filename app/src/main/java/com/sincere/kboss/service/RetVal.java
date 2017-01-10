package com.sincere.kboss.service;

/**
 * Created by Michael on 2016.09.23.
 */
public class RetVal {
    public RetVal() {
        code = ServiceParams.ERR_EXCEPTION;
        msg = "";

        intData = 0;
        strData = "";
    }

    public int code;
    public String msg;

    public int intData;
    public String strData;
}
