package audiogen.arxiv.fetch;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RetryStrategy implements ServiceUnavailableRetryStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(RetryStrategy.class);

    // The default gap between retry requests, in milliseconds
    private static final int DEFAULT_RETRY_GAP_MILLIS = 5000;

    int retryGapMillis;

    RetryStrategy() {
        retryGapMillis = DEFAULT_RETRY_GAP_MILLIS;
    }

    @Override
    public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
        LOG.info("Checking if request should be retried");
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();

        // If Service Unavailable but not able to get a Retry-After, then bail
        if (statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE) {
            if (!setWaitPeriod(response, statusLine)) {
                return false;
            }
        }

        // Retry up to 3 times in all other cases
        return executionCount <= 3 && statusCode >= 500;
    }

    @Override
    public long getRetryInterval() {
        return retryGapMillis;
    }

    private boolean setWaitPeriod(HttpResponse response, StatusLine statusLine) {
        String reasonPhrase = statusLine.getReasonPhrase();

        LOG.info("Response status was Service Unavailable. Reason: " + reasonPhrase);
        if (!reasonPhrase.contains("Retry")) {
            LOG.warn("Not setting wait period because no Retry mentioned in Reason string");
            return false;
        }

        Header[] headers = response.getHeaders("Retry-After");
        if (headers.length > 1) {
            LOG.warn("Multiple Retry-After headers found: (" + headers.length + "). " +
                    "Going with the first one");
        }
        LOG.info("Retry-After header: " + headers[0]);

        retryGapMillis = getHeaderValueAsMillis(headers[0]);

        return true;
    }

    private int getHeaderValueAsMillis(Header header) {
        String retryStr = header.getValue().trim();
        return 1000 * Integer.parseInt(retryStr);
    }
}
