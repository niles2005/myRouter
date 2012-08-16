/**
 * Copyright(c) 2007 TeleNav, Inc.
 *
 * History:
 *   Mar 1, 2009 1:00:00 PM Created by NieLei
 */
package com.xtwsoft.utils.io;

/**
 *
 * @author NieLei E-mail:lnie@telenav.cn
 * @version 1.0 CreateTime:Mar 1, 2009 1:00:00 PM
 *
 */
public class LockTimeoutException
    extends Exception
{
    public LockTimeoutException() {
        super();
    }

    public LockTimeoutException(String message) {
        super(message);
    }

    public LockTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockTimeoutException(Throwable cause) {
        super(cause);
    }
}
