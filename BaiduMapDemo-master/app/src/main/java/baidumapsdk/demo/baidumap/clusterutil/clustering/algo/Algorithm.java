/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */

package baidumapsdk.demo.baidumap.clusterutil.clustering.algo;



import java.util.Collection;
import java.util.Set;

import baidumapsdk.demo.baidumap.clusterutil.clustering.Cluster;
import baidumapsdk.demo.baidumap.clusterutil.clustering.ClusterItem;

/**
 * Logic for computing clusters
 */
public interface Algorithm<T extends ClusterItem> {
    void addItem(T item);

    void addItems(Collection<T> items);

    void clearItems();

    void removeItem(T item);

    Set<? extends Cluster<T>> getClusters(double zoom);

    Collection<T> getItems();
}