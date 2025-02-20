package audiogen.arxiv.fetch;

import audiogen.arxiv.parse.ParseArxivRaw;
import audiogen.arxiv.records.ListRecords;
import audiogen.arxiv.records.OaiFeed;
import io.bitken.tts.model.entity.ArxivOai;
import io.bitken.tts.repo.ArxivOaiRepo;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

@Component
public class ArxivOaiFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(ArxivOaiFetcher.class);

    private static final String BASE_URL = "http://export.arxiv.org/oai2";

    @Autowired
    private ArxivOaiRepo arxivOaiRepo;

    private final ParseArxivRaw parser;

    public ArxivOaiFetcher() {
        parser = new ParseArxivRaw();
    }

    public void fetch() throws IOException, ParseException {
        try (CloseableHttpClient httpclient = createHttpClient()) {
            FetchParams fp = new FetchParams(arxivOaiRepo, parser);
            SavedFetch sf = SavedFetch.empty();
            fetchNewData(httpclient, fp, sf);
        }
    }

    private void fetchNewData(CloseableHttpClient httpclient, FetchParams fp, SavedFetch sf) throws IOException {
        fetchAndStore(httpclient, fp, sf);

        if (sf.getXmlOpt().isEmpty() || sf.getBatchIdOpt().isEmpty()) {
            return;
        }

        keepFetchingTillTheEnd(httpclient, fp, sf);
    }

    private CloseableHttpClient createHttpClient() {
        return HttpClientBuilder.create()
                .setServiceUnavailableRetryStrategy(new RetryStrategy())
                .build();
    }

    private void keepFetchingTillTheEnd(CloseableHttpClient httpclient, FetchParams fp, SavedFetch sf)
            throws IOException {

        Optional<String> resumptionTokenOpt = getResumptionToken(sf.getXmlOpt().get());

        while (resumptionTokenOpt.isPresent()) {
            String resumptionToken = resumptionTokenOpt.get();
            fp.setResumptionToken(resumptionToken);

            LOG.info("Requesting with resumption token: " + resumptionToken);
            fetchAndStore(httpclient, fp, sf);
            LOG.info("Finished storing for resumption token: " + resumptionToken);

            if (sf.getXmlOpt().isEmpty()) {
                LOG.warn("No XML data in saved fetch. So unable to find resumption token: " + resumptionToken);
                return;
            }

            resumptionTokenOpt = getResumptionToken(sf.getXmlOpt().get());
        }
    }

    private Optional<String> getResumptionToken(String xml) throws IOException {
        OaiFeed feed = parser.parse(xml);

        ListRecords listRecords = feed.getListRecords();
        if (listRecords == null) {
            return Optional.empty();
        }

        String token = listRecords.getResumptionToken();
        if (token == null || token.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(token);
    }

    private void fetchAndStore(CloseableHttpClient httpclient, FetchParams fp, SavedFetch sf) {
        String url = createUrl(BASE_URL, fp);

        Optional<String> xmlOpt = fetch(url, httpclient);
        if (xmlOpt.isEmpty()) {
            LOG.error("Couldn't fetch OAI XML");
            return;
        }
        LOG.info("Finished fetching OAI XML");

        saveOaiData(xmlOpt.get(), url, fp, sf);
        LOG.info("Finished storing raw OAI XML");
    }

    private String createUrl(String baseUrl, FetchParams fp) {
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);

        addQueryParams(sb, fp);

        return sb.toString();
    }

    private void addQueryParams(StringBuilder sb, FetchParams fp) {
        Iterator<Map.Entry<String, String>> iter = fp.asMap().entrySet().iterator();

        if (!iter.hasNext()) {
            return;
        }

        sb.append("?");
        while (iter.hasNext()) {
            Map.Entry<String, String> kv = iter.next();
            sb.append(kv.getKey() + "=" + URLEncoder.encode(kv.getValue(), StandardCharsets.UTF_8));

            if (iter.hasNext()) {
                sb.append("&");
            }
        }
    }

    private Optional<String> fetch(String url, CloseableHttpClient client) {
        HttpGet httpGet = new HttpGet(url);

        try (CloseableHttpResponse response1 = client.execute(httpGet)) {
            return Optional.of(
                StreamUtils.copyToString(response1.getEntity().getContent(), StandardCharsets.UTF_8)
            );
        } catch (IOException e) {
            LOG.error("Error fetching url:" + url, e);
            return Optional.empty();
        }
    }

    private void saveOaiData(String oaiXml, String url, FetchParams fp, SavedFetch sf) {
        if (sf.getBatchIdOpt().isEmpty()) {
            sf.setBatchIdOpt(Optional.of(System.currentTimeMillis()));
        }

        ArxivOai arxivOai = new ArxivOai();
        arxivOai.setOaiXml(oaiXml);
        arxivOai.setBatchId(sf.getBatchIdOpt().get());

        ArxivOai.Source src = new ArxivOai.Source();
        src.setUrl(url);
        src.setParams(fp.asMap());
        arxivOai.setSrc(src);

        arxivOaiRepo.save(arxivOai);

        sf.setXmlOpt(Optional.of(oaiXml));
    }

}
