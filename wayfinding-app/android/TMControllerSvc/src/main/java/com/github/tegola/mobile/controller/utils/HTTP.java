package com.github.tegola.mobile.controller.utils;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.ConnectionSpec;
import okhttp3.Dispatcher;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class HTTP {
    private final static String TAG = HTTP.class.getCanonicalName();

    public static boolean isValidUrl(String url) {
        if (url.contains("/")) {
            Pattern p = Patterns.WEB_URL;
            Matcher m = p.matcher(url.toLowerCase());
            return m.matches();
        } else
            return false;
    }

    private static Dispatcher newDefaultDispatcher() {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(20);
        return dispatcher;
    }

    private static X509TrustManager newDefaultX509TrustManager() {
        TrustManagerFactory trustManagerFactory = null;
        X509TrustManager trustManager = null;
        try {
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore)null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            Log.d(TAG, String.format("newDefaultX509TrustManager: trustManagerFactory.getTrustManagers(): %s", Arrays.toString(trustManagers)));
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager))
                throw new IllegalStateException(String.format("Unexpected default trust managers: %s", Arrays.toString(trustManagers)));
            trustManager = (X509TrustManager)trustManagers[0];
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trustManager;
    }

    private static X509TrustManager newX509TrustManagerBypass() {
        return new X509TrustManager() {
            private final String TAG = "X509TrustManagerBypass";

            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                Log.d(TAG, "checkClientTrusted: no-op for bypass");
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                Log.d(TAG, "checkServerTrusted: no-op for bypass");
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                Log.d(TAG, "getAcceptedIssuers: returning java.security.cert.X509Certificate[]{} for bypass");
                return new X509Certificate[]{};
            }
        };
    }

    private static class SSLConfig {
        SSLContext sslContext = null;
        KeyManager[] keyManagers = null;
        TrustManager[] trustManagers = null;
        SecureRandom random = null;
    }
    private static SSLConfig newSSLConfig(final String sslprotocol, KeyManager[] keyManagers, final TrustManager[] trustManagers, final SecureRandom random) {
        SSLConfig sslConfig = new SSLConfig();
        sslConfig.keyManagers = keyManagers;
        sslConfig.trustManagers = trustManagers;
        sslConfig.random = random;
        try {
            sslConfig.sslContext = SSLContext.getInstance(sslprotocol);   //see https://developer.android.com/reference/javax/net/ssl/SSLContext.html#getInstance(java.lang.String) for list of canonical protocol strings
            Log.d(TAG, String.format("newSSLConfig: sslContext.getProtocol(): %s", sslConfig.sslContext.getProtocol()));
            sslConfig.sslContext.init(keyManagers, trustManagers, random);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslConfig;
    }
    private static SSLConfig newSSLConfig(final String sslprotocol) {
        return newSSLConfig(sslprotocol, null, new TrustManager[]{newDefaultX509TrustManager()}, null);
    }

    private static HostnameVerifier newDefaultHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
    }

    private static OkHttpClient.Builder newDefaultHttpClientBuilder(final boolean useSSL) {
        final OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
            .dispatcher(newDefaultDispatcher())
            .protocols(new ArrayList<Protocol>(){{add(Protocol.HTTP_1_1);}})    //restrict to HTTP 1.1
            .readTimeout(20, TimeUnit.SECONDS);
        if (useSSL) {
            try {
                SSLConfig sslConfig = newSSLConfig("SSL");
                httpClientBuilder.sslSocketFactory(sslConfig.sslContext.getSocketFactory(), (X509TrustManager)sslConfig.trustManagers[0]);
                httpClientBuilder.hostnameVerifier(newDefaultHostnameVerifier());
                httpClientBuilder.connectionSpecs(new ArrayList<ConnectionSpec>(){{add(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).build());}});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return httpClientBuilder;
    }
    private static OkHttpClient.Builder newDefaultHttpClientBuilder() {
        return newDefaultHttpClientBuilder(false);
    }

    private static OkHttpClient newDefaultHttpClient() {
        return newDefaultHttpClientBuilder().build();
    }

    public static class Get {
        private final static String TAG = Get.class.getCanonicalName();

        public interface ContentHandler {
            void onStartRead(long n_size);
            void onChunkRead(int n_bytes_read, byte[] bytes_1kb_chunk);
            void onReadError(long n_remaining, Exception e);
            void onReadComplete(long n_read, long n_remaining);
        }

        public static void exec(final String s_url, final ContentHandler content_handler) {
            if (s_url == null)
                throw new NullPointerException("s_url cannot be null");
            final OkHttpClient httpClient = newDefaultHttpClient();
            final Request http_get_request = new Request.Builder()
                    .url(s_url)
                    .get()
                    .build();
            Log.d(TAG, "exec: new Request created: " + http_get_request.toString());
            Headers request_headers = http_get_request.headers();
            if (request_headers != null && request_headers.size() > 0) {
                Log.d(TAG, "\tHeaders:");
                for (int i = 0; i < request_headers.size(); i++) {
                    String s_hdr_name = request_headers.name(i);
                    String s_hdr_val = request_headers.get(s_hdr_name);
                    Log.d(TAG, "\t\t" + s_hdr_name + ": " + s_hdr_val);
                }
            }
            final Call httpClient_call__get_request = httpClient.newCall(http_get_request);
            Log.d(TAG, "exec: new OkHttpClient Call (for " + http_get_request.toString() + ") created: " + httpClient_call__get_request.toString() + "; executing...");
            Response htt_get_response = null;
            long n_content_length = 0, n_bytes_remaining = 0;
            try {
                htt_get_response = httpClient_call__get_request.execute();
                Log.d(TAG, "exec: executed OkHttpClient Call (" + httpClient_call__get_request.toString() + "); handling response...");
                if (!htt_get_response.isSuccessful())
                    throw new IOException("Unexpected Response (to " + http_get_request.toString() + "): " + htt_get_response.toString());
                else {
                    Log.d(TAG, "exec: Response (to " + http_get_request.toString() + "): " + htt_get_response.toString());
                    Headers response_headers = htt_get_response.headers();
                    if (response_headers != null && response_headers.size() > 0) {
                        //DEBUG - remove for release (unnecessary iteration for release builds)
                        Log.d(TAG, "\tHeaders:");
                        for (int i = 0; i < response_headers.size(); i++) {
                            String s_hdr_name = response_headers.name(i);
                            String s_hdr_val = response_headers.get(s_hdr_name);
                            Log.d(TAG, "\t\t" + s_hdr_name + ": " + s_hdr_val);
                        }
                    }

                    n_content_length = n_bytes_remaining = htt_get_response.body().contentLength();
                    content_handler.onStartRead(n_bytes_remaining);
                    if (n_content_length < 0) {
                        Log.w(TAG, "exec: response headers do not appear to contain Content-Length!");
                        n_bytes_remaining = 0;
                    } else {
                        Log.d(TAG, "exec: reading ResponseBody (" + n_content_length + " bytes)...");
                    }

                    byte[] bytes = new byte[8192];
                    int n_bytes_read = 0;
                    Log.d(TAG, "exec: entering response.body().source().read() loop...");
                    while ((n_bytes_read = htt_get_response.body().source().read(bytes)) != -1) {
                        content_handler.onChunkRead(n_bytes_read, bytes);
                        n_bytes_remaining -= n_bytes_read;
                    }

                    content_handler.onReadComplete(
                            n_content_length < 0
                                    ? -n_bytes_remaining
                                    : n_content_length - n_bytes_remaining
                            , n_content_length < 0
                                    ? 0
                                    : n_bytes_remaining);
                }
            } catch (Exception e) {
                content_handler.onReadError(
                        n_content_length < 0
                            ? 0
                            : n_bytes_remaining
                        , e);
            } finally {
                if (httpClient_call__get_request != null)
                    httpClient_call__get_request.cancel();
                if (htt_get_response != null) {
                    Log.d(TAG, "exec: closing response (to " + http_get_request.toString() + ")");
                    htt_get_response.body().close();
                    htt_get_response.close();
                } else {
                    Log.d(TAG, "exec: cannot close response (to " + http_get_request.toString() + ") as response is null");
                }
                if (httpClient != null) {
                    Log.d(TAG, "exec: httpClient.dispatcher().cancelAll()");
                    httpClient.dispatcher().cancelAll();
                }
            }
        }
    }

    public static class AsyncGet {
        public static class HttpUrl_To_Local_File {
            private final HttpUrl url;
            public final HttpUrl get_url() {return url;}

            private final File file;
            public File get_file() {return file;}

            public HttpUrl_To_Local_File(@NonNull final HttpUrl url, @NonNull final File file) {
                this.url = url;
                this.file = file;
            }
        }

        public static class Task extends AsyncTask<HttpUrl_To_Local_File, Void, Exception> {
            private final static String TAG = "Utils.HTTP.AsyncGet" + Task.class.getSimpleName();

            private final TaskStageHandler m_asyncgetfiletask_stage_handler;

            private Call call__http_get = null;
            public Call get_http_get_call() {return call__http_get;}

            public Task(@NonNull final AsyncGet.TaskStageHandler asyncgetfiletask_stage_handler) {
                m_asyncgetfiletask_stage_handler = asyncgetfiletask_stage_handler;
                asyncgetfiletask_stage_handler.set_asyncgetfiletask(this);
            }

            private static class ChunkedResponseBody extends ResponseBody {
                private final static String TAG = "Utils.HTTP.AsyncGet" + ChunkedResponseBody.class.getSimpleName();

                private final ResponseBody m_responseBody;
                private final TaskStageHandler m_asyncgetfiletask_stage_handler;
                private BufferedSource m_bufferedSource;

                ChunkedResponseBody(@NonNull final ResponseBody responseBody, @NonNull final AsyncGet.HttpUrl_To_Local_File httpUrl_to_local_file, @NonNull final AsyncGet.TaskStageHandler asyncgetfiletask_stage_handler) {
                    m_responseBody = responseBody;
                    asyncgetfiletask_stage_handler.set_httpUrl_to_local_file(httpUrl_to_local_file);
                    m_asyncgetfiletask_stage_handler = asyncgetfiletask_stage_handler;
                }

                @Override
                public MediaType contentType() {
                    return m_responseBody.contentType();
                }

                @Override
                public long contentLength() {
                    return m_responseBody.contentLength();
                }

                @Override
                public BufferedSource source() {
                    if (m_bufferedSource == null) {
                        Log.d(TAG, "source: Okio buffering ResponseBody source (contentLength " + contentLength() + ")");
                        m_bufferedSource = Okio.buffer(source(m_responseBody.source()));
                    } else {
//                        Log.d(TAG, "NOT using Okio to buffer source(m_responseBody.source())..");
                    }
                    return m_bufferedSource;
                }

                private Source source(Source source) {
                    final Source source_ret = new ForwardingSource(source) {
                        @Override
                        public long read(Buffer sink, long byteCount) throws IOException {
                            long bytesRead = super.read(sink, byteCount);   // read() returns the number of bytes read, or -1 if this source is exhausted.
                            //Log.d(TAG, "ForwardingSource::read() - read " + bytesRead + " bytes into sink; assert sink.size()==" + bytesRead + " --> " + (sink.size() == bytesRead) + "; calling m_asyncgetfiletask_stage_handler.onChunkRead()...");
                            m_asyncgetfiletask_stage_handler.onChunkRead(sink, bytesRead, m_responseBody.contentLength(), bytesRead == -1);
                            sink.flush();
                            return bytesRead;
                        }
                    };
                    Log.d(TAG, "source: returning new ForwardingSource w/ our own read() override");
                    return source_ret;
                }
            }


            @Override
            protected void onPreExecute() {
                m_asyncgetfiletask_stage_handler.onPreExecute();
            }

            private long request_file_size(@NonNull final OkHttpClient httpClient, @NonNull final HttpUrl http_url) throws IOException, RemoteFile_SizeException {
                long l_file_size = 0;
                final Request http_request_file_size = new Request.Builder()
                        .url(http_url)
                        .get()
                        .addHeader("Range", "bytes=0-")
                        .build();
                Log.d(TAG, "request_file_size: new Request created: " + http_request_file_size.toString());
                Headers request_headers = http_request_file_size.headers();
                if (request_headers != null && request_headers.size() > 0) {
                    Log.d(TAG, "\tHeaders:");
                    for (int i = 0; i < request_headers.size(); i++) {
                        String s_hdr_name = request_headers.name(i);
                        String s_hdr_val = request_headers.get(s_hdr_name);
                        Log.d(TAG, "\t\t" + s_hdr_name + ": " + s_hdr_val);
                    }
                }
                call__http_get = httpClient.newCall(http_request_file_size);
                Log.d(TAG, "request_file_size: new OkHttpClient Call (for " + http_request_file_size.toString() + ") created: " + http_request_file_size.toString() + "; executing...");
                Response response = null;
                try {
                    response = call__http_get.execute();
                    Log.d(TAG, "request_file_size: executed OkHttpClient Call (" + call__http_get.toString() + "); handling response...");
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected Response (to " + http_request_file_size.toString() + "): " + response.toString());
                    else {
                        Log.d(TAG, "Response (to " + http_request_file_size.toString() + "): " + response.toString());
                        Headers response_headers = response.headers();
                        if (response_headers != null && response_headers.size() > 0) {
                            //DEBUG - remove for release (unnecessary iteration for release builds)
                            Log.d(TAG, "\tHeaders:");
                            for (int i = 0; i < response_headers.size(); i++) {
                                String s_hdr_name = response_headers.name(i);
                                String s_hdr_val = response_headers.get(s_hdr_name);
                                Log.d(TAG, "\t\t" + s_hdr_name + ": " + s_hdr_val);
                            }
                            String s_content_length = response_headers.get("Content-Length");
                            if (s_content_length == null) {
                                throw new RemoteFile_SizeException(http_url, "no Content-Length header");
                            }
                            try {
                                s_content_length = s_content_length.trim();
                                Long l_content_length = Long.parseLong(s_content_length);
                                if (l_content_length == null)
                                    throw new RemoteFile_SizeException(http_url, "invalid \"Content-Length value\": \"" + s_content_length + "\"");
                                l_file_size = l_content_length.longValue();
                            } catch (NumberFormatException e) {
                                throw new RemoteFile_SizeException(http_url, "invalid \"Content-Length value\": \"" + s_content_length + "\"");
                            }
                        }
                    }
                } finally {
                    if (call__http_get != null)
                        call__http_get.cancel();
                    if (response != null) {
                        response.body().source().buffer().close();
                        response.body().close();
                        response.close();
                    }
                }
                return l_file_size;
            }

            private void download_file(@NonNull final Response response) throws IOException {
                if (response.body().contentLength() > 0) {
                    Log.d(TAG, "download_file: downloading ChunkedResponseBody (" + response.body().contentLength() + " bytes)...");
                    byte[] bytes = new byte[1024];
                    int n_bytes_read = 0;
                    long n_bytes_remaining = response.body().contentLength();
                    Log.d(TAG, "download_file: entering response.body().source().read() loop...");
                    while (true) {
                        n_bytes_read = response.body().source().read(bytes);
                        if (n_bytes_read != -1) {
                            //Log.d(TAG, "download_file: response.body().source().read() loop: read next " + n_bytes_read + " byte-chunk of " + n_bytes_remaining + " bytes remaining");
                            n_bytes_remaining -= n_bytes_read;
                        } else {
                            if (n_bytes_remaining > 0) {
                                Log.e(TAG, "download_file: response.body().source().read() loop: n_bytes_read==-1 --> failed to read, n_bytes_remaining==" + n_bytes_remaining);
                            } else {
                                Log.d(TAG, "download_file: response.body().source().read() loop: n_bytes_read==-1 --> breaking out of loop; n_bytes_remaining==0");
                            }
                            break;
                        }
                    }
                } else {
                    Log.e(TAG, "download_file: response body contains no content (content-length/bytes: " + response.body().contentLength() + ")!");
                }
            }

            private void request_file_download(@NonNull final OkHttpClient httpClient, @NonNull final HttpUrl http_url, final long l_byte_offset, final long l_byte_length) throws IOException {
                final Request http_request_file_download = new Request.Builder()
                        .url(http_url)
                        .get()
                        .addHeader("Range", "bytes=" + l_byte_offset + "-" + (l_byte_offset + l_byte_length - 1))
                        .build();
                Log.d(TAG, "request_file_download: new Request created: " + http_request_file_download.toString());
                Headers request_headers = http_request_file_download.headers();
                if (request_headers != null && request_headers.size() > 0) {
                    Log.d(TAG, "\tHeaders:");
                    for (int i = 0; i < request_headers.size(); i++) {
                        String s_hdr_name = request_headers.name(i);
                        String s_hdr_val = request_headers.get(s_hdr_name);
                        Log.d(TAG, "\t\t" + s_hdr_name + ": " + s_hdr_val);
                    }
                }
                call__http_get = httpClient.newCall(http_request_file_download);
                Log.d(TAG, "request_file_download: new OkHttpClient Call (for " + http_request_file_download.toString() + ") created: " + http_request_file_download.toString() + "; executing...");
                Response response = null;
                try {
                    response = call__http_get.execute();
                    Log.d(TAG, "request_file_download: executed OkHttpClient Call (" + call__http_get.toString() + "); handling response...");
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected Response (to " + http_request_file_download.toString() + "): " + response.toString());
                    else {
                        Log.d(TAG, "request_file_download: Response (to " + http_request_file_download.toString() + "): " + response.toString());
                        Headers response_headers = response.headers();
                        if (response_headers != null && response_headers.size() > 0) {
                            //DEBUG - remove for release (unnecessary iteration for release builds)
                            Log.d(TAG, "\tHeaders:");
                            for (int i = 0; i < response_headers.size(); i++) {
                                String s_hdr_name = response_headers.name(i);
                                String s_hdr_val = response_headers.get(s_hdr_name);
                                Log.d(TAG, "\t\t" + s_hdr_name + ": " + s_hdr_val);
                            }
                        }
                        Log.d(TAG, "request_file_download: Response (to " + http_request_file_download.toString() + ") body has " + response.body().byteStream().available() + " bytes available");
                    }
                } finally {
                    if (call__http_get != null)
                        call__http_get.cancel();
                    if (response != null) {
                        Log.d(TAG, "request_file_download: closing response (to " + http_request_file_download.toString() + ")");
                        response.body().source().buffer().close();
                        response.body().close();
                        response.close();
                    } else {
                        Log.d(TAG, "request_file_download: cannot close response (to " + http_request_file_download.toString() + ") as response is null");
                    }
                }
            }
            private void request_file_download(@NonNull final OkHttpClient httpClient, @NonNull final HttpUrl http_url) throws IOException {
                final Request http_request_file_download = new Request.Builder()
                        .url(http_url)
                        .get()
                        .addHeader("Content-Type", "application/octet-stream")
                        .build();
                Log.d(TAG, "request_file_download: new Request created: " + http_request_file_download.toString());
                Headers request_headers = http_request_file_download.headers();
                if (request_headers != null && request_headers.size() > 0) {
                    Log.d(TAG, "\tHeaders:");
                    for (int i = 0; i < request_headers.size(); i++) {
                        String s_hdr_name = request_headers.name(i);
                        String s_hdr_val = request_headers.get(s_hdr_name);
                        Log.d(TAG, "\t\t" + s_hdr_name + ": " + s_hdr_val);
                    }
                }
                call__http_get = httpClient.newCall(http_request_file_download);
                Log.d(TAG, "request_file_download: new OkHttpClient Call (for " + http_request_file_download.toString() + ") created: " + http_request_file_download.toString() + "; executing...");
                Response response = null;
                try {
                    response = call__http_get.execute();
                    Log.d(TAG, "request_file_download: executed OkHttpClient Call (" + call__http_get.toString() + "); handling response...");
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected Response (to " + http_request_file_download.toString() + "): " + response.toString());
                    else {
                        Log.d(TAG, "request_file_download: Response (to " + http_request_file_download.toString() + "): " + response.toString());
                        Headers response_headers = response.headers();
                        if (response_headers != null && response_headers.size() > 0) {
                            //DEBUG - remove for release (unnecessary iteration for release builds)
                            Log.d(TAG, "\tHeaders:");
                            for (int i = 0; i < response_headers.size(); i++) {
                                String s_hdr_name = response_headers.name(i);
                                String s_hdr_val = response_headers.get(s_hdr_name);
                                Log.d(TAG, "\t\t" + s_hdr_name + ": " + s_hdr_val);
                            }
                        }
                        download_file(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
                finally {
                    if (call__http_get != null)
                        call__http_get.cancel();
                    if (response != null) {
                        Log.d(TAG, "request_file_download: closing response (to " + http_request_file_download.toString() + ")");
                        response.body().source().buffer().close();
                        response.body().close();
                        response.close();
                    } else {
                        Log.d(TAG, "request_file_download: cannot close response (to " + http_request_file_download.toString() + ") as response is null");
                    }
                }
            }

            @Override
            protected Exception doInBackground(final HttpUrl_To_Local_File[] httpUrl_to_local_file) {
                Exception exception = null;

                OkHttpClient httpClient = null;
                try {
                    if (httpUrl_to_local_file == null || httpUrl_to_local_file[0] == null)
                        throw new RemoteFileInvalidParameterException("HttpUrl_To_Local_File is null");
                    if (httpUrl_to_local_file[0].get_url() == null)
                        throw new RemoteFileInvalidParameterException("HttpUrl_To_Local_File.url is null");
                    if (httpUrl_to_local_file[0].get_file() == null)
                        throw new RemoteFileInvalidParameterException("HttpUrl_To_Local_File.file is null");
                    httpClient = newDefaultHttpClientBuilder()
                            .followRedirects(true)
                            //network interceptor not currently needed - only application interceptor
//                            .addNetworkInterceptor(new Interceptor() {
//                                @Override public Response intercept(Chain chain) throws IOException {
//                                    Response originalResponse = chain.proceed(chain.request());
//                                    return originalResponse.newBuilder()
//                                            .body(new ChunkedResponseBody(originalResponse.body(), m_httpurl_to_local_file[0], m_asyncgetfiletask_stage_handler))
//                                            .build();
//                                }
//                            })
                            .addInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Response originalResponse = chain.proceed(chain.request());
                                    return originalResponse.newBuilder()
                                            .body(new ChunkedResponseBody(originalResponse.body(), httpUrl_to_local_file[0], m_asyncgetfiletask_stage_handler))
                                            .build();
                                }
                            })
                            .build();
                    //Log.d(TAG, "doInBackground: new OkHttpClient created");
                    HttpUrl http_url = httpUrl_to_local_file[0].get_url();
                    //request_file_size(httpClient, http_url);
                    request_file_download(httpClient, http_url);
                } catch (IOException e) {
                    exception = e;
                } catch (RemoteFileInvalidParameterException e) {
                    exception = e;
//                    } catch (RemoteFile_SizeException e) {
//                        e.printStackTrace();
                } finally {
                    if (httpClient != null)
                        httpClient.dispatcher().cancelAll();
                }
                return exception;
            }

            @Override
            protected void onCancelled(Exception exception) {
                m_asyncgetfiletask_stage_handler.onCancelled(exception);
            }

            @Override
            protected void onPostExecute(Exception exception) {
                m_asyncgetfiletask_stage_handler.onPostExecute(exception);
            }
        }

        public static class CallableTaskException extends Exception {
            final HttpUrl_To_Local_File url_to_local_file;
            final public HttpUrl_To_Local_File get_httpUrl_to_local_file() {
                return url_to_local_file;
            }

            public CallableTaskException(@NonNull final Exception e, @NonNull final HttpUrl_To_Local_File url_to_local_file) {
                super(e);
                this.url_to_local_file = url_to_local_file;
            }
        }

        public static class CallableTask extends Task implements Callable<HttpUrl_To_Local_File> {
            final HttpUrl_To_Local_File url_to_local_file;
            final public HttpUrl_To_Local_File get_httpUrl_to_local_file() {
                return url_to_local_file;
            }

            public CallableTask(@NonNull final HttpUrl_To_Local_File url_to_local_file, @NonNull final TaskStageHandler asyncgetfiletask_stage_handler) {
                super(asyncgetfiletask_stage_handler);
                this.url_to_local_file = url_to_local_file;
                asyncgetfiletask_stage_handler.set_httpUrl_to_local_file(url_to_local_file);
            }

            @Override
            public HttpUrl_To_Local_File call() throws Exception {
                this.onPreExecute();
                Exception e = this.doInBackground(new HttpUrl_To_Local_File[]{url_to_local_file});
                this.onPostExecute(e);
                if (e != null)
                    throw new CallableTaskException(e, url_to_local_file);
                return url_to_local_file;
            }
        }

        public static class RemoteFileInvalidParameterException extends Exception {
            public RemoteFileInvalidParameterException(String message) {
                super(message);
            }
        }

        public static class RemoteFile_SizeException extends Exception {
            private final HttpUrl m_httpUrl;
            public final HttpUrl get_httpurl() {
                return m_httpUrl;
            }
            public RemoteFile_SizeException(final HttpUrl httpUrl) {
                m_httpUrl = httpUrl;
            }
            public RemoteFile_SizeException(final HttpUrl httpUrl, final String msg) {
                super(msg);
                m_httpUrl = httpUrl;
            }
        }

        public static class StageHandlerOnChunkRead_LocalFileAlreadyExistsException extends IOException {
            private final HttpUrl_To_Local_File m_httpUrl_to_local_file;
            public final HttpUrl_To_Local_File get_httpUrl_to_local_file() {
                return m_httpUrl_to_local_file;
            }
            public StageHandlerOnChunkRead_LocalFileAlreadyExistsException(final HttpUrl_To_Local_File httpUrl_to_local_file) {
                m_httpUrl_to_local_file = httpUrl_to_local_file;
            }
            public StageHandlerOnChunkRead_LocalFileAlreadyExistsException(final HttpUrl_To_Local_File httpUrl_to_local_file, final String msg) {
                super(msg);
                m_httpUrl_to_local_file = httpUrl_to_local_file;
            }
        }

        public static class StageHandlerOnChunkRead_LocalFileCreateException extends IOException {
            private final HttpUrl_To_Local_File m_httpUrl_to_local_file;
            public final HttpUrl_To_Local_File get_httpUrl_to_local_file() {
                return m_httpUrl_to_local_file;
            }
            public StageHandlerOnChunkRead_LocalFileCreateException(final HttpUrl_To_Local_File httpUrl_to_local_file) {
                m_httpUrl_to_local_file = httpUrl_to_local_file;
            }
            public StageHandlerOnChunkRead_LocalFileCreateException(final HttpUrl_To_Local_File httpUrl_to_local_file, final String msg) {
                super(msg);
                m_httpUrl_to_local_file = httpUrl_to_local_file;
            }
        }

        public static class StageHandlerOnChunkRead_GeneralIOException extends IOException {
            private final HttpUrl_To_Local_File m_httpUrl_to_local_file;
            public final HttpUrl_To_Local_File get_httpUrl_to_local_file() {
                return m_httpUrl_to_local_file;
            }
            public StageHandlerOnChunkRead_GeneralIOException(final HttpUrl_To_Local_File httpUrl_to_local_file) {
                m_httpUrl_to_local_file = httpUrl_to_local_file;
            }
            public StageHandlerOnChunkRead_GeneralIOException(final HttpUrl_To_Local_File httpUrl_to_local_file, final String msg) {
                super(msg);
                m_httpUrl_to_local_file = httpUrl_to_local_file;
            }
        }

        public static abstract class TaskStageHandler {
            private final static String TAG = "Utils.HTTP.AsyncGet" + TaskStageHandler.class.getSimpleName();

            private HttpUrl_To_Local_File m_httpUrl_to_local_file = null;
            private Task m_asyncgetfiletask = null;
            private final Object m_asyncgetfiletask_sync_target = new Object();

            public void set_asyncgetfiletask(final Task asyncgetfiletask) {
                synchronized (m_asyncgetfiletask_sync_target) {
                    m_asyncgetfiletask =  asyncgetfiletask;
                }
            }
            public Task get_asyncgetfiletask() {
                synchronized (m_asyncgetfiletask_sync_target) {
                    return m_asyncgetfiletask;
                }
            }

            public void set_httpUrl_to_local_file(@NonNull final AsyncGet.HttpUrl_To_Local_File httpUrl_to_local_file) {
                m_httpUrl_to_local_file = httpUrl_to_local_file;
            }
            public final HttpUrl_To_Local_File get_httpUrl_to_local_file() {
                return m_httpUrl_to_local_file;
            }

            public abstract void onPreExecute();
            public abstract void onChunkRead(Buffer sink, long bytesRead, long contentLength, boolean done) throws IOException;
            public abstract void onCancelled(Exception exception);
            public abstract void onPostExecute(Exception exception);
        }

        public static class ExecutorService extends ThreadPoolExecutor {
            private final String TAG = ExecutorService.class.getSimpleName();

            private final LinkedBlockingQueue<Future<?>> observableDownloadQueue;

            public static class DownloadQueueObserver {
                protected String getTag() {
                    return DownloadQueueObserver.class.getSimpleName();
                }

                private final Object queue_empty_monitor = new Object();
                public final void waitUntilAllDownloadsFinish() {
                    synchronized (queue_empty_monitor) {
                        try {
                            queue_empty_monitor.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                private LinkedBlockingQueue<HttpUrl_To_Local_File> successful_downloads = new LinkedBlockingQueue<>();
                protected LinkedBlockingQueue<HttpUrl_To_Local_File> getSuccessfulDownloads() {
                    return successful_downloads;
                }

                private LinkedBlockingQueue<CallableTaskException> failed_download_exceptions = new LinkedBlockingQueue<>();
                protected LinkedBlockingQueue<CallableTaskException> getFailedDownloadExceptions() {
                    return failed_download_exceptions;
                }

                public void onDownloadQueued(HttpUrl_To_Local_File httpUrl_to_local_file, int n_pending) {
                    String url = httpUrl_to_local_file.get_url().toString();
                    File f = httpUrl_to_local_file.get_file();
                    String file_path = null;
                    try {
                        file_path = f.getCanonicalPath();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(
                        getTag(),
                        String.format(
                            "onDownloadQueued: new download (task) of %s to local file %s has been queued - %d download tasks pending in queue",
                            url,
                            file_path,
                            n_pending
                        )
                    );
                }

                public void onDownloadStarted(HttpUrl_To_Local_File httpUrl_to_local_file) {
                    String url = httpUrl_to_local_file.get_url().toString();
                    File f = httpUrl_to_local_file.get_file();
                    String file_path = null;
                    try {
                        file_path = f.getCanonicalPath();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(
                        getTag(),
                        String.format(
                            "onDownloadStarted: queued (task) download of %s to local file %s has started",
                            url,
                            file_path
                        )
                    );
                }

                public void onDownloaded(HttpUrl_To_Local_File httpUrl_to_local_file, int n_pending) {
                    String url = httpUrl_to_local_file.get_url().toString();
                    File f = httpUrl_to_local_file.get_file();
                    String file_path = null;
                    try {
                        file_path = f.getCanonicalPath();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    boolean file_exists = f.exists();
                    Log.d(
                        getTag(),
                        String.format(
                            "onDownloaded: queued (task) download of %s to local file %s is COMPLETE - local file exists: %b; %d download tasks pending in queue",
                            url,
                            file_path,
                            file_exists,
                            n_pending
                        )
                    );
                    successful_downloads.add(httpUrl_to_local_file);
                }

                public void onDownloadFailed(CallableTaskException e, int n_pending) {
                    String
                        s_url = "<failed to retrieve remote file url>",
                        s_local_file = "<failed to retrieve local file path>";
                    try {
                        s_url = e.get_httpUrl_to_local_file().get_url().toString();
                        s_local_file = e.get_httpUrl_to_local_file().get_file().getCanonicalPath();
                    } catch (IOException e1) {}
                    Log.d(
                        getTag(),
                        String.format(
                            "onDownloadFailed: queued (task) download %s to %s failed with error: %s",
                            s_url,
                            s_local_file,
                            e.getMessage(),
                            n_pending
                        )
                    );
                    failed_download_exceptions.add(e);
                }

                public void onAllDownloadsFinished() {
                    successful_downloads.clear();
                    failed_download_exceptions.clear();

                    synchronized (queue_empty_monitor) {
                        queue_empty_monitor.notifyAll();
                    }
                }
            }
            private final DownloadQueueObserver downloadQueueObserver;

            public ExecutorService(@NonNull final RejectedExecutionHandler handler, final DownloadQueueObserver downloadQueueObserver) {
                super(1, 1, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), handler);
                this.downloadQueueObserver = downloadQueueObserver;
                observableDownloadQueue = (downloadQueueObserver != null ? new LinkedBlockingQueue<>() : null);
            }
            public ExecutorService(@NonNull final RejectedExecutionHandler handler) {
                this(handler, null);
            }

            @Override
            public boolean prestartCoreThread() {
                Log.d(TAG, String.format("prestartCoreThread"));
                return super.prestartCoreThread();
            }

            @Override
            public int prestartAllCoreThreads() {
                Log.d(TAG, String.format("prestartAllCoreThreads"));
                return super.prestartAllCoreThreads();
            }

            @NonNull
            @Override
            public <T> Future<T> submit(@NonNull Callable<T> callable) {
                Log.d(TAG, String.format("submit - %s", callable.getClass().getCanonicalName()));
                if (!(callable instanceof CallableTask))
                    throw new ClassCastException(
                        String.format(
                            "%s is incompatible with %s - arg to submit() must be type %s",
                            ExecutorService.class.getCanonicalName(),
                            callable.getClass().getCanonicalName(),
                            CallableTask.class.getCanonicalName()
                        )
                    );
                Future<T> future = null;
                if (downloadQueueObserver != null) {
                    observableDownloadQueue.add(super.submit(callable));
                    downloadQueueObserver.onDownloadQueued(((CallableTask)callable).get_httpUrl_to_local_file(), observableDownloadQueue.size());
                } else
                    future = super.submit(callable);
                return future;
            }

            @NonNull
            @Override
            public <T> Future<T> submit(@NonNull Runnable runnable, T t) {  //only support FutureTask wrapping CallableTask
                Log.d(TAG, String.format("submit - %s", runnable.getClass().getCanonicalName()));
                CallableTask callableTask = null;
                if (!(runnable instanceof FutureTask)) {
                    throw new ClassCastException(
                        String.format(
                            "%s is incompatible with %s - arg to submit() must be an instance of %s (wrapping %s)",
                            ExecutorService.class.getCanonicalName(),
                            runnable.getClass().getCanonicalName(),
                            FutureTask.class.getCanonicalName(),
                            CallableTask.class.getCanonicalName()
                        )
                    );
                } else {
                    FutureTask ft = (FutureTask)runnable;
                    Field f = null;
                    try {
                        f = ft.getClass().getDeclaredField("callable");
                        f.setAccessible(true);
                        try {callableTask = (CallableTask)f.get(ft);} catch (IllegalAccessException e0) {}
                    } catch (NoSuchFieldException e1) {}
                }
                if (callableTask == null) {
                    throw new ClassCastException(
                        String.format(
                            "%s must wrap %s instance but does not",
                            FutureTask.class.getCanonicalName(),
                            CallableTask.class.getCanonicalName()
                        )
                    );
                } else {
                    Log.d(TAG, String.format("submit - instance of %s", callableTask.getClass().getCanonicalName()));
                }
                Future<T> future = null;
                if (downloadQueueObserver != null) {
                    observableDownloadQueue.add(super.submit(runnable, t));
                    downloadQueueObserver.onDownloadQueued(callableTask.get_httpUrl_to_local_file(), observableDownloadQueue.size());
                } else
                    future = super.submit(runnable, t);
                return future;
            }

            @NonNull
            @Override
            public Future<?> submit(@NonNull Runnable runnable) {
                Log.d(TAG, String.format("submit - %s", runnable.getClass().getCanonicalName()));
                CallableTask callableTask = null;
                if (!(runnable instanceof FutureTask)) {
                    throw new ClassCastException(
                        String.format(
                            "%s is incompatible with %s - arg to submit() must be an instance of %s (wrapping %s)",
                            ExecutorService.class.getCanonicalName(),
                            runnable.getClass().getCanonicalName(),
                            FutureTask.class.getCanonicalName(),
                            CallableTask.class.getCanonicalName()
                        )
                    );
                } else {
                    FutureTask ft = (FutureTask)runnable;
                    Field f = null;
                    try {
                        f = ft.getClass().getDeclaredField("callable");
                        f.setAccessible(true);
                        try {callableTask = (CallableTask)f.get(ft);} catch (IllegalAccessException e0) {}
                    } catch (NoSuchFieldException e1) {}
                }
                if (callableTask == null) {
                    throw new ClassCastException(
                        String.format(
                            "%s must wrap %s instance but does not",
                            FutureTask.class.getCanonicalName(),
                            CallableTask.class.getCanonicalName()
                        )
                    );
                } else {
                    Log.d(TAG, String.format("submit - instance of %s", callableTask.getClass().getCanonicalName()));
                }
                Future<?> future = null;
                if (downloadQueueObserver != null) {
                    observableDownloadQueue.add(super.submit(runnable));
                    downloadQueueObserver.onDownloadQueued(callableTask.get_httpUrl_to_local_file(), observableDownloadQueue.size());
                } else
                    future = super.submit(runnable);
                return future;
            }

            @Override
            public void execute(@NonNull Runnable runnable) {
                CallableTask callableTask = null;
                if (!(runnable instanceof FutureTask)) {
                    throw new ClassCastException(
                        String.format(
                            "%s is incompatible with %s - arg to submit() must be an instance of %s (wrapping %s)",
                            ExecutorService.class.getCanonicalName(),
                            runnable.getClass().getCanonicalName(),
                            FutureTask.class.getCanonicalName(),
                            CallableTask.class.getCanonicalName()
                        )
                    );
                } else {
                    FutureTask ft = (FutureTask)runnable;
                    Field f = null;
                    try {
                        f = ft.getClass().getDeclaredField("callable");
                        f.setAccessible(true);
                        try {callableTask = (CallableTask)f.get(ft);} catch (IllegalAccessException e0) {}
                    } catch (NoSuchFieldException e1) {}
                }
                if (callableTask == null) {
                    throw new ClassCastException(
                        String.format(
                            "%s must wrap %s instance but does not",
                            FutureTask.class.getCanonicalName(),
                            CallableTask.class.getCanonicalName()
                        )
                    );
                } else {
                    Log.d(TAG, String.format("execute - instance of %s", callableTask.getClass().getCanonicalName()));
                }
                super.execute(runnable);
            }

            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                CallableTask callableTask = null;
                if (!(r instanceof FutureTask)) {
                    throw new ClassCastException(
                        String.format(
                            "%s is incompatible with %s - arg to submit() must be an instance of %s (wrapping %s)",
                            ExecutorService.class.getCanonicalName(),
                            r.getClass().getCanonicalName(),
                            FutureTask.class.getCanonicalName(),
                            CallableTask.class.getCanonicalName()
                        )
                    );
                } else {
                    FutureTask ft = (FutureTask)r;
                    Field f = null;
                    try {
                        f = ft.getClass().getDeclaredField("callable");
                        f.setAccessible(true);
                        try {callableTask = (CallableTask)f.get(ft);} catch (IllegalAccessException e0) {}
                    } catch (NoSuchFieldException e1) {}
                }
                if (callableTask == null) {
                    throw new ClassCastException(
                        String.format(
                            "%s must wrap %s instance but does not",
                            FutureTask.class.getCanonicalName(),
                            CallableTask.class.getCanonicalName()
                        )
                    );
                } else {
//                    Log.d(TAG, String.format("beforeExecute - instance of %s", callableTask.getClass().getCanonicalName()));
                }
                if (downloadQueueObserver != null)
                    downloadQueueObserver.onDownloadStarted(callableTask.get_httpUrl_to_local_file());
                super.beforeExecute(t, r);
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (downloadQueueObserver != null) {
                    Future<?> queued_download = observableDownloadQueue.poll();
                    int n_pending = observableDownloadQueue.size();
                    try {
                        HttpUrl_To_Local_File url_to_local_file = (HttpUrl_To_Local_File)queued_download.get();
                        downloadQueueObserver.onDownloaded(url_to_local_file, n_pending);
                    } catch (InterruptedException e) {
                        downloadQueueObserver.onDownloadFailed((CallableTaskException)e.getCause(), n_pending);
                    } catch (ExecutionException e) {
                        downloadQueueObserver.onDownloadFailed((CallableTaskException)e.getCause(), n_pending);
                    } finally {
                        if (n_pending == 0)
                            downloadQueueObserver.onAllDownloadsFinished();
                    }
                }
            }

            @Override
            protected void terminated() {
                Log.d(TAG, String.format("terminated"));
                super.terminated();
            }
        }

        public static class TaskExecuteQueueItem {
            private final TaskExecuteQueueItemExecutor m_executor;
            final public TaskExecuteQueueItemExecutor get_executor() {
                return m_executor;
            }
            private final HttpUrl_To_Local_File m_httpurl_to_local_file;
            final public HttpUrl_To_Local_File get_httpUrl_to_local_file() {
                return m_httpurl_to_local_file;
            }

            public TaskExecuteQueueItem(@NonNull final AsyncGet.TaskExecuteQueueItemExecutor executor, @NonNull final AsyncGet.HttpUrl_To_Local_File httpurl_to_local_file) {
                m_executor = executor;
                m_httpurl_to_local_file = httpurl_to_local_file;
            }
        }

        public static class TaskExecuteQueueException extends Exception {
            public TaskExecuteQueueException(String message) {
                super(message);
            }
        }

        public static abstract class TaskExecuteQueueListener {
            private final LinkedHashMap<TaskExecuteQueueItemExecutor, Exception> item_excutor_exception_map = new LinkedHashMap<TaskExecuteQueueItemExecutor, Exception>();

            public abstract void onPreExecute();
                public abstract void onItemExecutor_PreExecute(final TaskExecuteQueueItemExecutor executor);
                public abstract void onItemExecutor_PostExecute(final TaskExecuteQueueItemExecutor executor);
                public abstract void onItemExecutor_Cancelled(final TaskExecuteQueueItemExecutor executor);
            public abstract void onCancelled();
            public abstract void onPostExecute(final LinkedHashMap<TaskExecuteQueueItemExecutor, Exception> item_excutor_exception_map);
        }

        public static class TaskExecuteQueue extends LinkedBlockingQueue<TaskExecuteQueueItem> {
            private final TaskExecuteQueueListener m_listener;
            private int m_n_pending = 0;

            public TaskExecuteQueue() {
                m_listener = null;
                m_n_pending = 0;
            }
            public TaskExecuteQueue(final TaskExecuteQueueListener listener) {
                m_listener = listener;
                m_n_pending = 0;
            }

            @Override
            public boolean add(TaskExecuteQueueItem asyncGetFileTaskExecuteQueueItem) {
                final boolean b_added = super.add(asyncGetFileTaskExecuteQueueItem);
                if (b_added)
                    ++m_n_pending;
                return b_added;
            }

            public void execute() throws TaskExecuteQueueException {
                if (isEmpty())
                    throw new TaskExecuteQueueException("queue is empty");

                if (m_listener != null)
                    m_listener.onPreExecute();

                Iterator<TaskExecuteQueueItem> iterator_exec_queue_items = iterator();
                while (iterator_exec_queue_items.hasNext()) {
                    TaskExecuteQueueItem exec_queue_item = iterator_exec_queue_items.next();
                    exec_queue_item.get_executor().execute(exec_queue_item.get_httpUrl_to_local_file());
                }
            }
        }

        public static class TaskExecuteQueueItemExecutor extends Task {
            private final static String TAG = "Utils.HTTP.AsyncGet" + TaskExecuteQueueItemExecutor.class.getSimpleName();

            private final TaskExecuteQueue m_queue;

            public TaskExecuteQueueItemExecutor(@NonNull AsyncGet.TaskStageHandler stage_handler, @NonNull AsyncGet.TaskExecuteQueue queue) {
                super(stage_handler);
                m_queue = queue;
            }

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onCancelled(Exception exception) {
                Log.d(TAG, "onCancelled: exception is: " + (exception != null ? exception.getClass().getSimpleName() : "<no exception>") +"; calling super.onCancelled(exception)...");
                super.onCancelled(exception);
                --m_queue.m_n_pending;
                if (m_queue.m_listener != null) {
                    if (exception != null) {
                        Log.d(TAG, "onCancelled: adding exception " + exception.getClass().getSimpleName() + " for this executor to m_queue.m_listener.item_excutor_exception_map...");
                        m_queue.m_listener.item_excutor_exception_map.put(this, exception);
                    }
                    Log.d(TAG, "onCancelled: canceling http get request (" + get_http_get_call().request().toString() + ")");
                    get_http_get_call().cancel();
                    m_queue.m_listener.onItemExecutor_Cancelled(this);
                }
                if (m_queue.m_n_pending == 0) {
                    if (m_queue.m_listener != null) {
                        Log.d(TAG, "onCancelled: calling m_queue.m_listener.onPostExecute() w/ " + (m_queue.m_listener.item_excutor_exception_map.size() > 0 ? "non-" : "") + "empty m_queue.m_listener.item_excutor_exception_map");
                        m_queue.m_listener.onPostExecute(m_queue.m_listener.item_excutor_exception_map.size() > 0 ? m_queue.m_listener.item_excutor_exception_map : null);
                        m_queue.m_listener.item_excutor_exception_map.clear();
                    }
                    m_queue.clear();
                }
            }

            @Override
            protected void onPostExecute(Exception exception) {
                Log.d(TAG, "onPostExecute: exception: " + (exception != null ? exception.getClass().getSimpleName() : "<no exception>") +"; calling super.onPostExecute(exception)...");
                super.onPostExecute(exception);
                --m_queue.m_n_pending;
                if (m_queue.m_listener != null) {
                    if (exception != null) {
                        Log.d(TAG, "onPostExecute: adding exception " + exception.getClass().getSimpleName() + " for this executor to m_queue.m_listener.item_excutor_exception_map...");
                        m_queue.m_listener.item_excutor_exception_map.put(this, exception);
                    }
                    Log.d(TAG, "onPostExecute: canceling http get request (" + get_http_get_call().request().toString() + ")");
                    get_http_get_call().cancel();
                    m_queue.m_listener.onItemExecutor_PostExecute(this);
                }
                if (m_queue.m_n_pending == 0) {
                    if (m_queue.m_listener != null) {
                        Log.d(TAG, "onPostExecute: calling m_queue.m_listener.onPostExecute() w/ " + (m_queue.m_listener.item_excutor_exception_map.size() > 0 ? "non-" : "") + "empty m_queue.m_listener.item_excutor_exception_map");
                        m_queue.m_listener.onPostExecute(m_queue.m_listener.item_excutor_exception_map.size() > 0 ? m_queue.m_listener.item_excutor_exception_map : null);
                        m_queue.m_listener.item_excutor_exception_map.clear();
                    }
                    m_queue.clear();
                }
            }
        }
    }
}
