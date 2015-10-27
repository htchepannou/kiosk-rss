package com.tchepannou.kiosk.rss.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;

public class S3Helper {
    private final AmazonS3 s3;

    public S3Helper (AmazonS3 s3){
        this.s3 = s3;
    }

    public S3Object get (String bucket, String key){
        GetObjectRequest s3r = new GetObjectRequest(bucket, key);
        return s3.getObject(s3r);
    }

    public ObjectListing list (String bucket, String prefix){
        final ListObjectsRequest s3r = new ListObjectsRequest()
                .withBucketName(bucket)
                .withPrefix(prefix);

        return s3.listObjects(s3r);
    }
}
