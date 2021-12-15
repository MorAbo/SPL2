package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.locks.*;


public class ReadWriteList<E> {
    private ArrayList<E> list = new ArrayList<>();
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public ReadWriteList(E... initialElements) {
        list.addAll(Arrays.asList(initialElements));
    }

    public void add(E element) {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();

        try {
            list.add(element);
        } finally {
            writeLock.unlock();
        }
    }

    public boolean remove(E element) {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();

        try {
            return list.remove(element);
        } finally {
            writeLock.unlock();
        }
    }

    public int whereIs(E e) {
        Lock readLock = rwLock.readLock();
        readLock.lock();

        try {
            if (list.contains(e)){
                for (int i=0; i< list.size(); i++)
                    if( list.get(i).equals(e) ) return i;}
            return -1;
        } finally {
            readLock.unlock();
        }
    }

    public boolean contains(E e) {
        Lock readLock = rwLock.readLock();
        readLock.lock();

        try {
            return list.contains(e);
        } finally {
            readLock.unlock();
        }
    }

    public E get(int index) {
        Lock readLock = rwLock.readLock();
        readLock.lock();

        try {
            return list.get(index);
        }
        finally {
            readLock.unlock();
        }
    }

    public int size() {
        Lock readLock = rwLock.readLock();
        readLock.lock();

        try {
            return list.size();
        } finally {
            readLock.unlock();
        }
    }

}