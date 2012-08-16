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
public class LockManager
{
    private static final int EXCLUSIVE_LOCK_TIMEOUT = 20;
    private static final int SHARED_LOCK_TIMEOUT = 10;
    public static final short READ = 1;
    public static final short WRITE = 2;
    private Lock exclusiveLock;
    private int leases;

    private static LockManager m_instance = null;

    private LockManager() {
    }

    public static LockManager getInstance() {
        if (m_instance == null) {
            m_instance = new LockManager();
        }
        return m_instance;
    }

    public synchronized void release(Lock lock) {
        LockImpl li = (LockImpl) lock;

        if (li.getType() == Lock.EXCLUSIVE) {
            this.exclusiveLock = null;
        }
        else {
            this.leases--;
        }

        this.notify();
    }

    public synchronized Lock aquireExclusive() throws LockTimeoutException {
        int cnt = 0;

        while (((this.exclusiveLock != null) || (this.leases > 0))
               && (cnt < EXCLUSIVE_LOCK_TIMEOUT)) {
            cnt++;

            try {
                this.wait(500);
            }
            catch (InterruptedException e) {
                throw new LockTimeoutException(e);
            }
        }

        if ((this.exclusiveLock != null) || (this.leases > 0)) {
            throw new LockTimeoutException("Timeout aquiring exclusive lock");
        }

        this.exclusiveLock = new LockImpl(Lock.EXCLUSIVE);

        return this.exclusiveLock;
    }

    public synchronized Lock aquireShared() throws LockTimeoutException {
        int cnt = 0;

        while ((this.exclusiveLock != null) && (cnt < SHARED_LOCK_TIMEOUT)) {
            cnt++;

            try {
                this.wait(500);
            }
            catch (InterruptedException e) {
                throw new LockTimeoutException(e);
            }
        }

        if (this.exclusiveLock != null) {
            throw new LockTimeoutException("Timeout aquiring shared lock");
        }

        this.leases++;

        return new LockImpl(Lock.SHARED);
    }

    private class LockImpl
        implements Lock
    {
        private short type;

        public LockImpl(short type) {
            this.type = type;
        }

        public short getType() {
            return this.type;
        }
    }
}
