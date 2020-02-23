package app.relayer;

import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Request;
import com.aspectran.core.component.bean.annotation.Required;
import com.aspectran.core.util.apon.AponReader;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;
import com.aspectran.core.util.security.InvalidPBTokenException;
import com.aspectran.core.util.security.TimeLimitedPBTokenIssuer;
import com.aspectran.web.activity.response.DefaultRestResponse;
import com.aspectran.web.activity.response.RestResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <p>Created: 2020/02/23</p>
 */
@Component
public class EndpointManager {

    private static final Log log = LogFactory.getLog(EndpointManager.class);

    @Request("/endpoints/${token}")
    public RestResponse getEndpoints(Translet translet, @Required String token) throws IOException {
        try {
            TimeLimitedPBTokenIssuer.validate(token);
        } catch (InvalidPBTokenException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage(), e);
            }
            return new DefaultRestResponse().forbidden();
        }
        File file = translet.getApplicationAdapter().toRealPathAsFile("/config/endpoint-config.apon");
        EndpointConfig endpointConfig = new EndpointConfig();
        AponReader.parse(file, endpointConfig);
        List<EndpointInfo> endpointInfoList = endpointConfig.getEndpointInfoList();
        for (EndpointInfo endpointInfo : endpointInfoList) {
            String url = endpointInfo.getUrl();
            if (!url.endsWith("/")) {
                url += "/";
            }
            url += TimeLimitedPBTokenIssuer.getToken();
            endpointInfo.setUrl(url);
        }
        return new DefaultRestResponse(endpointInfoList).ok();
    }

}
