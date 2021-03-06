package com.github.tegola.mobile.controller;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.github.tegola.mobile.controller.utils.HTTP;
import okio.Buffer;

public class TestHTTPAsyncGetStageHandler extends HTTP.AsyncGet.TaskStageHandler {
    final private String TAG = TestHTTPAsyncGetStageHandler.class.getSimpleName();

    private volatile boolean firstUpdate = true;
    private volatile long content_length = 0;
    private volatile long total_bytes_read = 0;
    private volatile HTTP.AsyncGet.HttpUrl_To_Local_File httpUrl_to_local_file = null;
    private volatile String s_url_remote_file = "<failed to retrieve remote file url>";
    private volatile File local_file = null;
    private volatile String s_local_file = "<failed to retrieve local file path>";
    private volatile FileOutputStream f_outputstream__local_file = null;

    @Override
    public void onPreExecute() {
        Log.d(TAG, "onPreExecute - resetting state vars...");
        firstUpdate = true;
        content_length = 0;
        total_bytes_read = 0;
        httpUrl_to_local_file = get_httpUrl_to_local_file();
        f_outputstream__local_file = null;

        if (httpUrl_to_local_file != null) {
            s_url_remote_file = httpUrl_to_local_file.get_url().toString();
            local_file = httpUrl_to_local_file.get_file();
            if (local_file != null) {
                try {
                    s_local_file = local_file.getCanonicalPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d(
            TAG,
            String.format(
                "onPreExecute - starting download of %s to %s",
                s_url_remote_file,
                s_local_file
            )
        );
    }

    @Override
    public void onChunkRead(Buffer sink, long bytesRead, long contentLength, boolean done) throws IOException {
        if (bytesRead > -1) {
            Log.d(TAG, String.format("onChunkRead: %d bytesRead, %s contentLength", bytesRead, contentLength));
        }

        try {
            if (!done) {
                if (firstUpdate) {
                    Log.d(TAG, "onChunkRead: firstUpdate==true");
                    firstUpdate = false;

                    if (httpUrl_to_local_file == null) {
                        httpUrl_to_local_file = get_httpUrl_to_local_file();
                        s_url_remote_file = httpUrl_to_local_file.get_url().toString();
                        local_file = httpUrl_to_local_file.get_file();
                        if (local_file != null) {
                            try {
                                s_local_file = local_file.getCanonicalPath();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (contentLength < 1) {
                        Log.d(TAG, "onChunkRead: *** WARNING!!! *** - contentLength < 1");
                    }
                    content_length = contentLength;
                    Log.d(
                        TAG,
                        String.format(
                            "onChunkRead: content_length==%d",
                            content_length
                        )
                    );
                    if (local_file.exists()) {
                        Log.d(
                            TAG,
                            String.format(
                                "onChunkRead: local file %s already exists",
                                s_local_file
                            )
                        );
                        throw new HTTP.AsyncGet.StageHandlerOnChunkRead_LocalFileAlreadyExistsException(httpUrl_to_local_file);
                    } else {
                        Log.d(
                            TAG,
                            String.format(
                                "onChunkRead: local file %s does not exist",
                                s_local_file
                            )
                        );
                        if (!local_file.getParentFile().exists()) {
                            Log.d(
                                TAG,
                                String.format(
                                    "onChunkRead: local file directory %s does not exist; creating...",
                                    local_file.getParentFile().getCanonicalPath()
                                )
                            );
                            local_file.getParentFile().mkdirs();
                        }
                        boolean created_file = local_file.createNewFile();
                        Log.d(
                            TAG,
                            String.format(
                                "onChunkRead: %s new local file %s; opening outputstream",
                                (created_file ? "Succcessfully created" : "Failed to create"),
                                s_local_file
                            )
                        );
                        f_outputstream__local_file = new FileOutputStream(local_file);
                    }
                    Log.d(
                        TAG,
                        String.format(
                            "onChunkRead: downloading/writing %s outputstream to %s...",
                            s_url_remote_file,
                            s_local_file
                        )
                    );
                }

                //write bytes to outputstream
                //Log.d(TAG, "onChunkRead: sink.size()==" + sink.size());
                sink.copyTo(f_outputstream__local_file);
                f_outputstream__local_file.flush();

                total_bytes_read += bytesRead;
                //Log.d(TAG, "onChunkRead: total_bytes_read==" + total_bytes_read);

                //now update progress
                if (content_length > 0) {
                    final double dbl_progress = ((double) total_bytes_read * 1.0) / ((double) content_length * 1.0);
                    //Log.d(TAG, "onChunkRead: (double) progress ratio: " + dbl_progress);
                    final int i_progress = (int) (dbl_progress * 100.0);
                    //Log.d(TAG, "onChunkRead: updating progress to " + i_progress + "%");
                } else {
                    Log.d(
                        TAG,
                        String.format(
                            "onChunkRead: cannot update interim progress for %s to %s download since content_length==%d",
                            s_url_remote_file,
                            s_local_file,
                            content_length
                        )
                    );
                }
            } else {//done
                Log.d(
                    TAG,
                    String.format(
                        "onChunkRead: done; wrote: %d bytes to %s",
                        total_bytes_read,
                        s_local_file
                    )
                );
                if (f_outputstream__local_file != null) {
                    Log.d(
                        TAG,
                        String.format(
                            "onChunkRead: Closing fileoutputstream for %s",
                            s_local_file
                        )
                    );
                    f_outputstream__local_file.close();
                } else {
                    Log.d(
                        TAG,
                        String.format(
                            "onChunkRead: Cannot close null fileoutputstream for %s",
                            s_local_file
                        )
                    );
                }
            }
        } catch (HTTP.AsyncGet.StageHandlerOnChunkRead_LocalFileAlreadyExistsException e) {
            throw e;
        } catch (FileNotFoundException e) {
            HTTP.AsyncGet.StageHandlerOnChunkRead_LocalFileCreateException lfce = new HTTP.AsyncGet.StageHandlerOnChunkRead_LocalFileCreateException(httpUrl_to_local_file, e.getMessage());
            throw lfce;
        } catch (IOException e) {
            HTTP.AsyncGet.StageHandlerOnChunkRead_GeneralIOException gioe = new HTTP.AsyncGet.StageHandlerOnChunkRead_GeneralIOException(httpUrl_to_local_file, e.getMessage());
            throw gioe;
        }
    }

    @Override
    public void onCancelled(Exception exception) {
        if (exception != null) {
            Log.d(
                TAG,
                String.format(
                    "onCancelled - exception: %s",
                    exception
                )
            );
        }
        Log.d(
            TAG,
            String.format(
                "onCancelled - download of %s to %s cancelled",
                s_url_remote_file,
                s_local_file
            )
        );
    }

    @Override
    public void onPostExecute(Exception exception) {
        if (exception != null) {
            Log.d(
                TAG,
                String.format(
                    "onPostExecute - exception: %s",
                    exception
                )
            );
        }
        Log.d(
            TAG,
            String.format(
                "onPostExecute - download of %s to %s complete",
                s_url_remote_file,
                s_local_file
            )
        );
    }
}
