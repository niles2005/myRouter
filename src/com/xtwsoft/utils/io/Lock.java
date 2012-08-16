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
public interface Lock
{

    public static short SHARED = 1;
    public static short EXCLUSIVE = 2;

    public short getType();

}
